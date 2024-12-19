package org.but.feec.bds.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConfig {
    private static final String PROPERTIES_FILE = "src/main/resources/app.properties";
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static Connection connection;

    public static DatabaseConfig getDataBase() {
        return ds;
    }

    // Method to load properties
    public static Properties loadProperties() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(PROPERTIES_FILE)) {
            properties.load(fis);
        } catch (Exception e) {
            logger.error("Failed to load properties file: ", e);
        }
        return properties;
    }

    // Method to establish database connection
    public static Connection getConnection() {
        if (connection == null) {
            try {
                Properties props = loadProperties();
                String url = props.getProperty("datasource.url");
                String username = props.getProperty("datasource.username");
                String password = props.getProperty("datasource.password");

                connection = DriverManager.getConnection(url, username, password);
                logger.info("Database connected successfully!");
            } catch (Exception e) {
                logger.error("Database connection failed: ", e);
            }
        }
        return connection;
    }

    // Close connection
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed.");
            } catch (Exception e) {
                logger.error("Failed to close database connection: ", e);
            }
        }
    }
}
