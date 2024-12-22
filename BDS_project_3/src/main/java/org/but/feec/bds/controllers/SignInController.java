package org.but.feec.bds.controllers;

import org.but.feec.bds.exceptions.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.but.feec.bds.data.CustomerRepository;
import org.but.feec.bds.service.AuthService;
import org.but.feec.bds.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class SignInController {

    private static final Logger logger = LoggerFactory.getLogger(SignInController.class);
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button signInButton;
    @FXML
    private AuthService authService;
    @FXML CustomerRepository customerRepository;
    private String username;
    private BookStoreController bookStoreController;


    public void setBookStoreController(BookStoreController bookStoreController) {
        this.bookStoreController = bookStoreController;
    }
    private boolean isAuth = false;

    public String getUsername(){return username; }

    public boolean getIsAuth() {
        return isAuth;
    }


    @FXML
    private void initialize(){
        customerRepository = new CustomerRepository();
        authService = new AuthService(customerRepository);
    }

    public void signInActionHandler(ActionEvent event) {
        handleSignIn();
    }

    @FXML
    private void handleSignIn() {
        username = usernameField.getText();
        String password = passwordField.getText();

        try {
            boolean authenticated = authService.authenticate(username, password);
            if (authenticated) {
                showAccView();
                authConfirmDialog();
                isAuth = true;
            } else {
                showInvalidPaswordDialog();
            }
        } catch (ResourceNotFoundException | DataAccessException e) {
            showInvalidPaswordDialog();
        }

    }

    private void showInvalidPaswordDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Unauthenticated");
        alert.setHeaderText("The user is not authenticated");
        alert.setContentText("Please provide a valid username and password");//ww  w . j  a  va2s  .  co  m

        alert.showAndWait();
    }

    public void showAccView() {
        try {
            if (bookStoreController != null) {
                bookStoreController.handleAuthenticationSuccess();
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Account.fxml"));
            VBox userProfile = loader.load();

            AccountController controller = loader.getController();
            controller.setUsername(username);

            Stage profileStage = new Stage();
            profileStage.setTitle("Profile");
            Scene scene = new Scene(userProfile);

            profileStage.setScene(scene);

            profileStage.show();


        } catch (IOException ex) {
            ex.printStackTrace();  // Print the exception details to the console
            ExceptionHandler.handleException(ex);
        }
    }

    @FXML
    private void handleCreateAccount(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreateAccount.fxml"));
            VBox createAccount = loader.load();

            CreateAccountController controller = loader.getController();
            javafx.stage.Stage createAccStage = new javafx.stage.Stage();
            createAccStage.setTitle("New Account");

            javafx.scene.Scene scene = new javafx.scene.Scene(createAccount);
            createAccStage.setScene(scene);


            createAccStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void authConfirmDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logging confirmation");
        alert.setHeaderText("You were successfully logged in.");

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

        if (result.get() == ButtonType.OK) {
            System.out.println("ok clicked");
        } else if (result.get() == ButtonType.CANCEL) {
            System.out.println("cancel clicked");
        }
    }
}