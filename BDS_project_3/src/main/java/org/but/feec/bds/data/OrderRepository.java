package org.but.feec.bds.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.but.feec.bds.api.OrderView;
import org.but.feec.bds.config.DataSourceConfig;
import org.but.feec.bds.exceptions.DataAccessException;

public class OrderRepository {

    public void insertOrder(String username, Long book_id, int shipping){
        String insertSQL = "WITH customer_data AS (" +
                "    SELECT" +
                "        customer_id" +
                "    FROM" +
                "        bds.customer" +
                "    WHERE" +
                "        username = ?" +//username
                ")," +
                "address_data AS (" +
                "    SELECT" +
                "        address_id" +
                "    FROM" +
                "        bds.address" +
                "    WHERE" +
                "        customer_id = (SELECT customer_id FROM customer_data)" +
                ")" +
                "INSERT INTO bds.cust_order (" +
                "    customer_id," +
                "    dest_addres_id," +
                "    book_id," +
                "    shipping_method_id," +
                "    price," +
                "    time_of_order" +
                ")" +
                "VALUES (" +
                "    (SELECT customer_id FROM customer_data)," +
                "    (SELECT address_id FROM address_data)," +
                "    ?," + //book
                "    ?," + //shipping
                "    (" +
                "        SELECT" +
                "            COALESCE(shipping_cost, 0) + COALESCE(price, 0)" +
                "        FROM" +
                "            bds.shipping" +
                "            CROSS JOIN bds.book" +
                "        WHERE" +
                "            shipping_method_id = ?" +
                "            AND book_id = ?" +
                "    )," +
                "    CURRENT_TIMESTAMP" +
                ");";
        try(Connection connection = DataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, username);
            preparedStatement.setLong(2, book_id);
            preparedStatement.setInt(3, shipping);
            preparedStatement.setInt(4, shipping);
            preparedStatement.setLong(5, book_id);

            int affectedRows = preparedStatement.executeUpdate();

            if(affectedRows == 0){
                throw new DataAccessException("Creating person failed, no rows affected.");
            }
        }catch (SQLException e){
            throw new DataAccessException("Creating person failed operation on the database failed.");
        }
    }

    public List<OrderView> getOrdersByUsername(String username) {

        try (Connection connection = DataSourceConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT " +
                             "o.order_id, o.customer_id, o.dest_addres_id, b.book_name, ol.price" +
                             "o.shipping_method_id, o.order_status_id, o.price, o.time_of_order, " +
                             "c.first_name, c.last_name, c.username, " +
                             "a.street, a.city_id, " +
                             "s.shiping_name " +
                             "FROM bds.cust_order o " +
                             "JOIN bds.customer c ON o.customer_id = c.customer_id " +
                             "JOIN bds.address a ON o.dest_addres_id = a.address_id " +
                             "JOIN bds.order_line ol ON o.order_id = ol.order_id " +
                             "JOIN bds.book b ON ol.book_id = b.book_id " +
                             "JOIN bds.shipping s ON o.shipping_method_id = s.shipping_method_id " +
                             "WHERE c.username = ?")) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<OrderView> orderViews = new ArrayList<>();
            while (resultSet.next()) {
                orderViews.add(mapToOrderView(resultSet));
            }
            return orderViews;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
        orderView.setOrderStatus(rs.getString("order_status"));
        orderView.setBookName(rs.getString("book_name"));
        orderView.setIsbn(rs.getString("isbn"));
        orderView.setPrice(rs.getLong("price"));
        return orderView;
    }

}
