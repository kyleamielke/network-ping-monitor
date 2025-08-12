plugins {
    `java-library`
    id("org.springframework.boot") version "3.4.1" apply false
    id("io.spring.dependency-management") version "1.1.7"
}

group = "io.thatworked.support"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.0")
    }
}

dependencies {
    // Spring Boot dependencies (optional since this is a library)
    compileOnly("org.springframework.boot:spring-boot-starter")
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    
    // Logging dependencies
    implementation("org.slf4j:slf4j-api")
    implementation("ch.qos.logback:logback-classic")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    
    // Kafka support (optional)
    compileOnly("org.springframework.kafka:spring-kafka")
    
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    
    // Jackson for JSON
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    
    // Jakarta Validation for annotations (optional - services can opt in)
    compileOnly("jakarta.validation:jakarta.validation-api")
    
    // MDC support
    implementation("org.slf4j:slf4j-ext")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core")
}

tasks.test {
    useJUnitPlatform()
}

// Create a normal jar for library
tasks.jar {
    enabled = true
    archiveBaseName.set("common")
}