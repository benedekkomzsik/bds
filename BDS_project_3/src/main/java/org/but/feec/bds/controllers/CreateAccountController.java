package org.but.feec.bds.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.but.feec.bds.api.CustomerCreateView;
import org.but.feec.bds.data.CustomerRepository;
import org.but.feec.bds.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CreateAccountController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button createAccountButton;
    @FXML
    private CustomerRepository customerRepository;
    @FXML
    private CustomerService customerService;

    private static final Logger logger = LoggerFactory.getLogger(CreateAccountController.class);

    @FXML
    private void initialize() {
        customerRepository = new CustomerRepository();
        customerService = new CustomerService(customerRepository);

        logger.info("PersonCreateController initialized");
    }

    @FXML
    private void handleCreateAccount() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        CustomerCreateView customerCreateView = new CustomerCreateView();
        customerCreateView.setUsername(username);
        customerCreateView.setPassword(password.toCharArray());

        customerService.createCustomer(customerCreateView);

        userCreatedConfirmationDialog();

    }

    private void userCreatedConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Person Created Confirmation");
        alert.setHeaderText("Your person was successfully created.");

        Timeline idlestage = new Timeline(new KeyFrame(Duration.seconds(3), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                alert.setResult(ButtonType.CANCEL);
                alert.hide();
            }
        }));
        idlestage.setCycleCount(1);
        idlestage.play();
        Optional<ButtonType> result = alert.showAndWait();
    }
}

