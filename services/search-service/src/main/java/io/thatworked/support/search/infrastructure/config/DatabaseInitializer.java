package io.thatworked.support.search.infrastructure.config;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class DatabaseInitializer {

    private final StructuredLogger logger;
    
    public DatabaseInitializer(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(DatabaseInitializer.class);
    }
    
    @Value("${spring.datasource.url}")
    private String datasourceUrl;
    
    @Value("${spring.datasource.username}")
    private String dbUser;
    
    @Value("${spring.datasource.password}")
    private String dbPassword;
    
    @Value("${POSTGRES_USER:tsdbadmin}")
    private String adminUser;
    
    @Value("${POSTGRES_PASSWORD:devpassword}")
    private String adminPassword;

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        try {
            initializeDatabase();
        } catch (SQLException e) {
            logger.with("error", e.getMessage())
                    .with("errorType", e.getClass().getSimpleName())
                    .error("Failed to initialize database", e);
            throw new RuntimeException("Database initialization failed", e);
        }
        
        properties.setUrl(datasourceUrl);
        properties.setUsername(dbUser);
        properties.setPassword(dbPassword);
        
        return properties.initializeDataSourceBuilder().build();
    }
    
    private void initializeDatabase() throws SQLException {
        String baseUrl = datasourceUrl.substring(0, datasourceUrl.lastIndexOf("/"));
        String adminUrl = baseUrl + "/postgres";
        String databaseName = datasourceUrl.substring(datasourceUrl.lastIndexOf("/") + 1);
        
        logger.with("databaseName", databaseName)
                .with("action", "initializeDatabase")
                .info("Initializing database");
        
        try (Connection adminConnection = DriverManager.getConnection(adminUrl, adminUser, adminPassword)) {
            
            // Create user if not exists
            if (!userExists(adminConnection, dbUser)) {
                logger.with("username", dbUser)
                        .with("action", "createUser")
                        .info("Creating user");
                try (Statement stmt = adminConnection.createStatement()) {
                    stmt.execute(String.format(
                        "CREATE USER %s WITH PASSWORD '%s'", 
                        dbUser, dbPassword.replace("'", "''")));
                }
            }
            
            // Create database if not exists
            if (!databaseExists(adminConnection, databaseName)) {
                logger.with("databaseName", databaseName)
                        .with("action", "createDatabase")
                        .info("Creating database");
                try (Statement stmt = adminConnection.createStatement()) {
                    stmt.execute("CREATE DATABASE " + databaseName + " OWNER " + dbUser);
                }
            }
            
            // Grant privileges
            try (Statement stmt = adminConnection.createStatement()) {
                stmt.execute("GRANT ALL PRIVILEGES ON DATABASE " + databaseName + " TO " + dbUser);
            }
        }
        
        logger.with("status", "completed")
                .with("action", "initializeDatabase")
                .info("Database initialization completed successfully");
    }
    
    private boolean userExists(Connection connection, String username) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
            "SELECT 1 FROM pg_user WHERE usename = ?")) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    private boolean databaseExists(Connection connection, String dbName) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
            "SELECT 1 FROM pg_database WHERE datname = ?")) {
            stmt.setString(1, dbName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}