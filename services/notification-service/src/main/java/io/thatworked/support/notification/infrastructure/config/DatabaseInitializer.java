package io.thatworked.support.notification.infrastructure.config;

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

/**
 * Database initializer for notification service.
 * Creates the database and user if they don't exist.
 */
@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
public class DatabaseInitializer {
    
    private final StructuredLogger logger;

    @Value("${db.name:notification_db}")
    private String databaseName;
    
    @Value("${db.user:notification_user}")
    private String dbUser;
    
    @Value("${db.password:notification_pass}")
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
              .with("serviceName", "notification-service")
              .info("Initializing database for notification service");
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
                
                // Connect to the actual database to create tables
                logger.with("databaseName", databaseName)
                      .info("Creating notification tables and indexes");
                
                // Need to connect to the actual database to create tables
                String targetUrl = baseUrl + "/" + databaseName;
                org.postgresql.ds.PGSimpleDataSource targetDataSource = new org.postgresql.ds.PGSimpleDataSource();
                targetDataSource.setUrl(targetUrl);
                targetDataSource.setUser(adminUsername);  // Use admin for initial setup
                targetDataSource.setPassword(adminPassword);
                
                try (Connection targetConn = targetDataSource.getConnection();
                     Statement targetStmt = targetConn.createStatement()) {
                    
                    // Create notification tables
                    createNotificationTables(targetStmt);
                    
                    // Create indexes
                    createIndexes(targetStmt);
                    
                    // Grant table permissions to service user
                    targetStmt.execute(String.format(
                        "GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO %s",
                        dbUser
                    ));
                    targetStmt.execute(String.format(
                        "GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO %s",
                        dbUser
                    ));
                    
                    logger.with("operation", "databaseInit")
                          .with("status", "tablesCreated")
                          .info("Notification tables and indexes created successfully");
                }
                
                logger.with("operation", "databaseInit")
                      .with("status", "completed")
                      .info("Database initialization completed successfully");
                
            }
        } catch (Exception e) {
            logger.with("operation", "databaseInit")
                  .with("status", "failed")
                  .error("Error initializing database", e);
            // Don't throw - let Spring handle connection errors gracefully
        }

        // Now create the actual datasource with the service-specific user
        properties.setUrl(datasourceUrl);
        properties.setUsername(dbUser);
        properties.setPassword(dbPassword);
        
        return properties.initializeDataSourceBuilder().build();
    }
    
    private void createNotificationTables(Statement stmt) throws Exception {
        logger.with("operation", "createTable")
              .with("tableName", "notification_requests")
              .info("Creating notification_requests table...");
              
        // Create notification_requests table
        String createRequestsTable = """
            CREATE TABLE IF NOT EXISTS notification_requests (
                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                notification_type VARCHAR(255) NOT NULL,
                channel VARCHAR(255) NOT NULL,
                recipient VARCHAR(255) NOT NULL,
                subject VARCHAR(500),
                message TEXT,
                requested_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                source_event_id UUID
            )
            """;
        stmt.execute(createRequestsTable);
        
        // Create notification_metadata table
        String createMetadataTable = """
            CREATE TABLE IF NOT EXISTS notification_metadata (
                notification_request_id UUID NOT NULL,
                metadata_key VARCHAR(255) NOT NULL,
                metadata_value VARCHAR(1000),
                PRIMARY KEY (notification_request_id, metadata_key),
                FOREIGN KEY (notification_request_id) REFERENCES notification_requests(id) ON DELETE CASCADE
            )
            """;
        stmt.execute(createMetadataTable);
        
        // Create notification_results table
        String createResultsTable = """
            CREATE TABLE IF NOT EXISTS notification_results (
                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                notification_request_id UUID NOT NULL,
                successful BOOLEAN NOT NULL,
                message TEXT,
                error_details TEXT,
                sent_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                channel_specific_id VARCHAR(255),
                FOREIGN KEY (notification_request_id) REFERENCES notification_requests(id) ON DELETE CASCADE
            )
            """;
        stmt.execute(createResultsTable);
        
        logger.with("operation", "createTable")
              .with("status", "completed")
              .info("Notification tables created/verified");
    }
    
    private void createIndexes(Statement stmt) throws Exception {
        String[] indexes = {
            "CREATE INDEX IF NOT EXISTS idx_notification_requests_type ON notification_requests(notification_type)",
            "CREATE INDEX IF NOT EXISTS idx_notification_requests_channel ON notification_requests(channel)",
            "CREATE INDEX IF NOT EXISTS idx_notification_requests_recipient ON notification_requests(recipient)",
            "CREATE INDEX IF NOT EXISTS idx_notification_requests_requested_at ON notification_requests(requested_at)",
            "CREATE INDEX IF NOT EXISTS idx_notification_requests_source_event ON notification_requests(source_event_id)",
            "CREATE INDEX IF NOT EXISTS idx_notification_results_request_id ON notification_results(notification_request_id)",
            "CREATE INDEX IF NOT EXISTS idx_notification_results_status ON notification_results(status)",
            "CREATE INDEX IF NOT EXISTS idx_notification_results_processed_at ON notification_results(processed_at)"
        };
        
        for (String indexSql : indexes) {
            try {
                stmt.execute(indexSql);
                logger.with("index", indexSql.substring(0, Math.min(indexSql.length(), 50)) + "...")
                      .debug("Index created/verified");
            } catch (Exception e) {
                logger.with("error", e.getMessage())
                      .warn("Failed to create index");
            }
        }
        
        logger.with("operation", "createIndexes")
              .with("status", "completed")
              .info("Database indexes created/verified");
    }
}