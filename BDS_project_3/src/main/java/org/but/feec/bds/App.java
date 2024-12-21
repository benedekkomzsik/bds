package org.but.feec.bds;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.but.feec.bds.exceptions.ExceptionHandler;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/intro.fxml"));
            VBox mainStage = loader.load();

            primaryStage.setTitle("Enter Screen");
            Scene scene = new Scene(mainStage);
            primaryStage.setScene(scene);
            primaryStage.show();


        }catch (Exception ex){
            ExceptionHandler.handleException(ex);
        }
    }
}
