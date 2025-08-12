package io.thatworked.support.alert.infrastructure.config;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
public class DatabaseInitializer {
    
    private final StructuredLogger logger;

    @Value("${alert-service.database.initialization.database-name:support_alert_db}")
    private String databaseName;
    
    @Value("${alert-service.database.initialization.username:support_alert_user}")
    private String dbUser;
    
    @Value("${alert-service.database.initialization.password:alert_secure_pass_2024}")
    private String dbPassword;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${POSTGRES_USER:tsdbadmin}")
    private String adminUsername;

    @Value("${POSTGRES_PASSWORD:devpassword}")
    private String adminPassword;

    public DatabaseInitializer(StructuredLoggerFactory structuredLoggerFactory) {
        this.logger = structuredLoggerFactory.getLogger(DatabaseInitializer.class);
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        // Extract base URL and connect to postgres database for initialization
        String baseUrl = datasourceUrl.substring(0, datasourceUrl.lastIndexOf("/"));
        String adminUrl = baseUrl + "/postgres";
        
        logger.with("operation", "databaseInit")
              .with("serviceName", "alert-service")
              .info("Initializing database for alert service");
        logger.with("databaseName", databaseName)
              .with("dbUser", dbUser)
              .info("Database configuration");
        
        try {
            // Create admin datasource to connect to postgres database
            org.postgresql.ds.PGSimpleDataSource adminDataSource = new org.postgresql.ds.PGSimpleDataSource();
            adminDataSource.setUrl(adminUrl);
            adminDataSource.setUser(adminUsername);
            adminDataSource.setPassword(adminPassword);

            try (Connection conn = adminDataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                // Create user if not exists
                ResultSet rs = stmt.executeQuery(
                    "SELECT 1 FROM pg_roles WHERE rolname = '" + dbUser + "'"
                );
                
                if (!rs.next()) {
                    logger.with("dbUser", dbUser)
                          .info("Creating database user");
                    stmt.execute(String.format(
                        "CREATE USER %s WITH PASSWORD '%s'", 
                        dbUser, dbPassword
                    ));
                } else {
                    logger.with("dbUser", dbUser)
                          .info("Database user already exists");
                }
                
                // Check if database exists
                rs = stmt.executeQuery(
                    "SELECT 1 FROM pg_database WHERE datname = '" + databaseName + "'"
                );
                
                if (!rs.next()) {
                    // Database doesn't exist, create it
                    logger.with("databaseName", databaseName)
                          .info("Creating database");
                    stmt.execute(String.format(
                        "CREATE DATABASE %s OWNER %s", 
                        databaseName, dbUser
                    ));
                    logger.with("databaseName", databaseName)
                          .info("Database created successfully");
                } else {
                    logger.with("databaseName", databaseName)
                          .info("Database already exists");
                }
                
                // Grant privileges
                stmt.execute(String.format(
                    "GRANT ALL PRIVILEGES ON DATABASE %s TO %s",
                    databaseName, dbUser
                ));
                
            }
        } catch (Exception e) {
            logger.with("operation", "initializeDatabase")
                  .with("databaseName", databaseName)
                  .with("error", e.getMessage())
                  .error("Error initializing database", e);
            // Don't throw - let Spring handle connection errors gracefully
        }

        // Now create the actual datasource with the service-specific user
        properties.setUrl(datasourceUrl);
        properties.setUsername(dbUser);
        properties.setPassword(dbPassword);
        
        return properties.initializeDataSourceBuilder().build();
    }
}