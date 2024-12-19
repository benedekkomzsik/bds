package org.but.feec.bds.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.but.feec.bds.api.OrderView;
import org.but.feec.bds.config.DatabaseConfig;

public class OrderRepository {

    public List<OrderView> getOrderView() {
        try (Connection connection = DatabaseConfig.getDatabase().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement
}
