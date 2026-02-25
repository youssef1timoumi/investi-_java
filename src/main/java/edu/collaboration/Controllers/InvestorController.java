package edu.collaboration.Controllers;

import edu.collaboration.entities.Investment;
import edu.collaboration.entities.Project;
import edu.collaboration.services.CurrencyService;
import edu.collaboration.services.InvestmentService;
import edu.collaboration.services.ProjectService;
import edu.collaboration.services.AiService;
import edu.collaboration.services.PdfExportService;

import edu.collaboration.Controllers.Investment.AddInvestmentController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.awt.Desktop;
import java.io.File;

public class InvestorController implements Initializable {

    // ─── Main Pages ────────────────────────────────────────────────────────────
    @FXML
    private VBox browsePage;
    @FXML
    private VBox portfolioPage;
    @FXML
    private VBox collaborationPage;

    // ─── Sidebar Navigation Buttons ─────────────────────────────────────────────
    @FXML
    private Button navBrowse;
    @FXML
    private Button navPortfolio;
    @FXML
    private Button navCollaboration;

    // ─── Browse Page Components ──────────────────────────────────────────────────
    @FXML
    private FlowPane projectsContainer;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> categoryFilterBox;
    @FXML
    private ComboBox<String> sortBox;
    @FXML
    private ComboBox<String> currencyBox;

    // ─── Portfolio Page Components ───────────────────────────────────────────────
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

    @FXML
    private Button btnAiAdvisor;
    @FXML
    private Label aiAdvisorOutput;

    // ─── Collaboration Page Components ───────────────────────────────────────────
    @FXML
    private VBox collaborationContainer;

    // ─── Detail Overlay Components ───────────────────────────────────────────────
    @FXML
    private VBox detailOverlay;
    @FXML
    private Label detailTitle;
    @FXML
    private Label detailCategory;
    @FXML
    private Label detailStatus;
    @FXML
    private Label detailGoal;
    @FXML
    private Label detailEquity;
    @FXML
    private Label detailCreator;
    @FXML
    private Label detailDesc;
    @FXML
    private Button btnAiExplain;
    @FXML
    private Button btnInvestNow;

    private final ProjectService projectService = new ProjectService();
    private final InvestmentService investmentService = new InvestmentService();

    // MOCK LOGIN
    private final int currentInvestorId = 2;

    private List<Project> allProjects;
    private List<Investment> myInvestments;

    private Project selectedOverlayProject;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupPortfolioTable();

        // Setup filter boxes
        categoryFilterBox
                .setItems(FXCollections.observableArrayList("All", "Tech", "Health", "Education", "Finance", "Other"));
        categoryFilterBox.setValue("All");

        sortBox.setItems(FXCollections.observableArrayList("Newest First", "Amount: High→Low", "Amount: Low→High",
                "Equity: High→Low", "Equity: Low→High"));
        sortBox.setValue("Newest First");

        currencyBox.setItems(FXCollections.observableArrayList(CurrencyService.getSupportedCurrencies()));
        currencyBox.setValue("USD");

        // Listeners for dynamic filtering
        searchField.textProperty().addListener((obs, o, n) -> applyFilters());
        categoryFilterBox.setOnAction(e -> applyFilters());
        sortBox.setOnAction(e -> applyFilters());
        currencyBox.setOnAction(e -> applyFilters());

        refreshProjects();
        refreshPortfolio();
        buildCollaborationPage();
    }

    // ─── Sidebar Navigation Methods ──────────────────────────────────────────────

    @FXML
    void showBrowsePage() {
        togglePage(browsePage, navBrowse);
    }

    @FXML
    void showPortfolioPage() {
        togglePage(portfolioPage, navPortfolio);
    }

    @FXML
    void showCollaborationPage() {
        togglePage(collaborationPage, navCollaboration);
        buildCollaborationPage();
    }

    private void togglePage(VBox pageToShow, Button activeNavBtn) {
        browsePage.setVisible(false);
        browsePage.setManaged(false);
        portfolioPage.setVisible(false);
        portfolioPage.setManaged(false);
        collaborationPage.setVisible(false);
        collaborationPage.setManaged(false);

        navBrowse.getStyleClass().remove("active");
        navPortfolio.getStyleClass().remove("active");
        navCollaboration.getStyleClass().remove("active");

        pageToShow.setVisible(true);
        pageToShow.setManaged(true);
        activeNavBtn.getStyleClass().add("active");
    }

    @FXML
    void logout() {
        // Mock logout
        System.out.println("Logging out...");
        Platform.exit();
    }

    // ─── Detail Overlay Logic ───────────────────────────────────────────────────

    private void showProjectDetails(Project p, Investment interaction) {
        selectedOverlayProject = p;
        detailTitle.setText(p.getTitle());
        detailCategory.setText(p.getCategory() != null ? p.getCategory() : "Other");
        detailStatus.setText(p.getStatus());

        String dispCurr = currencyBox.getValue() != null ? currencyBox.getValue() : "USD";
        double convAmt = CurrencyService.convertFromUSD(p.getAmountRequested(), dispCurr);
        detailGoal.setText(CurrencyService.format(convAmt, dispCurr));

        detailEquity.setText(p.getEquityOffered() + "%");
        detailCreator.setText("#" + p.getEntrepreneurId());
        detailDesc.setText(p.getDescription());

        // Buttons configuration
        btnInvestNow.setDisable(false);
        btnInvestNow.setText("💼 Add Investment Offer");
        btnAiExplain.setDisable(false);
        btnAiExplain.setText("🤖 AI Setup Explanation & PDF");

        if (interaction != null) {
            btnInvestNow.setDisable(true);
            btnInvestNow.setText(interaction.getStatus());
            btnAiExplain.setDisable(true); // Once invested, no need to pitch it to them
        } else if (!"OPEN".equals(p.getStatus())) {
            btnInvestNow.setDisable(true);
            btnInvestNow.setText("Unavailable");
        }

        detailOverlay.setVisible(true);
    }

    @FXML
    void hideProjectDetails() {
        detailOverlay.setVisible(false);
    }

    @FXML
    void investInOverlayProject() {
        if (selectedOverlayProject != null) {
            openInvestDialog(selectedOverlayProject);
        }
    }

    @FXML
    void generateAiPdfReport() {
        if (selectedOverlayProject == null)
            return;

        btnAiExplain.setDisable(true);
        btnAiExplain.setText("Generating Report... ⏳");

        // Background thread to perform API call & PDF creation without freezing UI
        new Thread(() -> {
            String explanation = AiService.generateProjectExplanation(selectedOverlayProject);
            String pdfPath = PdfExportService.generateProjectAiReport(selectedOverlayProject, explanation);

            Platform.runLater(() -> {
                btnAiExplain.setDisable(false);
                btnAiExplain.setText("✅ Open PDF Report");
                btnAiExplain.setOnAction(e -> {
                    if (pdfPath != null) {
                        try {
                            Desktop.getDesktop().open(new File(pdfPath));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                // Alert completion
                new Alert(Alert.AlertType.INFORMATION, "PDF generation successful! Click the button again to view it.")
                        .show();
            });
        }).start();
    }

    // ─── Filtering & Project Cards ──────────────────────────────────────────────

    private void applyFilters() {
        if (allProjects == null)
            return;
        String keyword = searchField.getText().trim().toLowerCase();
        String category = categoryFilterBox.getValue();
        String sort = sortBox.getValue();

        List<Project> filtered = allProjects.stream()
                .filter(p -> "OPEN".equals(p.getStatus())
                        || myInvestments.stream().anyMatch(inv -> inv.getProjectId() == p.getProjectId()))
                .filter(p -> keyword.isEmpty() || p.getTitle().toLowerCase().contains(keyword)
                        || p.getDescription().toLowerCase().contains(keyword))
                .filter(p -> category == null || category.equals("All") || category.equals(p.getCategory()))
                .collect(Collectors.toList());

        if (sort != null) {
            switch (sort) {
                case "Amount: High→Low" ->
                    filtered.sort((a, b) -> Double.compare(b.getAmountRequested(), a.getAmountRequested()));
                case "Amount: Low→High" ->
                    filtered.sort((a, b) -> Double.compare(a.getAmountRequested(), b.getAmountRequested()));
                case "Equity: High→Low" ->
                    filtered.sort((a, b) -> Double.compare(b.getEquityOffered(), a.getEquityOffered()));
                case "Equity: Low→High" ->
                    filtered.sort((a, b) -> Double.compare(a.getEquityOffered(), b.getEquityOffered()));
            }
        }
        renderProjectCards(filtered);
    }

    @FXML
    void refreshProjects() {
        allProjects = projectService.getData();
        refreshPortfolio();
        applyFilters();
    }

    private void renderProjectCards(List<Project> projects) {
        projectsContainer.getChildren().clear();
        String dispCurr = currencyBox.getValue() != null ? currencyBox.getValue() : "USD";

        if (projects.isEmpty()) {
            projectsContainer.getChildren().add(new Label("No projects found under this search."));
            return;
        }

        for (Project p : projects) {
            VBox card = new VBox(15);
            card.getStyleClass().add("project-card");

            HBox header = new HBox(10);
            Label catBadge = new Label(p.getCategory() != null ? p.getCategory() : "Other");
            catBadge.getStyleClass().add("category-badge");

            Label title = new Label(p.getTitle());
            title.getStyleClass().add("card-title");

            header.getChildren().addAll(catBadge, title);

            Label desc = new Label(p.getDescription());
            desc.getStyleClass().add("card-desc");
            desc.setWrapText(true);
            desc.setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);

            double convAmt = CurrencyService.convertFromUSD(p.getAmountRequested(), dispCurr);
            Label goal = new Label("Goal: " + CurrencyService.format(convAmt, dispCurr));
            goal.getStyleClass().add("card-price");

            java.util.Optional<Investment> interaction = myInvestments.stream()
                    .filter(inv -> inv.getProjectId() == p.getProjectId()).findFirst();

            if (interaction.isPresent()) {
                Label interactBadge = new Label(interaction.get().getStatus());
                interactBadge.setStyle("-fx-font-weight: bold; -fx-text-fill: #A62639;");
                card.getChildren().addAll(header, desc, goal, interactBadge);
            } else {
                // Button for AI recommendation inline
                Button aiAdvisorBtn = new Button("Quick AI Advice?");
                aiAdvisorBtn.setStyle(
                        "-fx-background-color: transparent; -fx-text-fill: #9B7E46; -fx-cursor: hand; -fx-underline: true;");
                aiAdvisorBtn.setOnAction(e -> {
                    aiAdvisorBtn.setText("Thinking...");
                    aiAdvisorBtn.setDisable(true);
                    new Thread(() -> {
                        String advice = AiService.evaluateProjectForInvestor(p);
                        Platform.runLater(() -> {
                            aiAdvisorBtn.setText(advice);
                            aiAdvisorBtn.setWrapText(true);
                        });
                    }).start();
                });

                card.getChildren().addAll(header, desc, goal, aiAdvisorBtn);
            }

            // Hover Overlay click logic
            card.setOnMouseClicked(e -> showProjectDetails(p, interaction.isPresent() ? interaction.get() : null));

            projectsContainer.getChildren().add(card);
        }
    }

    // ─── Portfolio Table Logic ──────────────────────────────────────────────────

    private void setupPortfolioTable() {
        colInvId.setCellValueFactory(new PropertyValueFactory<>("investmentId"));
        colInvProject.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        colInvAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colInvEquity.setCellValueFactory(new PropertyValueFactory<>("equityRequested"));
        colInvStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colInvDate.setCellValueFactory(new PropertyValueFactory<>("investmentDate"));
        addTableButtons();
    }

    @FXML
    void refreshPortfolio() {
        myInvestments = investmentService.getInvestmentsByInvestor(currentInvestorId);
        if (portfolioTable != null) {
            portfolioTable.setItems(FXCollections.observableArrayList(myInvestments));
            portfolioTable.refresh();
        }
    }

    private void addTableButtons() {
        colInvAction.setCellFactory(param -> new TableCell<>() {
            private final Button cancelBtn = new Button("Cancel Offer");
            {
                cancelBtn.getStyleClass().add("ai-btn");
                cancelBtn.setOnAction(e -> {
                    Investment data = getTableView().getItems().get(getIndex());
                    if ("ACCEPTED".equalsIgnoreCase(data.getStatus()) || "REFUSED".equalsIgnoreCase(data.getStatus())) {
                        new Alert(Alert.AlertType.ERROR, "Cannot cancel an ACCEPTED or REFUSED offer.").show();
                        return;
                    }
                    if (confirm("Delete Offer", "Are you sure you want to cancel this offer?")) {
                        investmentService.deleteEntity(data);
                        refreshPortfolio();
                    }
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : cancelBtn);
            }
        });
    }

    @FXML
    void askAiAdvisor() {
        btnAiAdvisor.setText("Thinking... ⏳");
        btnAiAdvisor.setDisable(true);
        aiAdvisorOutput.setVisible(true);
        aiAdvisorOutput.setManaged(true);
        aiAdvisorOutput.setText("Analyzing your portfolio and the current market...");

        new Thread(() -> {
            StringBuilder pastContext = new StringBuilder();
            if (myInvestments.isEmpty()) {
                pastContext.append(
                        "The investor has no past investments yet. They are looking for their very first opportunity.");
            } else {
                for (Investment i : myInvestments) {
                    Project p = allProjects.stream().filter(proj -> proj.getProjectId() == i.getProjectId()).findFirst()
                            .orElse(null);
                    if (p != null) {
                        pastContext.append("- ").append(p.getTitle()).append(" (Category: ")
                                .append(p.getCategory() != null ? p.getCategory() : "Other").append("). Invested: $")
                                .append(i.getTotalAmount()).append("\n");
                    }
                }
            }

            StringBuilder openContext = new StringBuilder();
            List<Project> openProjects = allProjects.stream()
                    .filter(p -> "OPEN".equals(p.getStatus()))
                    .limit(10)
                    .collect(Collectors.toList());

            if (openProjects.isEmpty()) {
                openContext.append("There are currently no open projects available for investment.");
            } else {
                for (Project p : openProjects) {
                    openContext.append("- [ID: ").append(p.getProjectId()).append("] ").append(p.getTitle())
                            .append(" (Category: ").append(p.getCategory() != null ? p.getCategory() : "Other")
                            .append(")\n");
                }
            }

            String advice = AiService.getPortfolioRecommendations(pastContext.toString(), openContext.toString());

            Platform.runLater(() -> {
                btnAiAdvisor.setText("🤖 Refresh AI Advice");
                btnAiAdvisor.setDisable(false);
                aiAdvisorOutput.setText(advice);
            });
        }).start();
    }

    // ─── Collaboration Page Logic ───────────────────────────────────────────────

    private void buildCollaborationPage() {
        if (collaborationContainer == null)
            return;
        collaborationContainer.getChildren().clear();

        List<Investment> activeCollabs = myInvestments.stream()
                .filter(i -> "ACCEPTED".equalsIgnoreCase(i.getStatus()))
                .collect(Collectors.toList());

        if (activeCollabs.isEmpty()) {
            collaborationContainer.getChildren()
                    .add(new Label("You have no active collaborations yet. Find a project to fund!"));
            return;
        }

        for (Investment i : activeCollabs) {
            VBox card = new VBox(15);
            card.getStyleClass().add("progress-card");

            Label title = new Label("Project ID #" + i.getProjectId());
            title.getStyleClass().add("progress-title");

            // Progress Bar
            ProgressBar pb = new ProgressBar(i.getProgressPercentage() / 100.0);
            pb.setPrefWidth(500);
            pb.getStyleClass().add("progress-bar");
            Label progressLabel = new Label(i.getProgressPercentage() + "% completed");

            // Log Input
            VBox logBox = new VBox(5);
            Label logTitle = new Label("Latest Entrepreneur Update:");
            logTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #456990;");
            Label logValue = new Label(i.getLatestProgressLog() != null ? i.getLatestProgressLog() : "No updates yet.");
            logValue.getStyleClass().add("progress-log-box");
            logBox.getChildren().addAll(logTitle, logValue);

            // Financial Status
            GridPane finGrid = new GridPane();
            finGrid.setHgap(30);

            Label l1 = new Label("Total Investment:");
            Label v1 = new Label("$" + String.format("%.2f", i.getTotalAmount()));
            v1.setStyle("-fx-font-weight: bold;");
            finGrid.add(l1, 0, 0);
            finGrid.add(v1, 1, 0);

            Label l2 = new Label("Payment Months:");
            HBox paymentBox = new HBox(10);
            paymentBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            Label v2 = new Label(i.getPaymentMonthsCompleted() + " / " + i.getDurationMonths() + " Paid");
            v2.setStyle("-fx-font-weight: bold;");
            paymentBox.getChildren().add(v2);

            if (i.getPaymentMonthsCompleted() < i.getDurationMonths()) {
                Button btnPay = new Button("Mark Month Paid ✅");
                btnPay.getStyleClass().add("ai-btn");
                btnPay.setOnAction(e -> {
                    if (confirm("Log Payment", "Mark the next month as paid for this investment?")) {
                        investmentService.updateProgress(i.getInvestmentId(), i.getProgressPercentage(),
                                i.getLatestProgressLog(), i.getPaymentMonthsCompleted() + 1);
                        refreshPortfolio();
                        buildCollaborationPage();
                    }
                });
                paymentBox.getChildren().add(btnPay);
            }

            finGrid.add(l2, 0, 1);
            finGrid.add(paymentBox, 1, 1);

            Label l3 = new Label("Expected Equity:");
            Label v3 = new Label(i.getEquityRequested() + "% Ownership");
            v3.setStyle("-fx-font-weight: bold; -fx-text-fill: #9B7E46;");
            finGrid.add(l3, 0, 2);
            finGrid.add(v3, 1, 2);

            card.getChildren().addAll(title, pb, progressLabel, logBox, finGrid);
            collaborationContainer.getChildren().add(card);
        }
    }

    private void openInvestDialog(Project p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddInvestment.fxml"));
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof AddInvestmentController) {
                ((AddInvestmentController) controller).setTargetProjectId(p.getProjectId());
            }
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Invest in " + p.getTitle());
            stage.showAndWait();
            hideProjectDetails();
            refreshPortfolio();
            refreshProjects();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean confirm(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}
