package org.but.feec.bds.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.but.feec.bds.api.OrderView;
import org.but.feec.bds.config.DataSourceConfig;

public class OrderRepository {

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
