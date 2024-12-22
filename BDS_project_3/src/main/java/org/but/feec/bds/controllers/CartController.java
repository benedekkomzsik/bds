package org.but.feec.bds.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.but.feec.bds.api.OrderView;
import org.but.feec.bds.data.OrderRepository;

import java.awt.*;
import java.util.List;

public class CartController{

    private String username;

    public void setUsername(String username) {
        this.username = username;
        updateInfo();
    }

    @FXML
    private VBox orderListVBox;

    private final OrderRepository orderRepository = new OrderRepository();

    @FXML
    private void updateInfo() {
        List<OrderView> orders = orderRepository.getOrdersByUsername(username);

        if (orders == null || orders.isEmpty()) {
            System.out.println("No orders found for username: " + username);
            return;
        }

        for (OrderView order : orders) {
            Label orderLabel = new Label("Order ID: " + order.getOrderId() +
                    ", Book Name: " + order.getBookName() +
                    ", Price: " + order.getPrice() +
                    ", Shipping: " + order.getShipping() +
                    ", Time of Order: " + order.getTimeOfOrder());

            orderListVBox.getChildren().add(orderLabel);
        }
    }
}
