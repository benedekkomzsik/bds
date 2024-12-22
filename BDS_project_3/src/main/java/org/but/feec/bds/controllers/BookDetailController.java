package org.but.feec.bds.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.but.feec.bds.api.BookView;
import org.but.feec.bds.api.OrderView;
import org.but.feec.bds.data.BookRepository;
import org.but.feec.bds.data.OrderRepository;

import java.awt.*;
import java.io.IOException;

public class BookDetailController {

    @FXML
    private Label titleLabel;
    @FXML
    private Label genreLabel;
    @FXML
    private Label publisherLabel;
    @FXML
    private Label introLabel;
    @FXML
    private Label authorLabel;
    @FXML
    private Label pagesLabel;
    @FXML
    private Label isbnLabel;
    @FXML
    private Label releaseYearLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label idLabel;
    @FXML
    private Button buyButton;

    private BookView mojaKniha;

    @FXML
    private SignInController signInController;

    public SignInController getSignInController() {
        return signInController;
    }

    @FXML
    public void initialize(BookView book, SignInController signInController) {
        mojaKniha = book;
        this.signInController = signInController;
        titleLabel.setText(book.getBookName());
        introLabel.setText("Introduction: " + book.getIntro());
        authorLabel.setText("Author: " + book.getAuthorFirstName() + " " + book.getAuthorLastName());
        pagesLabel.setText("Number of Pages: " + book.getNumPages());
        isbnLabel.setText("ISBN: " + book.getIsbn());
        releaseYearLabel.setText("Release Year: " + book.getReleaseYear());
    }

    @FXML
    public void buyBook() {
        if (signInController != null && signInController.getIsAuth()) {

            Stage shippingStage = new Stage();
            shippingStage.initModality(Modality.APPLICATION_MODAL);
            shippingStage.setTitle("Select Shipping Option");


            VBox vbox = new VBox(10);
            vbox.setAlignment(Pos.CENTER);


            ToggleGroup shippingOptions = new ToggleGroup();
            RadioButton glsRadio = new RadioButton("GLS - $1.50");
            RadioButton upsRadio = new RadioButton("UPS - $1.80");
            RadioButton slovenskaPostaRadio = new RadioButton("Slovenska posta - $2.15");
            RadioButton zasielkovnaRadio = new RadioButton("Zasielkovna - $1.50");
            RadioButton odberNaMiesteRadio = new RadioButton("Odber na mieste - $1.20");

            glsRadio.setToggleGroup(shippingOptions);
            upsRadio.setToggleGroup(shippingOptions);
            slovenskaPostaRadio.setToggleGroup(shippingOptions);
            zasielkovnaRadio.setToggleGroup(shippingOptions);
            odberNaMiesteRadio.setToggleGroup(shippingOptions);


            Button confirmButton = new Button("Confirm");
            confirmButton.setOnAction(e -> {
                String selectedShippingOption = ((RadioButton) shippingOptions.getSelectedToggle()).getText();

                int shippingOptionNumber = 0;

                if (glsRadio.isSelected()) {
                    shippingOptionNumber = 6; // GLS
                } else if (upsRadio.isSelected()) {
                    shippingOptionNumber = 7; // UPS
                } else if (slovenskaPostaRadio.isSelected()) {
                    shippingOptionNumber = 8; // Slovenska posta
                } else if (zasielkovnaRadio.isSelected()) {
                    shippingOptionNumber = 9; // Zasielkovna
                } else if (odberNaMiesteRadio.isSelected()) {
                    shippingOptionNumber = 10; // Odber na mieste
                }

                shippingStage.close();


                OrderRepository orderRepository = new OrderRepository();
                orderRepository.insertOrder(signInController.getUsername(), mojaKniha.getBookId(), shippingOptionNumber);

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Book successfully bought!");
                successAlert.showAndWait();
            });

            vbox.getChildren().addAll(glsRadio, upsRadio, slovenskaPostaRadio, zasielkovnaRadio, odberNaMiesteRadio, confirmButton);

            // Show the shipping options pop-up
            Scene scene = new Scene(vbox, 300, 200);
            shippingStage.setScene(scene);
            shippingStage.show();



        } else {
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Sign up");
            successAlert.setHeaderText(null);
            successAlert.setContentText("You need to sign up to buy books ");
            successAlert.showAndWait();
        }
    }

}

