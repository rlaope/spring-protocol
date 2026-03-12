# 시작하기

## 개요

Spring Protocol은 Spring Boot 기반의 선언적 클라이언트 프레임워크입니다. gRPC, REST, GraphQL, RSocket 등 다양한 통신 프로토콜을 통합된 어노테이션 프로그래밍 모델로 제공합니다.

모든 프로토콜이 동일한 두 개의 어노테이션을 공유합니다:
- `@SpringClient` - 인터페이스를 프로토콜 클라이언트로 지정
- `@ProtocolMapping` - 메서드를 프로토콜별 동작에 매핑

## 요구사항

- Java 17 이상
- Spring Boot 3.x
- Gradle 8.x

## 설치

통합 boot starter와 필요한 프로토콜 starter를 추가하세요:

```gradle
dependencies {
    // 항상 필요
    implementation 'io.spring-protocol:spring-protocol-boot:0.1.0-SNAPSHOT'

    // 필요한 프로토콜 선택
    implementation 'io.spring-protocol:grpc-client-spring-boot-starter:0.1.0-SNAPSHOT'
    implementation 'io.spring-protocol:rest-client-spring-boot-starter:0.1.0-SNAPSHOT'
    implementation 'io.spring-protocol:graphql-client-spring-boot-starter:0.1.0-SNAPSHOT'
    implementation 'io.spring-protocol:rsocket-client-spring-boot-starter:0.1.0-SNAPSHOT'
}
```

## 통합 설정

```yaml
spring:
  protocol:
    grpc:
      clients:
        greeter-service:
          address: localhost:9090
    rest:
      clients:
        user-service:
          address: http://localhost:8081
    graphql:
      clients:
        user-service:
          address: http://localhost:8080/graphql
    rsocket:
      clients:
        notification-service:
          address: localhost:7000
```

## 프로젝트 구조

```
spring-protocol/
├── spring-protocol-core/           # 통합 어노테이션 (@SpringClient, @ProtocolMapping), SPI
├── spring-protocol-spring/         # @EnableSpringClients, 빈 등록
├── spring-protocol-boot/           # 자동 구성, ProtocolRegistry
├── spring-protocol-grpc/           # gRPC 클라이언트 구현
├── spring-protocol-rest/           # REST 클라이언트 구현
├── spring-protocol-graphql/        # GraphQL 클라이언트 구현
├── spring-protocol-rsocket/        # RSocket 클라이언트 구현
├── spring-protocol-test/           # 테스트 유틸리티
├── spring-protocol-examples/       # 프로토콜별 사용 예시
└── spring-protocol-docs/           # 문서 (이 모듈)
```

## 동작 원리

1. `@EnableSpringClients`가 클래스패스를 스캔하여 `@SpringClient` 인터페이스를 찾습니다.
2. 각 인터페이스마다 `SpringClientFactoryBean`이 등록됩니다.
3. FactoryBean이 `spring.protocol.{protocol}.clients.{id}.address`에서 연결 정보를 확인합니다.
4. `ProtocolRegistry`가 해당 프로토콜의 `ProtocolClientHandler`에 위임합니다.
5. 핸들러가 프로토콜별 `AbstractClientProxy` 서브클래스를 사용하는 JDK Dynamic Proxy를 생성합니다.
6. 메서드 메타데이터는 `ConcurrentHashMap`에 캐싱됩니다 — 리플렉션은 메서드당 한 번만 발생합니다.
