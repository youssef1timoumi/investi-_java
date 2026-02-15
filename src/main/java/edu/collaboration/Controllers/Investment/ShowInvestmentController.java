package edu.collaboration.controllers.Investment;

import edu.collaboration.entities.Investment;
import edu.collaboration.services.InvestmentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import edu.collaboration.Controllers.ActionButtonsController;

public class ShowInvestmentController {

    private final InvestmentService is = new InvestmentService();

    @FXML
    private TableView<Investment> investmentTable;
    @FXML
    private TableColumn<Investment, Integer> idCol;
    @FXML
    private TableColumn<Investment, Integer> projectCol;
    @FXML
    private TableColumn<Investment, Integer> investorCol;
    @FXML
    private TableColumn<Investment, Double> amountCol;
    @FXML
    private TableColumn<Investment, Integer> durationCol;
    @FXML
    private TableColumn<Investment, Double> equityCol;
    @FXML
    private TableColumn<Investment, String> statusCol;
    @FXML
    private TableColumn<Investment, Void> actionCol;

    @FXML
    void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("investmentId"));
        projectCol.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        investorCol.setCellValueFactory(new PropertyValueFactory<>("investorId"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        durationCol.setCellValueFactory(new PropertyValueFactory<>("durationMonths"));
        equityCol.setCellValueFactory(new PropertyValueFactory<>("equityRequested"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadData();
        addActionButtons();
    }

    private void loadData() {
        investmentTable.setItems(
                FXCollections.observableArrayList(is.getData()));
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
                            Investment i = getTableView().getItems().get(getIndex());
                            try {
                                javafx.fxml.FXMLLoader editLoader = new javafx.fxml.FXMLLoader(
                                        getClass().getResource("/UpdateInvestment.fxml"));
                                javafx.scene.Parent root = editLoader.load();

                                UpdateInvestmentController controllerInvest = editLoader.getController();
                                controllerInvest.initData(i);

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
                            Investment i = getTableView().getItems().get(getIndex());
                            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                                    "Delete Investment " + i.getInvestmentId() + "?", ButtonType.YES, ButtonType.NO);
                            confirm.showAndWait().ifPresent(type -> {
                                if (type == ButtonType.YES) {
                                    is.deleteEntity(i);
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
        navigate(event, "/AddInvestment.fxml");
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
