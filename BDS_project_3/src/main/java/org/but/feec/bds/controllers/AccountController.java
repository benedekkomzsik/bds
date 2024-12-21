package org.but.feec.bds.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.but.feec.bds.api.CustomerView;
import org.but.feec.bds.data.CustomerRepository;
import org.but.feec.bds.exceptions.DataAccessException;
import org.but.feec.bds.service.CustomerService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AccountController {

    @FXML
    private Label firstNameLabel, lastNameLabel, streetLabel, houseNumLabel,
            postalCodeLabel, telephoneLabel, emailLabel, cityLabel, countryLabel;
    private String username;
    @FXML
    private Button saveButton;

    private Stage accStage; // Add this variable

    public void setAccStage(Stage accStage) {
        this.accStage = accStage;
    }


    public void setUsername(String username) {
        this.username = username;
        updateCustomerInfo();
    }
    @FXML
    private void updateCustomerInfo() {
        CustomerRepository customerRepository = new CustomerRepository();
        CustomerView customer = customerRepository.getCustomerInfo(username);


        if (customer != null) {
            firstNameLabel.setText("First Name: " + customer.getFirstName());
            lastNameLabel.setText("Last Name: " + customer.getLastName());
            streetLabel.setText("Street: " + customer.getStreet());
            houseNumLabel.setText("House Num: " + String.valueOf(customer.getHouseNum()));
            postalCodeLabel.setText("Postal Code: " + String.valueOf(customer.getPostalCode()));
            cityLabel.setText("City: " + customer.getCity());
            countryLabel.setText("Country: " + customer.getCountry());
            telephoneLabel.setText("Telephone: " + String.valueOf(customer.getTelephone()));
            emailLabel.setText("Email: " + customer.getEmail());
        } else {
            System.out.println("Data not found");
        }
    }

    @FXML
    private void showCart(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Cart.fxml"));
            Parent cartRoot = loader.load();

            Stage cartStage = new Stage();
            cartStage.setTitle("Your Cart");
            cartStage.initModality(Modality.APPLICATION_MODAL);
            cartStage.setScene(new Scene(cartRoot));

            CartController cartController = loader.getController();  // Use getController to obtain the existing instance
            cartController.setUsername(username);

            cartStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void insertData() {
        Stage contactStage = new Stage();

        // Create UI components for the new stage
        Label emailInputLabel = new Label("Email:");
        Label telephoneInputLabel = new Label("Telephone:");
        Label firstNameLabel = new Label("First Name:");
        Label lastNameLabel = new Label("Last Name:");
        Label streetLabel = new Label("Street:");
        Label houseNumLabel = new Label("House Number:");
        Label postalLabel = new Label("Postal Code:");
        Label cityLabel = new Label("City:");
        Label countryLabel = new Label("Country:");

        TextField emailTextField = new TextField();
        TextField telephoneTextField = new TextField();
        TextField first_nameTextField = new TextField();
        TextField last_nameTextField = new TextField();
        TextField streetTextField = new TextField();
        TextField houseNumTextField = new TextField();
        TextField postalTextField = new TextField();
        TextField cityTextField = new TextField();
        TextField countryTextField = new TextField();

        Button saveButton = new Button("Save");

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(
                emailInputLabel, emailTextField,
                telephoneInputLabel, telephoneTextField,
                firstNameLabel, first_nameTextField,
                lastNameLabel, last_nameTextField,
                streetLabel, streetTextField,
                houseNumLabel, houseNumTextField,
                postalLabel, postalTextField,
                cityLabel, cityTextField,
                countryLabel, countryTextField,
                saveButton
        );

        vBox.setPadding(new javafx.geometry.Insets(10));

        saveButton.setOnAction(event -> {
            // Get the values from text fields
            String email = emailTextField.getText();
            String telephoneText = telephoneTextField.getText();
            String first_name = first_nameTextField.getText();
            String last_name = last_nameTextField.getText();
            String street = streetTextField.getText();
            String houseNumText = houseNumTextField.getText();
            String postalCodeText = postalTextField.getText();
            String city = cityTextField.getText();
            String country = countryTextField.getText();


            if (email.isEmpty() || telephoneText.isEmpty() || first_name.isEmpty() || last_name.isEmpty()
                    || street.isEmpty() || houseNumText.isEmpty() || postalCodeText.isEmpty() ||
                    city.isEmpty() || country.isEmpty()) {
                showAlert("Please fill in all fields.");
                return;
            }

            if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
                showAlert("Invalid email format");
                return;
            }

            if (first_name.matches(".*\\d.*") || last_name.matches(".*\\d.*")) {
                showAlert("Name cannot contain numbers");
                return;
            }

            if (!telephoneText.matches("\\d+")) {
                showAlert("Telephone must contain only numbers");
                return;
            }

            if (!houseNumText.matches("\\d+")) {
                showAlert("House number must contain only numbers");
                return;
            }

            if (!postalCodeText.matches("\\d+")) {
                showAlert("Postal code must contain only numbers");
                return;
            }

            int telephone = Integer.parseInt(telephoneText);
            int houseNum = Integer.parseInt(houseNumText);
            int postalCode = Integer.parseInt(postalCodeText);

            CustomerRepository customerRepository = new CustomerRepository();
            customerRepository.insertName(first_name, last_name, username);
            customerRepository.insertContact(email, telephone, username);
            customerRepository.insertAddress(street, houseNum, postalCode, city, country, username);

            contactStage.close();

            updateCustomerInfo();
        });

        contactStage.setScene(new javafx.scene.Scene(vBox));
        contactStage.setTitle("Insert Contact Information");
        contactStage.show();
    }

    public void editData(ActionEvent event) {
        List<String> choices = Arrays.asList("Change Contact", "Change Address");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Change Contact", choices);
        dialog.setTitle("Edit Data");
        dialog.setHeaderText("Select the data you want to edit:");
        dialog.setContentText("Choose:");


        Optional<String> result = dialog.showAndWait();

        result.ifPresent(choice -> {
            if ("Change Contact".equals(choice)) {
                TextInputDialog emailDialog = new TextInputDialog();
                emailDialog.setTitle("Change Contact");
                emailDialog.setHeaderText("Enter new email:");
                emailDialog.setContentText("Email:");

                Optional<String> newEmailResult = emailDialog.showAndWait();

                if (newEmailResult.isPresent()) {
                    String newEmail = newEmailResult.get();

                    TextInputDialog telephoneDialog = new TextInputDialog();
                    telephoneDialog.setTitle("Change Contact");
                    telephoneDialog.setHeaderText("Enter new telephone:");
                    telephoneDialog.setContentText("Telephone:");

                    Optional<String> newTelephoneResult = telephoneDialog.showAndWait();

                    if (newTelephoneResult.isPresent()) {
                        String newTelephone = newTelephoneResult.get();

                        CustomerRepository customerRepository = new CustomerRepository();
                        try {
                            customerRepository.updateContact(newEmail, Integer.parseInt(newTelephone), username);

                        } catch (DataAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }


            } else if ("Change Address".equals(choice)) {
                TextInputDialog streetDialog = new TextInputDialog();
                streetDialog.setTitle("Change Address");
                streetDialog.setHeaderText("Enter new street:");
                Optional<String> streetResult = streetDialog.showAndWait();

                if (streetResult.isPresent()) {
                    TextInputDialog houseNumDialog = new TextInputDialog();
                    houseNumDialog.setTitle("Change Address");
                    houseNumDialog.setHeaderText("Enter new house number:");
                    Optional<String> houseNumResult = houseNumDialog.showAndWait();

                    TextInputDialog postalCodeDialog = new TextInputDialog();
                    postalCodeDialog.setTitle("Change Address");
                    postalCodeDialog.setHeaderText("Enter new postal code:");
                    Optional<String> postalCodeResult = postalCodeDialog.showAndWait();

                    TextInputDialog cityDialog = new TextInputDialog();
                    cityDialog.setTitle("Change Address");
                    cityDialog.setHeaderText("Enter new city:");
                    Optional<String> cityResult = cityDialog.showAndWait();

                    TextInputDialog countryDialog = new TextInputDialog();
                    countryDialog.setTitle("Change Address");
                    countryDialog.setHeaderText("Enter new country:");
                    Optional<String> countryResult = countryDialog.showAndWait();

                    if (houseNumResult.isPresent() && postalCodeResult.isPresent()
                            && cityResult.isPresent() && countryResult.isPresent()) {
                        CustomerRepository customerRepository = new CustomerRepository();
                        customerRepository.updateAddress(streetResult.get(), Integer.parseInt(postalCodeResult.get()), Integer.parseInt(houseNumResult.get()), cityResult.get(), countryResult.get(), username);

                        updateCustomerInfo();
                    }
                }
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

