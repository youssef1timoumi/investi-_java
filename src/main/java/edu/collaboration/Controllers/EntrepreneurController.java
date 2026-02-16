package edu.collaboration.Controllers;

import edu.collaboration.entities.Investment;
import edu.collaboration.entities.Project;
import edu.collaboration.services.InvestmentService;
import edu.collaboration.services.ProjectService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class EntrepreneurController implements Initializable {

    // --- My Projects Table ---
    @FXML
    private TableView<Project> myProjectsTable;
    @FXML
    private TableColumn<Project, Integer> colProjId;
    @FXML
    private TableColumn<Project, String> colProjTitle;
    @FXML
    private TableColumn<Project, String> colProjDesc;
    @FXML
    private TableColumn<Project, Double> colProjAmount;
    @FXML
    private TableColumn<Project, Double> colProjEquity;
    @FXML
    private TableColumn<Project, String> colProjStatus;
    @FXML
    private TableColumn<Project, Void> colProjAction;

    // --- Offers Table ---
    @FXML
    private TableView<Investment> offersTable;
    @FXML
    private TableColumn<Investment, Integer> colOfferId;
    @FXML
    private TableColumn<Investment, Integer> colOfferProject;
    @FXML
    private TableColumn<Investment, Integer> colOfferInvestor;
    @FXML
    private TableColumn<Investment, Double> colOfferAmount;
    @FXML
    private TableColumn<Investment, Double> colOfferEquity;
    @FXML
    private TableColumn<Investment, Integer> colOfferDuration;

    private final ProjectService projectService = new ProjectService();
    private final InvestmentService investmentService = new InvestmentService();

    // MOCK LOGIN FOR ENTREPRENEUR
    private final int currentEntrepreneurId = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTables();
        refreshData();
    }

    private void setupTables() {
        // Projects
        colProjId.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        colProjTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colProjDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colProjAmount.setCellValueFactory(new PropertyValueFactory<>("amountRequested"));
        colProjEquity.setCellValueFactory(new PropertyValueFactory<>("equityOffered"));
        colProjStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        addButtonToTable();

        // Offers
        colOfferId.setCellValueFactory(new PropertyValueFactory<>("investmentId"));
        colOfferProject.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        colOfferInvestor.setCellValueFactory(new PropertyValueFactory<>("investorId"));
        colOfferAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colOfferEquity.setCellValueFactory(new PropertyValueFactory<>("equityRequested"));
        colOfferDuration.setCellValueFactory(new PropertyValueFactory<>("durationMonths"));

        // Highlight Accepted Offers
        offersTable.setRowFactory(tv -> new TableRow<Investment>() {
            @Override
            protected void updateItem(Investment item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    if ("ACCEPTED".equals(item.getStatus())) {
                        setStyle("-fx-background-color: #d4edda;"); // Light Green
                    } else if ("REFUSED".equals(item.getStatus())) {
                        setStyle("-fx-background-color: #f8d7da; -fx-opacity: 0.6;"); // Red tint / Grey
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }

    @FXML
    void refreshData() {
        // 1. Get My Projects
        List<Project> myProjects = projectService.getProjectsByEntrepreneur(currentEntrepreneurId);
        myProjectsTable.setItems(FXCollections.observableArrayList(myProjects));

        // 2. Get Offers (Investments suitable for my projects needing attention or
        // history)
        List<Investment> visibleOffers = new ArrayList<>();

        // Fetch PENDING and ACCEPTED investments
        List<Investment> pending = investmentService.getInvestmentsByStatus("PENDING");
        List<Investment> accepted = investmentService.getInvestmentsByStatus("ACCEPTED");
        List<Investment> refused = investmentService.getInvestmentsByStatus("REFUSED"); // NEW: Show refused too? User
                                                                                        // said "the rest turns grey"

        List<Investment> allRelevant = new ArrayList<>();
        allRelevant.addAll(pending);
        allRelevant.addAll(accepted);
        allRelevant.addAll(refused);

        for (Investment inv : allRelevant) {
            // Check if this investment targets one of my projects
            boolean isMine = myProjects.stream().anyMatch(p -> p.getProjectId() == inv.getProjectId());
            if (isMine) {
                visibleOffers.add(inv);
            }
        }

        offersTable.setItems(FXCollections.observableArrayList(visibleOffers));
        myProjectsTable.refresh();
        offersTable.refresh();
    }

    @FXML
    void createNewProject(javafx.event.ActionEvent event) {
        try {
            // Load AddProject view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddProject.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Create New Project");
            stage.showAndWait(); // Wait to refresh after close
            refreshData();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load Create Project view: " + e.getMessage());
        }
    }

    @FXML
    void acceptOffer() {
        Investment selectedOffer = offersTable.getSelectionModel().getSelectedItem();
        if (selectedOffer == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an offer to accept.");
            return;
        }

        // CHECK: Is project already funded/accepted?
        boolean alreadyFunded = offersTable.getItems().stream()
                .anyMatch(inv -> inv.getProjectId() == selectedOffer.getProjectId()
                        && "ACCEPTED".equals(inv.getStatus()));

        if (alreadyFunded) {
            showAlert(Alert.AlertType.ERROR, "Project Funded",
                    "This project already has an accepted investment. You cannot accept another.");
            return;
        }

        if (confirm("Accept Offer",
                "Are you sure? This will FUND your project and REJECT all other pending offers for it.")) {
            boolean success = investmentService.acceptInvestment(selectedOffer.getInvestmentId(),
                    selectedOffer.getProjectId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Congratulations!",
                        "You have accepted the investment. Project is now FUNDED.");
                refreshData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to process acceptance.");
            }
        }
    }

    @FXML
    void declineOffer() {
        Investment selectedOffer = offersTable.getSelectionModel().getSelectedItem();
        if (selectedOffer == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an offer to decline.");
            return;
        }

        if (confirm("Decline Offer", "Are you sure you want to decline this offer?")) {
            selectedOffer.setStatus("REFUSED");
            if (investmentService.update(selectedOffer.getInvestmentId(), selectedOffer)) {
                refreshData();
            }
        }
    }

    private void addButtonToTable() {
        javafx.util.Callback<TableColumn<Project, Void>, TableCell<Project, Void>> cellFactory = new javafx.util.Callback<>() {
            @Override
            public TableCell<Project, Void> call(final TableColumn<Project, Void> param) {
                final TableCell<Project, Void> cell = new TableCell<>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");
                    private final javafx.scene.layout.HBox pane = new javafx.scene.layout.HBox(editBtn, deleteBtn);

                    {
                        pane.setSpacing(5);
                        editBtn.getStyleClass().add("button");
                        deleteBtn.getStyleClass().add("button");
                        deleteBtn.getStyleClass().add("danger-button");

                        editBtn.setOnAction(event -> {
                            Project data = getTableView().getItems().get(getIndex());
                            if ("FUNDED".equalsIgnoreCase(data.getStatus())
                                    || "CLOSED".equalsIgnoreCase(data.getStatus())) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Action Denied");
                                alert.setContentText("Cannot edit a FUNDED or CLOSED project.");
                                alert.showAndWait();
                                return;
                            }
                            editProject(data);
                        });

                        deleteBtn.setOnAction(event -> {
                            Project data = getTableView().getItems().get(getIndex());
                            deleteProject(data);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
                return cell;
            }
        };

        if (colProjAction != null) {
            colProjAction.setCellFactory(cellFactory);
        } else {
            System.err.println("CRITICAL ERROR: colProjAction is null during initialization.");
        }
    }

    private void editProject(Project p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateProject.fxml"));
            Parent root = loader.load();

            // Get controller and init data
            edu.collaboration.Controllers.Project.UpdateProjectController controller = loader.getController();
            controller.initData(p);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Update Project");
            stage.showAndWait();
            refreshData();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load update view: " + e.getMessage());
        }
    }

    private void deleteProject(Project p) {
        if (confirm("Delete Project", "Are you sure you want to delete '" + p.getTitle() + "'?")) {
            projectService.deleteEntity(p);
            refreshData();
        }
    }

    private boolean confirm(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
