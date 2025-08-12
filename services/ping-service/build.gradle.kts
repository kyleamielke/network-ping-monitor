plugins {
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("java")
}

group = "io.thatworked.support"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

val springCloudVersion = "2024.0.1"
val mapStructVersion = "1.6.3"
val lombokVersion = "1.18.36"

dependencies {
    // Common module for standardized logging
    if (findProject(":common") != null) {
        implementation(project(":common"))
    } else {
        implementation(files("../../common/build/libs/common-0.0.1-SNAPSHOT.jar"))
    }
    
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Database
    implementation("org.postgresql:postgresql")
    implementation("com.zaxxer:HikariCP")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // TimescaleDB
    implementation("io.hypersistence:hypersistence-utils-hibernate-62:3.7.0")

    // Spring Cloud
    implementation("org.springframework.cloud:spring-cloud-starter-consul-discovery")
    implementation("org.springframework.cloud:spring-cloud-starter-consul-config")
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")

    // Utilities
    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    implementation("org.mapstruct:mapstruct:${mapStructVersion}")
    annotationProcessor("org.mapstruct:mapstruct-processor:${mapStructVersion}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Kafka
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.apache.kafka:kafka-streams")


    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("io.rest-assured:rest-assured")
    testRuntimeOnly("com.h2database:h2")
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