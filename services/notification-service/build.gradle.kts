plugins {
    java
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "io.thatworked.support"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val springCloudVersion = "2024.0.1"
val mapStructVersion = "1.6.3"
val lombokVersion = "1.18.36"

dependencies {
    // Common module
    if (findProject(":common") != null) {
        implementation(project(":common"))
    } else {
        implementation(files("../../common/build/libs/common-0.0.1-SNAPSHOT.jar"))
    }

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Kafka
    implementation("org.springframework.kafka:spring-kafka")
    
    // Spring Cloud
    implementation("org.springframework.cloud:spring-cloud-starter-consul-discovery")
    implementation("org.springframework.cloud:spring-cloud-starter-consul-config")
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")

    // Database
    implementation("org.postgresql:postgresql")
    
    // Utilities
    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    implementation("org.mapstruct:mapstruct:${mapStructVersion}")
    annotationProcessor("org.mapstruct:mapstruct-processor:${mapStructVersion}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named("test").configure {
    enabled = true
}