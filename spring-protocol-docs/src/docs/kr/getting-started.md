# 시작하기

## 개요

Spring Protocol은 Spring Boot 기반의 선언적 클라이언트 프레임워크입니다. gRPC, GraphQL, REST, RSocket 등 다양한 통신 프로토콜을 어노테이션 기반의 통합된 프로그래밍 모델로 제공합니다.

## 요구사항

- Java 17 이상
- Spring Boot 3.x
- Gradle 8.x

## 설치

필요한 프로토콜의 starter 의존성을 추가하세요:

### gRPC Client

```gradle
dependencies {
    implementation 'io.spring-protocol:grpc-client-spring-boot-starter:0.1.0-SNAPSHOT'
}
```

## 프로젝트 구조

```
spring-protocol
├── spring-protocol-grpc/           # gRPC 클라이언트 모듈
│   ├── grpc-client-core            # 어노테이션, 프록시, 스텁 캐싱
│   ├── grpc-client-spring          # Spring 빈 등록
│   └── grpc-client-spring-boot-starter  # 자동 구성
├── spring-protocol-test/           # 테스트 유틸리티
├── spring-protocol-examples/       # 사용 예시
└── spring-protocol-docs/           # 문서 (이 모듈)
```

## 모듈 의존성 그래프

```
spring-boot-starter → grpc-client-spring → grpc-client-core
```

각 모듈의 역할:

- **grpc-client-core**: Spring 의존성 없음. 어노테이션, JDK Dynamic Proxy, 리플렉션 기반 스텁 생성, 채널 관리를 포함합니다.
- **grpc-client-spring**: Spring 통합 계층. `ImportBeanDefinitionRegistrar`를 사용하여 `@GrpcExchange` 인터페이스를 Spring 빈으로 등록합니다.
- **grpc-client-spring-boot-starter**: Spring Boot 자동 구성. `application.yml` 프로퍼티를 읽고 인프라 빈을 연결합니다.
