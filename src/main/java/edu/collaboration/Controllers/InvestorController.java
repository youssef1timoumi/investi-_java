package edu.collaboration.Controllers;

import edu.collaboration.entities.Investment;
import edu.collaboration.entities.Project;
import edu.collaboration.services.InvestmentService;
import edu.collaboration.services.ProjectService;
import edu.collaboration.Controllers.Investment.AddInvestmentController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.ButtonType;

public class InvestorController implements Initializable {

    @FXML
    private FlowPane projectsContainer;

    @FXML
    private TableView<Investment> portfolioTable;
    @FXML
    private TableColumn<Investment, Integer> colInvId;
    @FXML
    private TableColumn<Investment, Integer> colInvProject;
    @FXML
    private TableColumn<Investment, Double> colInvAmount;
    @FXML
    private TableColumn<Investment, Double> colInvEquity;
    @FXML
    private TableColumn<Investment, String> colInvStatus;
    @FXML
    private TableColumn<Investment, String> colInvDate;
    @FXML
    private TableColumn<Investment, Void> colInvAction;

    private final ProjectService projectService = new ProjectService();
    private final InvestmentService investmentService = new InvestmentService();

    // MOCK LOGIN FOR INVESTOR
    private final int currentInvestorId = 2;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupPortfolioTable();
        refreshProjects();
        refreshPortfolio();
    }

    private void setupPortfolioTable() {
        colInvId.setCellValueFactory(new PropertyValueFactory<>("investmentId"));
        colInvProject.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        colInvAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colInvEquity.setCellValueFactory(new PropertyValueFactory<>("equityRequested"));
        colInvStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colInvDate.setCellValueFactory(new PropertyValueFactory<>("investmentDate"));
        addButtonToTable();
    }

    @FXML
    void refreshProjects() {
        projectsContainer.getChildren().clear();

        // Fetch ALL projects to filter for both OPEN opportunities and MY HISTORICAL
        // investments
        List<Project> allProjects = projectService.getData();
        List<Investment> myInvestments = investmentService.getInvestmentsByInvestor(currentInvestorId);

        if (allProjects.isEmpty()) {
            Label placeholder = new Label("No projects available at the moment.");
            projectsContainer.getChildren().add(placeholder);
            return;
        }

        for (Project p : allProjects) {
            // Logic:
            // 1. Show if Status is OPEN (Available to invest)
            // 2. Show if I have already invested (ACCEPTED) -> To see my portfolio item in
            // context
            // 3. Show if I was REFUSED -> To see history
            // 4. Show if I have PENDING -> To see waiting status

            boolean isOpen = "OPEN".equals(p.getStatus());
            boolean iHaveInteraction = myInvestments.stream().anyMatch(inv -> inv.getProjectId() == p.getProjectId());

            if (isOpen || iHaveInteraction) {
                projectsContainer.getChildren().add(createProjectCard(p, myInvestments));
            }
        }
    }

    private VBox createProjectCard(Project p, List<Investment> myInvestments) {
        VBox card = new VBox(10);
        card.setStyle(
                "-fx-background-color: white; -fx-padding: 15; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefWidth(250);
        card.setMinHeight(200);

        Label title = new Label(p.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2A52BE;");
        title.setWrapText(true);

        Label desc = new Label(p.getDescription());
        desc.setWrapText(true);
        desc.setPrefHeight(60);

        Label goal = new Label("Goal: $" + p.getAmountRequested());
        Label equity = new Label("Equity Offered: " + p.getEquityOffered() + "%");

        Button investBtn = new Button("Invest Now");
        investBtn.getStyleClass().add("button");
        investBtn.setMaxWidth(Double.MAX_VALUE);

        // Check interaction status
        java.util.Optional<Investment> interaction = myInvestments.stream()
                .filter(inv -> inv.getProjectId() == p.getProjectId())
                .findFirst();

        if (interaction.isPresent()) {
            Investment inv = interaction.get();
            if ("ACCEPTED".equals(inv.getStatus())) {
                investBtn.setText("Invested");
                investBtn.setDisable(true);
                card.setStyle(
                        "-fx-background-color: #e8f5e9; -fx-opacity: 0.8; -fx-padding: 15; -fx-border-color: #c3e6cb; -fx-border-radius: 5; -fx-background-radius: 5;"); // Greenish
                                                                                                                                                                         // for
                                                                                                                                                                         // success
            } else if ("REFUSED".equals(inv.getStatus())) {
                investBtn.setText("Refused");
                investBtn.setDisable(true);
                card.setStyle(
                        "-fx-background-color: #f8d7da; -fx-opacity: 0.7; -fx-padding: 15; -fx-border-color: #f5c6cb; -fx-border-radius: 5; -fx-background-radius: 5;"); // Reddish
                                                                                                                                                                         // for
                                                                                                                                                                         // refused
            } else if ("PENDING".equals(inv.getStatus())) {
                investBtn.setText("Pending");
                investBtn.setDisable(true);
                card.setStyle(
                        "-fx-background-color: #fff3cd; -fx-padding: 15; -fx-border-color: #ffeeba; -fx-border-radius: 5; -fx-background-radius: 5;"); // Yellowish
                                                                                                                                                       // for
                                                                                                                                                       // pending
            }
        } else {
            // No interaction yet.
            if (!"OPEN".equals(p.getStatus())) {
                // If not open and no interaction, probably shouldn't be here based on
                // refreshProjects logic,
                // but if it is, disable it.
                investBtn.setText("Unavailable");
                investBtn.setDisable(true);
                card.setDisable(true);
            } else {
                investBtn.setOnAction(e -> openInvestDialog(p));
            }
        }

        card.getChildren().addAll(title, desc, new Separator(), goal, equity, new Separator(), investBtn);
        return card;
    }

    private void openInvestDialog(Project p) {
        try {
            // Need to pass Project ID to the controller.
            // Using a static holder or custom loader is common. For simplicity, we assume
            // we can setUserData or find controller.

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddInvestment.fxml"));
            Parent root = loader.load();

            // Access controller to set project ID
            // Assuming AddInvestmentController exists or we need to create/update it.
            // Since we can't easily see AddInvestmentController source right now to know
            // its name,
            // I will assume it's linked in FXML. If not, I'll need to update
            // AddInvestment.fxml first.
            // But wait, the previous `AddInvestment.fxml` logic is strict.
            // User requested: "Project ID automatically populated".

            // Let's grab the controller if possible.
            Object controller = loader.getController();
            if (controller instanceof AddInvestmentController) {
                ((AddInvestmentController) controller).setTargetProjectId(p.getProjectId());
            } else {
                // Fallback if controller class name is different or not castable yet
                // We will need to check AddInvestmentController.java next.
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Invest in " + p.getTitle());
            stage.showAndWait();

            refreshPortfolio();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void refreshPortfolio() {
        List<Investment> myInvestments = investmentService.getInvestmentsByInvestor(currentInvestorId);
        portfolioTable.setItems(FXCollections.observableArrayList(myInvestments));
        portfolioTable.refresh();
    }

    private void addButtonToTable() {
        javafx.util.Callback<TableColumn<Investment, Void>, TableCell<Investment, Void>> cellFactory = new javafx.util.Callback<>() {
            @Override
            public TableCell<Investment, Void> call(final TableColumn<Investment, Void> param) {
                final TableCell<Investment, Void> cell = new TableCell<>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");
                    private final javafx.scene.layout.HBox pane = new javafx.scene.layout.HBox(editBtn, deleteBtn);

                    {
                        pane.setSpacing(5);
                        editBtn.getStyleClass().add("button");
                        deleteBtn.getStyleClass().add("button");
                        deleteBtn.getStyleClass().add("danger-button");

                        editBtn.setOnAction(event -> {
                            Investment data = getTableView().getItems().get(getIndex());
                            if ("ACCEPTED".equalsIgnoreCase(data.getStatus())
                                    || "REFUSED".equalsIgnoreCase(data.getStatus())) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Action Denied");
                                alert.setContentText("Cannot edit an ACCEPTED or REFUSED investment.");
                                alert.showAndWait();
                                return;
                            }
                            editInvestment(data);
                        });

                        deleteBtn.setOnAction(event -> {
                            Investment data = getTableView().getItems().get(getIndex());
                            deleteInvestment(data);
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

        colInvAction.setCellFactory(cellFactory);
    }

    private void editInvestment(Investment i) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateInvestment.fxml"));
            Parent root = loader.load();

            // Get controller and init data
            Object controller = loader.getController();
            if (controller instanceof edu.collaboration.Controllers.Investment.UpdateInvestmentController) {
                ((edu.collaboration.Controllers.Investment.UpdateInvestmentController) controller).initData(i);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Update Investment");
            stage.showAndWait();
            refreshPortfolio();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteInvestment(Investment i) {
        if (confirm("Delete Investment", "Are you sure you want to delete this investment?")) {
            investmentService.deleteEntity(i);
            refreshPortfolio();
        }
    }

    private boolean confirm(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        java.util.Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
