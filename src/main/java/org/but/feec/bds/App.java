package org.but.feec.bds;


import org.but.feec.bds.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args ) {
        System.out.println( "Hello World!" );

        Connection conn = DatabaseConfig.getConnection();

        try (Statement stmt = conn.createStatement()) {
            // Example query to test the connection
            ResultSet rs = stmt.executeQuery("SELECT current_date");
            while (rs.next()) {
                System.out.println("Current Date: " + rs.getDate(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabaseConfig.closeConnection();
        }
    }
}
