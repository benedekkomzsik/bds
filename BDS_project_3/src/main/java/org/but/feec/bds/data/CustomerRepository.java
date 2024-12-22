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

    public void updateAddress(String street, int postalCode, int houseNum, String username) {
        String updateAddressSql = "UPDATE bds.address SET " +
                "street = ?, " +
                "house_num = ?, " +
                "postal_code = ? " + // No trailing comma here
                "WHERE customer_id = (SELECT customer_id FROM bds.customer WHERE username = ?);";

        try (Connection connection = DataSourceConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateAddressSql)) {

            preparedStatement.setString(1, street);
            preparedStatement.setInt(2, houseNum);
            preparedStatement.setInt(3, postalCode);
            preparedStatement.setString(4, username);

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

    public void insertAddress( String street, int houseNum, int postalCode, String username) {
        String insertAddressSql = "INSERT INTO bds.address (customer_id, street, house_num, postal_code) " +
                "VALUES (" +
                "  (SELECT customer_id FROM bds.customer WHERE username = ?), " +
                "  ?, ?, ?);";

        try (Connection connection = DataSourceConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertAddressSql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, street);
            preparedStatement.setInt(3, houseNum);
            preparedStatement.setInt(4, postalCode);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Creating address failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Creating address failed, operation on the database failed.", e);
        }
    }


    public void insertName(String first_name, String last_name, String username) {
        String insertNameSQL = "UPDATE bds.customer SET first_name = ?, last_name = ? WHERE username = ?";
        try (Connection connection = DataSourceConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertNameSQL)) {
            preparedStatement.setString(1, first_name);
            preparedStatement.setString(2, last_name);
            preparedStatement.setString(3, username);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Updating name failed, no rows affected. Username: " + username);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Updating name failed, operation on the database failed.", e);
        }
    }


    public void insertContact(String email, int telephone, String username) {
        String insertContactSql = "INSERT INTO bds.contact (email, telephone, customer_id) " +
                "SELECT ?, ?, c.customer_id " +
                "FROM bds.customer c " +
                "WHERE c.username = ?";
        try (Connection connection = DataSourceConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertContactSql)) {
            preparedStatement.setString(1, email);
            preparedStatement.setInt(2, telephone);
            preparedStatement.setString(3, username);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Creating contact failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Creating contact failed, operation on the database failed.", e);
        }
    }



    public CustomerView getCustomerInfo(String username) {
        String query = "SELECT " +
                "c.customer_id, c.first_name, c.last_name, a.street, a.house_num, a.postal_code, ct.email, ct.telephone " +
                "FROM bds.customer c " +
                "JOIN bds.contact ct ON ct.customer_id = c.customer_id " +
                "JOIN bds.address a ON c.customer_id = a.customer_id " +
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
        customerView.setCustomerId(rs.getLong("customer_id"));
        customerView.setFirstName(rs.getString("first_name"));
        customerView.setLastName(rs.getString("last_name"));
        customerView.setStreet(rs.getString("street"));
        customerView.setHouseNum(rs.getLong("house_num"));
        customerView.setPostalCode(rs.getLong("postal_code"));
        customerView.setEmail(rs.getString("email"));
        customerView.setTelephone(rs.getLong("telephone"));
        return customerView;
    }

    public SignInView findCustomerByUsername(String username) {
        String findCustomerSql = "SELECT username, password FROM bds.customer WHERE username = ?";
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
            preparedStatement.setString(2, String.valueOf(customerCreateView.getPassword()));

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Creating customer failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Creating customer failed, operation on the database failed.", e);
        }
    }
}
