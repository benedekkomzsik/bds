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

    public void updateAddress(String street, int postalCode, int houseNum, String city, String country, String username) {
        String updateAddressSql = "UPDATE bds.address SET " +
                "street = ?, " +
                "house_num = ?, " +
                "postal_code = ?, " +
                "city_id = (SELECT city_id FROM bds.city WHERE city_name = ?), " +
                "country_id = (SELECT country_id FROM bds.country WHERE country_name = ?) " +
                "WHERE customer_id = (SELECT customer_id FROM bds.customer WHERE username = ?);";

        try (Connection connection = DataSourceConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateAddressSql)) {

            preparedStatement.setString(1, street);
            preparedStatement.setInt(2, houseNum);
            preparedStatement.setInt(3, postalCode);
            preparedStatement.setString(4, city);
            preparedStatement.setString(5, country);
            preparedStatement.setString(6, username);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Updating address failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Updating address failed, operation on the database failed.", e);
        }
    }

    public void updateContact(String email, int telephone, String username){
        String insertContactSql = "UPDATE bds.contact" +
                " SET email = ?," +
                " telephone = ?" +
                " WHERE customer_id = (SELECT customer_id FROM bds.customer WHERE username = ?);";
        try(Connection connection = DataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertContactSql, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, email);
            preparedStatement.setInt(2, telephone);
            preparedStatement.setString(3, username);

            int affectedRows = preparedStatement.executeUpdate();

            if(affectedRows == 0){
                throw new DataAccessException("Updating contact failed, no rows affected.");
            }
        }catch (SQLException e){
            throw new DataAccessException("Updating contact failed operation on the database failed.");
        }
    }

    public void insertAddress(String street, int houseNum, int postalCode, String city, String country, String username) {
        String insertAddressSql = "INSERT INTO bds.address (customer_id, city_id, country_id, street, house_num, postal_code) " +
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
            preparedStatement.setInt(7, houseNum);
            preparedStatement.setInt(8, postalCode);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Creating address failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Creating address failed, operation on the database failed.", e);
        }
    }

    public void insertName(String first_name, String last_name, String username){
        String insertNameSQL = "UPDATE bds.customer" + " SET first_name = ?, last_name = ?" + " WHERE bds.customer.username = ?;";
        try(Connection connection = DataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertNameSQL, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, first_name);
            preparedStatement.setString(2, last_name);
            preparedStatement.setString(3, username);

            int affectedRows = preparedStatement.executeUpdate();

            if(affectedRows == 0){
                throw new DataAccessException("Creating person failed, no rows affected.");
            }

        }catch (SQLException e){
            throw new DataAccessException("Creating person failed operation on the database failed.");
        }

    }

    public void insertContact(String email, int telephone, String username) {
        String insertContactSql = "INSERT INTO bds.contact (email, telephone, customer_id)" +
                " SELECT " +
                "    ? AS email," +
                "    ? AS telephone," +
                "    c.customer_id" +
                " FROM" +
                "    bds.customer c" +
                " WHERE" +
                "    c.username = ?;";
        try(Connection connection = DataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertContactSql, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, email);
            preparedStatement.setInt(2, telephone);
            preparedStatement.setString(3, username);

            int affectedRows = preparedStatement.executeUpdate();

            if(affectedRows == 0){
                throw new DataAccessException("Creating contact failed, no rows affected.");
            }
        }catch (SQLException e){
            throw new DataAccessException("Creating contact failed operation on the database failed.");
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
        customerView.setEmail(rs.getString("email"));
        customerView.setTelephone(rs.getLong("telephone"));
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

            preparedStatement.setString(1, customerCreateView.getUsername());
            preparedStatement.setString(2, new String(customerCreateView.getPassword()));

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Creating customer failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Creating customer failed, operation on the database failed.", e);
        }
    }
}
