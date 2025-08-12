plugins {
    id("java")
    id("org.springframework.boot") version "3.4.1" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "io.thatworked.network-ping-monitor"
    version = "0.0.1-SNAPSHOT"
    
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    
    // Apply Spring dependency management to all subprojects that need it
    if (name != "support-common" && name != "support-api-gateway") {
        apply(plugin = "io.spring.dependency-management")
    }
    
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }
    
    dependencies {
        // Common test dependencies for all modules
        "testImplementation"("org.junit.jupiter:junit-jupiter")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }
    
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}