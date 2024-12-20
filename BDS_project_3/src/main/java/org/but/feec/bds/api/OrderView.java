package org.but.feec.bds.api;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class OrderView {
    private LongProperty orderId = new SimpleLongProperty();
    private LongProperty customerId = new SimpleLongProperty();
    private StringProperty timeOfOrder = new SimpleStringProperty();
    private StringProperty orderStatus = new SimpleStringProperty();
    private StringProperty bookName = new SimpleStringProperty();
    private StringProperty isbn = new SimpleStringProperty();
    private DoubleProperty price = new SimpleDoubleProperty();

    // Getters and Setters
    public long getOrderId() {
        return orderIdProperty().get();
    }

    public void setOrderId(long orderId) {
        this.orderIdProperty().set(orderId);
    }

    public LongProperty orderIdProperty() {
        return orderId;
    }

    public long getCustomerId() {
        return customerIdProperty().get();
    }

    public void setCustomerId(long customerId) {
        this.customerIdProperty().set(customerId);
    }

    public LongProperty customerIdProperty() {
        return customerId;
    }

    public String getTimeOfOrder() {
        return timeOfOrderProperty().get();
    }

    public void setTimeOfOrder(String timeOfOrder) {
        this.timeOfOrderProperty().set(timeOfOrder);
    }

    public StringProperty timeOfOrderProperty() {
        return timeOfOrder;
    }

    public String getOrderStatus() {
        return orderStatusProperty().get();
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatusProperty().set(orderStatus);
    }

    public StringProperty orderStatusProperty() {
        return orderStatus;
    }

    public String getBookName() {
        return bookNameProperty().get();
    }

    public void setBookName(String bookName) {
        this.bookNameProperty().set(bookName);
    }

    public StringProperty bookNameProperty() {
        return bookName;
    }

    public String getIsbn() {
        return isbnProperty().get();
    }

    public void setIsbn(String isbn) {
        this.isbnProperty().set(isbn);
    }

    public StringProperty isbnProperty() {
        return isbn;
    }

    public double getPrice() {
        return priceProperty().get();
    }

    public void setPrice(double price) {
        this.priceProperty().set(price);
    }

    public DoubleProperty priceProperty() {
        return price;
    }
}