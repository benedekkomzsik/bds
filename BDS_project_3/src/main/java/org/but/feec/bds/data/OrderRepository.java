package org.but.feec.bds.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.but.feec.bds.api.OrderView;
import org.but.feec.bds.config.DataSourceConfig;
import org.but.feec.bds.exceptions.DataAccessException;

public class OrderRepository {

    public void insertOrder(String username, Long book_id, int shipping) {
        String insertOrderSQL = "WITH customer_data AS (" +
                "    SELECT customer_id FROM bds.customer WHERE username = ?" +
                ")," +
                "address_data AS (" +
                "    SELECT address_id FROM bds.address WHERE customer_id = (SELECT customer_id FROM customer_data)" +
                ")" +
                "INSERT INTO bds.cust_order (" +
                "    customer_id, dest_address_id, shipping_method_id, time_of_order" +
                ") VALUES (" +
                "    (SELECT customer_id FROM customer_data)," +
                "    (SELECT address_id FROM address_data)," +
                "    ?," + // shipping
                "    CURRENT_TIMESTAMP" +
                ") RETURNING order_id;";

        String insertOrderLineSQL = "INSERT INTO bds.order_line (" +
                "    order_id, book_id, price" +
                ") VALUES (" +
                "    ?," + // order_id
                "    ?," + // book_id
                "    (SELECT price FROM bds.book WHERE book_id = ?)" + // price from book
                ");";

        try (Connection connection = DataSourceConfig.getConnection()) {
            connection.setAutoCommit(false);

            Long orderId;
            // Insert into cust_order
            try (PreparedStatement orderStatement = connection.prepareStatement(insertOrderSQL)) {
                orderStatement.setString(1, username);
                orderStatement.setInt(2, shipping);

                try (ResultSet rs = orderStatement.executeQuery()) {
                    if (rs.next()) {
                        orderId = rs.getLong(1);
                    } else {
                        throw new DataAccessException("Creating order failed, no order_id generated.");
                    }
                }
            }

            // Insert into order_line
            try (PreparedStatement orderLineStatement = connection.prepareStatement(insertOrderLineSQL)) {
                orderLineStatement.setLong(1, orderId);
                orderLineStatement.setLong(2, book_id);
                orderLineStatement.setLong(3, book_id);

                int affectedRows = orderLineStatement.executeUpdate();
                if (affectedRows == 0) {
                    throw new DataAccessException("Creating order line failed, no rows affected.");
                }
            }

            connection.commit();
        } catch (SQLException e) {
            throw new DataAccessException("Creating order failed, operation on the database failed.", e);
        }
    }

    public List<OrderView> getOrdersByUsername(String username) {
        String query = "SELECT " +
                "    o.order_id, " +
                "    o.customer_id, " +
                "    b.book_name, " +
                "    ol.price, " +
                "    s.shiping_name, " +
                "    o.time_of_order " +
                "FROM bds.cust_order o " +
                "JOIN bds.customer c ON o.customer_id = c.customer_id " +
                "JOIN bds.order_line ol ON o.order_id = ol.order_id " +
                "JOIN bds.book b ON ol.book_id = b.book_id " +
                "JOIN bds.shipping s ON o.shipping_method_id = s.shipping_method_id " +
                "WHERE c.username = ?";

        List<OrderView> orderViews = new ArrayList<>();

        try (Connection connection = DataSourceConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    orderViews.add(mapToOrderView(resultSet));
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch orders.");
            e.printStackTrace();
        }

        return orderViews;
    }


    public List<OrderView> getOrderView() {
        try (Connection connection = DataSourceConfig.getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT " +
                     "o.order_id, " +
                     "o.time_of_order, " +
                     "c.customer_id, " +
                     "os.order_status, " +
                     "b.book_name, " +
                     "b.isbn, " +
                     "ol.price " +
                     "FROM bds.cust_order o " +
                     "JOIN bds.customer c ON o.customer_id = c.customer_id " +
                     "JOIN bds.order_status os ON o.order_status_id = os.order_status_id " +
                     "JOIN bds.order_line ol ON o.order_id = ol.order_id " +
                     "JOIN bds.book b ON ol.book_id = b.book_id;"
             );
             ResultSet resultSet = preparedStatement.executeQuery();) {
            List<OrderView> orderViews = new ArrayList<>();
            while (resultSet.next()) {
                orderViews.add(mapToOrderView(resultSet));
            }
            return orderViews;
        } catch (SQLException e) {
            System.out.println("Nemozno nacitat order.");
            e.printStackTrace();
        }
        return null;
    }

    private OrderView mapToOrderView(ResultSet rs) throws SQLException{
        OrderView orderView = new OrderView();
        orderView.setOrderId(rs.getLong("order_id"));
        orderView.setTimeOfOrder(rs.getString("time_of_order"));
        orderView.setCustomerId(rs.getLong("customer_id"));
        orderView.setBookName(rs.getString("book_name"));
        orderView.setPrice(rs.getLong("price"));
        orderView.setShipping(rs.getString("shiping_name"));
        return orderView;
    }

}
