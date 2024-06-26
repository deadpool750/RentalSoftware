package com.example.rentalsoftware;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import com.google.gson.Gson;

public class SecondSceneController {
    private Stage stage;
    private Scene scene3;

    public void init(Stage stage, Scene scene3) {
        this.stage = stage;
        this.scene3 = scene3;
    }

    @FXML
    public void initialize() {
        carList.getItems().clear();
        reservedCarList.getItems().clear();
        try (FileReader fileReader = new FileReader("vehicles.json")) {
            JsonReader jsonReader = new JsonReader(fileReader);
            Gson gson = new Gson();
            Type carListType = new TypeToken<List<Car>>(){}.getType();
            all = gson.fromJson(jsonReader, carListType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Car car : all) {
            if(!car.isRented()) {
                carList.getItems().add(car.toString());
            } else {
                reservedCarList.getItems().add(car.toString());
            }
        }
    }

    private List<Car> all;
    private Vehicle clickedCar;

    @FXML
    private Label availableLabel;

    @FXML
    private ListView<String> carList;

    @FXML
    private ListView<String> reservedCarList;

    @FXML
    private Label reservedLabel;

    @FXML
    private TextField searchTextField;

    @FXML
    private TextField searchTextField1;

    @FXML
    private Button backButton;

    @FXML
    private void back() throws IOException {
        Stage primaryStage = (Stage) backButton.getScene().getWindow();
        primaryStage.setScene(new Scene(new FXMLLoader(getClass().getResource("first-scene.fxml")).load()));
    }

    @FXML
    private void exit() {
        Platform.exit();
    }

    @FXML
    private void goToInvoice() {
        stage.setScene(scene3);
    }

    @FXML
    void listMouseClick(MouseEvent event) {
        String selected = carList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String[] parts = selected.split(" ");
            if (parts.length >= 7) {
                String chosen = parts[6];
                for (Car car : all) {
                    if (chosen.equals(car.getLicensePlate())) {
                        clickedCar = car;
                        break;
                    }
                }
                if (clickedCar != null) {
                    searchTextField.setText(clickedCar.toString());
                    if (clickedCar.isRented()) {
                        availableLabel.setText("This car is already reserved");
                    } else {
                        availableLabel.setText("This car is available for reservation");
                    }
                } else {
                    availableLabel.setText("Clicked car not found.");
                }
            } else {
                availableLabel.setText("Selected item does not contain enough parts.");
            }
        } else {
            availableLabel.setText("No item selected.");
        }
    }

    @FXML
    void listScrolled(ScrollEvent event) {
        // Add functionality to handle list scroll event
    }

    @FXML
    private void reservation() {
        if (clickedCar == null) {
            availableLabel.setText("Please choose a car");
            return;
        }
        if (clickedCar.isRented()) {
            availableLabel.setText("This car is already reserved");
            return;
        }
        clickedCar.setRented(true);
        clickedCar.setRentedDays(1);
        availableLabel.setText("Car reserved");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(all);

        try (FileWriter fileWriter = new FileWriter("vehicles.json")) {
            fileWriter.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initialize();
    }

    @FXML
    private void handleReserveButtonAction() {
        if (clickedCar == null) {
            availableLabel.setText("Please choose a car to reserve");
            return;
        }
        if (clickedCar.isRented()) {
            availableLabel.setText("This car is already reserved");
            return;
        }
        clickedCar.setRented(true);
        reservedCarList.getItems().add(clickedCar.toString());
        carList.getItems().remove(clickedCar.toString());

        availableLabel.setText("Car reserved successfully");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(all);

        try (FileWriter fileWriter = new FileWriter("vehicles.json")) {
            fileWriter.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleReturnButtonAction() {
        if (clickedCar == null) {
            availableLabel.setText("Please choose a car to return");
            return;
        }
        if (!clickedCar.isRented()) {
            availableLabel.setText("This car hasn't been rented");
            return;
        }
        clickedCar.setRented(false);
        carList.getItems().add(clickedCar.toString());
        reservedCarList.getItems().remove(clickedCar.toString());

        availableLabel.setText("Car returned successfully");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(all);

        try (FileWriter fileWriter = new FileWriter("vehicles.json")) {
            fileWriter.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // New method to handle the return button click
    /*@FXML
    private void handleReturnButtonAction() {
        String selected = reservedCarList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String[] parts = selected.split(" ");
            if (parts.length >= 7) {
                String chosen = parts[6];
                clickedCar = null; // Reset clickedCar before searching
                for (Car car : all) {
                    if (chosen.equals(car.getLicensePlate())) {
                        clickedCar = car;
                        break;
                    }
                }

                if (clickedCar == null) {
                    availableLabel.setText("Car not found.");
                    return;
                }

                if (!clickedCar.isRented()) {
                    availableLabel.setText("This car hasn't been rented.");
                    return;
                }

                clickedCar.setRented(false);
                clickedCar.setRentedDays(0);
                reservedCarList.getItems().remove(selected);
                carList.getItems().add(clickedCar.toString());

                availableLabel.setText("Car returned successfully");

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(all);

                try (FileWriter fileWriter = new FileWriter("vehicles.json")) {
                    fileWriter.write(json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                availableLabel.setText("Selected item does not contain enough parts.");
            }
        } else {
            availableLabel.setText("No item selected.");
        }
    }*/
}
