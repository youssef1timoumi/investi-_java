package controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import models.User;
import services.UserService;

public class ShowUsersController {
    private final UserService us = new UserService();
    private ObservableList<User> observableList;


    @FXML
    private TableColumn<User, Void> actions;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableColumn<User, Integer> ageCol;

    @FXML
    private TableColumn<User, String> firstNameCol;

    @FXML
    private TableColumn<User, String> lastNameCol;

    @FXML
    private TableView<User> tabId;


    @FXML
    void initialize() {
        refreshData();
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        addButtonToTable();
    }

    private void addButtonToTable() {
        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                final TableCell<User, Void> cell = new TableCell<>() {

                    private final Button btn = new Button("Del");

                    {
                        btn.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            try {
                                us.delete(user.getId());
                                refreshData();
                            } catch (Exception e) {
                                showAlert("Error deleting user", e.getMessage());
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);

                        }
                    }
                };
                return cell;
            }
        };

        actions.setCellFactory(cellFactory);
    }


    public void refreshData() {
        try {
            List<User> userList = us.read();
            observableList = FXCollections.observableList(userList);
            tabId.setItems(observableList);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error showing data ");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }


}