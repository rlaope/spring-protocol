package io.springprotocol.grpc.spring.registrar;

import io.springprotocol.grpc.core.annotation.GrpcExchange;
import io.springprotocol.grpc.spring.EnableGrpcClients;
import io.springprotocol.grpc.spring.factory.GrpcClientFactoryBean;

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
import java.util.Set;

/**
 * Scans for interfaces annotated with {@code @GrpcExchange} and registers
 * a {@link GrpcClientFactoryBean} bean definition for each one.
 */
public class GrpcClientRegistrar implements ImportBeanDefinitionRegistrar {

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

                    GrpcExchange annotation = interfaceType.getAnnotation(GrpcExchange.class);
                    if (annotation == null) {
                        return;
                    }

                    BeanDefinitionBuilder builder = BeanDefinitionBuilder
                            .genericBeanDefinition(GrpcClientFactoryBean.class);
                    builder.addPropertyValue("interfaceType", interfaceType);
                    builder.addPropertyValue("grpcClass", annotation.grpcClass());
                    builder.addPropertyValue("serviceId", annotation.serviceId());

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
                metadata.getAnnotationAttributes(EnableGrpcClients.class.getName()));

        if (attrs != null) {
            String[] packages = attrs.getStringArray("basePackages");
            if (packages.length > 0) {
                return Set.copyOf(Arrays.asList(packages));
            }
        }

        // Default: package of the annotated class
        return Set.of(ClassUtils.getPackageName(metadata.getClassName()));
    }

    private ClassPathScanningCandidateComponentProvider createScanner() {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(GrpcExchange.class));
        return scanner;
    }
}
