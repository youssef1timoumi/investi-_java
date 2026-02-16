package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import models.Sale;
import services.SaleService;

import java.sql.SQLException;
import java.util.List;

public class ShowSalesController {
    private final SaleService ss = new SaleService();
    private ObservableList<Sale> observableList;

    @FXML
    private TableView<Sale> saleTable;
    @FXML
    private TableColumn<Sale, String> refCol;
    @FXML
    private TableColumn<Sale, java.sql.Timestamp> dateCol;
    @FXML
    private TableColumn<Sale, Double> amountCol;
    @FXML
    private TableColumn<Sale, String> statusCol;
    @FXML
    private TableColumn<Sale, String> paymentStatusCol;
    @FXML
    private TableColumn<Sale, String> methodCol;
    @FXML
    private TableColumn<Sale, String> addressCol;
    @FXML
    private TableColumn<Sale, String> notesCol;
    @FXML
    private TableColumn<Sale, Void> actionsCol;

    @FXML
    void initialize() {
        refCol.setCellValueFactory(new PropertyValueFactory<>("reference"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        paymentStatusCol.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        methodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("shippingAddress"));
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));

        addButtonToTable();
        refreshData();
    }

    private void addButtonToTable() {
        Callback<TableColumn<Sale, Void>, TableCell<Sale, Void>> cellFactory = param -> new TableCell<>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final javafx.scene.layout.HBox container = new javafx.scene.layout.HBox(10, editBtn, deleteBtn);

            {
                // Premium Styling for Table Buttons
                editBtn.setStyle(
                        "-fx-background-color: #6366f1; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand; -fx-padding: 5 12;");
                deleteBtn.setStyle(
                        "-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand; -fx-padding: 5 12;");

                editBtn.setOnAction(event -> {
                    Sale sale = getTableView().getItems().get(getIndex());
                    AddSaleController.saleToEdit = sale;
                    DashboardController.getInstance().loadView("/AddSale.fxml", "Modifier la Vente");
                });

                deleteBtn.setOnAction(event -> {
                    Sale sale = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirmation de suppression");
                    confirm.setHeaderText(null);
                    confirm.setContentText("Voulez-vous vraiment supprimer la vente " + sale.getReference() + " ?");

                    if (confirm.showAndWait().get() == ButtonType.OK) {
                        try {
                            ss.delete((int) sale.getId());
                            refreshData();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            showAlert("Erreur de suppression", "Impossible de supprimer la vente : " + e.getMessage());
                        }
                    }
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        };
        actionsCol.setCellFactory(cellFactory);
    }

    public void refreshData() {
        try {
            List<Sale> saleList = ss.read();
            observableList = FXCollections.observableArrayList(saleList);
            saleTable.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Loading Error", "Failed to refresh sales tracking list: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void navigateAddSale() {
        AddSaleController.saleToEdit = null;
        DashboardController.getInstance().loadView("/AddSale.fxml", "Create New Sale");
    }

    @FXML
    void navigateShowProducts() {
        DashboardController.getInstance().showProducts();
    }
}
