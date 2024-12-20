package org.but.feec.bds.data;

import org.but.feec.bds.api.CustomerCreateView;
import org.but.feec.bds.api.CustomerView;
import org.but.feec.bds.api.SignInView;
import org.but.feec.bds.config.DataSourceConfig;
import org.but.feec.bds.exceptions.DataAccessException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepository {

    public void updateAddress(String street, int streetNum, int postalCode, int houseNum, String city, String country, String username) {
        String updateAddressSql = "UPDATE bds.address SET " +
                "street = ?, " +
                "street_num = ?, " +
                "house_num = ?, " +
                "postal_code = ?, " +
                "city_id = (SELECT city_id FROM bds.city WHERE city_name = ?), " +
                "country_id = (SELECT country_id FROM bds.country WHERE country_name = ?) " +
                "WHERE customer_id = (SELECT customer_id FROM bds.customer WHERE username = ?);";

        try (Connection connection = DataSourceConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateAddressSql)) {

            preparedStatement.setString(1, street);
            preparedStatement.setInt(2, streetNum);
            preparedStatement.setInt(3, houseNum);
            preparedStatement.setInt(4, postalCode);
            preparedStatement.setString(5, city);
            preparedStatement.setString(6, country);
            preparedStatement.setString(7, username);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Updating address failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Updating address failed, operation on the database failed.", e);
        }
    }

    public void insertAddress(String street, int streetNum, int postalCode, int houseNum, String city, String country, String username) {
        String insertAddressSql = "INSERT INTO bds.address (customer_id, city_id, country_id, street, street_num, house_num, postal_code) " +
                "VALUES (" +
                "  (SELECT customer_id FROM bds.customer WHERE username = ?), " +
                "  COALESCE((SELECT city_id FROM bds.city WHERE city_name = ?), " +
                "           (INSERT INTO bds.city (city_name) VALUES (?) RETURNING city_id)), " +
                "  COALESCE((SELECT country_id FROM bds.country WHERE country_name = ?), " +
                "           (INSERT INTO bds.country (country_name) VALUES (?) RETURNING country_id)), " +
                "  ?, ?, ?, ?);";

        try (Connection connection = DataSourceConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertAddressSql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, city);
            preparedStatement.setString(3, city);
            preparedStatement.setString(4, country);
            preparedStatement.setString(5, country);
            preparedStatement.setString(6, street);
            preparedStatement.setInt(7, streetNum);
            preparedStatement.setInt(8, houseNum);
            preparedStatement.setInt(9, postalCode);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Creating address failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Creating address failed, operation on the database failed.", e);
        }
    }


    public CustomerView getCustomerInfo(String username) {
        String query = "SELECT " +
                "c.customer_id, c.first_name, c.last_name, a.street, a.house_num, ci.city_name, co.country_name " +
                "FROM bds.customer c " +
                "JOIN bds.address a ON c.customer_id = a.customer_id " +
                "JOIN bds.city ci ON a.city_id = ci.city_id " +
                "JOIN bds.country co ON a.country_id = co.country_id " +
                "WHERE c.username = ?";

        try (Connection connection = DataSourceConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapToCustomerView(resultSet);
            }
        } catch (SQLException e) {
            System.out.println("Failed to load customer info.");
            e.printStackTrace();
        }
        return null;
    }


    private CustomerView mapToCustomerView(ResultSet rs) throws SQLException {
        CustomerView customerView = new CustomerView();
        customerView.setCustomerId(rs.getLong("customer_id"));
        customerView.setFirstName(rs.getString("first_name"));
        customerView.setLastName(rs.getString("last_name"));
        customerView.setStreet(rs.getString("street"));
        customerView.setHouseNum(rs.getLong("house_num"));
        customerView.setCity(rs.getString("city_name"));
        customerView.setCountry(rs.getString("country_name"));
        return customerView;
    }

    public SignInView findCustomerByUsername(String username) {
        String findCustomerSql = "SELECT customer_id FROM bds.customer WHERE username = ?";
        try (Connection connection = DataSourceConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(findCustomerSql)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return CustomerAuth(resultSet);
            } else {
                throw new DataAccessException("Customer not found with username: " + username);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Finding customer by username failed, operation on the database failed.", e);
        }
    }

    private SignInView CustomerAuth(ResultSet rs) throws SQLException {
        SignInView signInView = new SignInView();
        signInView.setUsername(rs.getString("username"));
        signInView.setPassword(rs.getString("password"));

        return signInView;
    }

    public void createCustomer(CustomerCreateView customerCreateView) {
        String insertCustomerSql = "INSERT INTO bds.customer (username, password) VALUES (?, ?)";
        try (Connection connection = DataSourceConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertCustomerSql)) {

            preparedStatement.setString(3, customerCreateView.getUsername());
            preparedStatement.setString(4, new String(customerCreateView.getPassword()));

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Creating customer failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Creating customer failed, operation on the database failed.", e);
        }
    }
}
