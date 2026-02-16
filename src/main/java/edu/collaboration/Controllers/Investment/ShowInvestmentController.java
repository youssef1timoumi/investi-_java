package edu.collaboration.Controllers.Investment;

import edu.collaboration.entities.Investment;
import edu.collaboration.services.InvestmentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import edu.collaboration.Controllers.ActionButtonsController;

import java.io.IOException;
import java.util.List;

public class ShowInvestmentController {

    private final InvestmentService is = new InvestmentService();
    private ObservableList<Investment> list;

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
        List<Investment> investments = is.getData();
        list = FXCollections.observableArrayList(investments);
        investmentTable.setItems(list);
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
                            Investment inv = getTableView().getItems().get(getIndex());
                            try {
                                javafx.fxml.FXMLLoader editLoader = new javafx.fxml.FXMLLoader(
                                        getClass().getResource("/UpdateInvestment.fxml"));
                                Parent root = editLoader.load();

                                UpdateInvestmentController updateController = editLoader.getController();
                                updateController.initData(inv);

                                Stage stage = new Stage();
                                stage.setScene(new Scene(root));
                                stage.initModality(Modality.APPLICATION_MODAL);
                                stage.showAndWait();
                                loadData(); // Refresh after edit
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        });

                        controller.getDeleteBtn().setOnAction(e -> {
                            Investment inv = getTableView().getItems().get(getIndex());
                            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                                    "Delete Investment " + inv.getInvestmentId() + "?",
                                    ButtonType.YES, ButtonType.NO);
                            confirm.showAndWait().ifPresent(type -> {
                                if (type == ButtonType.YES) {
                                    is.deleteEntity(inv);
                                    loadData();
                                }
                            });
                        });

                        setGraphic(pane);
                    } catch (IOException ex) {
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
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/Main.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void goAdd(javafx.event.ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/AddInvestment.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadData(); // Refresh on close
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
