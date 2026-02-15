package edu.collaboration.controllers.Project;

import edu.collaboration.entities.Project;
import edu.collaboration.services.ProjectService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import edu.collaboration.Controllers.ActionButtonsController;

import java.util.List;

public class ShowProjectController {

    private final ProjectService ps = new ProjectService();
    private ObservableList<Project> list;

    @FXML
    private TableView<Project> projectTable;
    @FXML
    private TableColumn<Project, Integer> idCol;
    @FXML
    private TableColumn<Project, String> titleCol;
    @FXML
    private TableColumn<Project, String> descCol;
    @FXML
    private TableColumn<Project, Double> amountCol;
    @FXML
    private TableColumn<Project, Double> equityCol;
    @FXML
    private TableColumn<Project, String> statusCol;
    @FXML
    private TableColumn<Project, Void> actionCol;

    @FXML
    void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amountRequested"));
        equityCol.setCellValueFactory(new PropertyValueFactory<>("equityOffered"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadData();
        addActionButtons();
    }

    private void loadData() {
        List<Project> projects = ps.getData();
        list = FXCollections.observableArrayList(projects);
        projectTable.setItems(list);
    }

    private void addActionButtons() {
        actionCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    try {
                        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                                getClass().getResource("/ActionButtons.fxml"));
                        javafx.scene.layout.HBox pane = loader.load();
                        ActionButtonsController controller = loader.getController();

                        controller.getEditBtn().setOnAction(e -> {
                            Project p = getTableView().getItems().get(getIndex());
                            try {
                                javafx.fxml.FXMLLoader editLoader = new javafx.fxml.FXMLLoader(
                                        getClass().getResource("/UpdateProject.fxml"));
                                javafx.scene.Parent root = editLoader.load();

                                UpdateProjectController updateController = editLoader.getController();
                                updateController.initData(p);

                                javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) e.getSource())
                                        .getScene()
                                        .getWindow();
                                stage.setScene(new javafx.scene.Scene(root));
                                stage.show();
                            } catch (java.io.IOException ex) {
                                ex.printStackTrace();
                            }
                        });

                        controller.getDeleteBtn().setOnAction(e -> {
                            Project p = getTableView().getItems().get(getIndex());
                            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + p.getTitle() + "?",
                                    ButtonType.YES, ButtonType.NO);
                            confirm.showAndWait().ifPresent(type -> {
                                if (type == ButtonType.YES) {
                                    ps.deleteEntity(p);
                                    loadData();
                                }
                            });
                        });

                        setGraphic(pane);
                    } catch (java.io.IOException ex) {
                        ex.printStackTrace();
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    void goMain(javafx.event.ActionEvent event) {
        navigate(event, "/Main.fxml");
    }

    @FXML
    void goAdd(javafx.event.ActionEvent event) {
        navigate(event, "/AddProject.fxml");
    }

    private void navigate(javafx.event.ActionEvent event, String fxmlPath) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene()
                    .getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
