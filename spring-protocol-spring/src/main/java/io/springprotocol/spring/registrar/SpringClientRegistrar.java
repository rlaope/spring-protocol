package io.springprotocol.spring.registrar;

import io.springprotocol.core.annotation.ProtocolType;
import io.springprotocol.core.annotation.SpringClient;
import io.springprotocol.spring.EnableSpringClients;
import io.springprotocol.spring.factory.SpringClientFactoryBean;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Scans for interfaces annotated with {@code @SpringClient} and registers
 * a {@link SpringClientFactoryBean} bean definition for each one.
 * Also supports legacy {@code @GrpcExchange} for backward compatibility.
 */
public class SpringClientRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {

        Set<String> basePackages = resolveBasePackages(importingClassMetadata);
        ClassPathScanningCandidateComponentProvider scanner = createScanner();

        for (String basePackage : basePackages) {
            scanner.findCandidateComponents(basePackage).forEach(beanDef -> {
                String className = beanDef.getBeanClassName();
                if (className == null) {
                    return;
                }

                try {
                    Class<?> interfaceType = ClassUtils.forName(className,
                            getClass().getClassLoader());

                    ProtocolType protocol = null;
                    String serviceId = null;
                    Map<String, Object> attributes = new HashMap<>();

                    // Check @SpringClient first
                    SpringClient sc = interfaceType.getAnnotation(SpringClient.class);
                    if (sc != null) {
                        protocol = sc.protocol();
                        serviceId = sc.serviceId();
                        if (sc.grpcClass() != void.class) {
                            attributes.put("grpcClass", sc.grpcClass());
                        }
                    }

                    if (protocol == null || serviceId == null) {
                        return;
                    }

                    BeanDefinitionBuilder builder = BeanDefinitionBuilder
                            .genericBeanDefinition(SpringClientFactoryBean.class);
                    builder.addPropertyValue("interfaceType", interfaceType);
                    builder.addPropertyValue("protocol", protocol);
                    builder.addPropertyValue("serviceId", serviceId);
                    builder.addPropertyValue("attributes", attributes);

                    String beanName = StringUtils.uncapitalize(interfaceType.getSimpleName());
                    registry.registerBeanDefinition(beanName, builder.getBeanDefinition());

                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Failed to load class: " + className, e);
                }
            });
        }
    }

    private Set<String> resolveBasePackages(AnnotationMetadata metadata) {
        AnnotationAttributes attrs = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(EnableSpringClients.class.getName()));

        if (attrs != null) {
            String[] packages = attrs.getStringArray("basePackages");
            if (packages.length > 0) {
                return Set.copyOf(Arrays.asList(packages));
            }
        }

        return Set.of(ClassUtils.getPackageName(metadata.getClassName()));
    }

    private ClassPathScanningCandidateComponentProvider createScanner() {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(SpringClient.class));
        return scanner;
    }
}
