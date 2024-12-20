package org.but.feec.bds.api;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class CustomerView {
    private LongProperty customerId = new SimpleLongProperty();
    private StringProperty firstName = new SimpleStringProperty();
    private StringProperty lastName = new SimpleStringProperty();
    private StringProperty street = new SimpleStringProperty();
    private LongProperty houseNum = new SimpleLongProperty();
    private StringProperty city = new SimpleStringProperty();
    private StringProperty country = new SimpleStringProperty();

    // Getters and Setters with Properties
    public long getCustomerId() {
        return customerIdProperty().get();
    }

    public void setCustomerId(long customerId) {
        this.customerIdProperty().set(customerId);
    }

    public LongProperty customerIdProperty() {
        return customerId;
    }

    public String getFirstName() {
        return firstNameProperty().get();
    }

    public void setFirstName(String firstName) {
        this.firstNameProperty().set(firstName);
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public String getLastName() {
        return lastNameProperty().get();
    }

    public void setLastName(String lastName) {
        this.lastNameProperty().set(lastName);
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public String getStreet() {
        return streetProperty().get();
    }

    public void setStreet(String street) {
        this.streetProperty().set(street);
    }

    public StringProperty streetProperty() {
        return street;
    }

    public long getHouseNum() {
        return houseNumProperty().get();
    }

    public void setHouseNum(long houseNum) {
        this.houseNumProperty().set(houseNum);
    }

    public LongProperty houseNumProperty() {
        return houseNum;
    }

    public String getCity() {
        return cityProperty().get();
    }

    public void setCity(String city) {
        this.cityProperty().set(city);
    }

    public StringProperty cityProperty() {
        return city;
    }

    public String getCountry() {
        return countryProperty().get();
    }

    public void setCountry(String country) {
        this.countryProperty().set(country);
    }

    public StringProperty countryProperty() {
        return country;
    }
}
