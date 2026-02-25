package edu.collaboration.Controllers;

import edu.collaboration.entities.Investment;
import edu.collaboration.entities.Project;
import edu.collaboration.services.AiService;
import edu.collaboration.services.EmailService;
import edu.collaboration.services.InvestmentService;
import edu.collaboration.services.ProjectService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EntrepreneurController implements Initializable {

    // ─── Main Pages ────────────────────────────────────────────────────────────
    @FXML
    private VBox projectsPage;
    @FXML
    private VBox offersPage;
    @FXML
    private VBox collaborationPage;

    // ─── Sidebar Navigation Buttons ─────────────────────────────────────────────
    @FXML
    private Button navProjects;
    @FXML
    private Button navCollaboration;

    // ─── Projects Page Components ───────────────────────────────────────────────
    @FXML
    private FlowPane projectsContainer;

    // ─── Offers Page Components ─────────────────────────────────────────────────
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
    @FXML
    private TableColumn<Investment, String> colOfferStatus;

    @FXML
    private Label offersPageTitle;
    @FXML
    private ComboBox<String> statusFilterBox;
    @FXML
    private ComboBox<String> sortOffersBox;

    // AI Mentor components
    @FXML
    private HBox aiMentorBox;
    @FXML
    private Button btnMentor;
    @FXML
    private Label aiMentorAdvice;
    @FXML
    private Button btnAccept;
    @FXML
    private Button btnDecline;

    // ─── Collaboration Page Components ───────────────────────────────────────────
    @FXML
    private VBox collaborationContainer;

    private final ProjectService projectService = new ProjectService();
    private final InvestmentService investmentService = new InvestmentService();
    // MOCK LOGIN
    private final int currentEntrepreneurId = 1;

    private List<Investment> allRelevantOffers = new ArrayList<>();
    private List<Project> myProjects = new ArrayList<>();
    private Project focusedProjectForOffers = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupOffersTable();

        statusFilterBox.setItems(FXCollections.observableArrayList("All", "PENDING", "ACCEPTED", "REFUSED"));
        statusFilterBox.setValue("All");

        sortOffersBox.setItems(FXCollections.observableArrayList(
                "Newest First", "Amount: High→Low", "Amount: Low→High", "Equity: High→Low", "Equity: Low→High"));
        sortOffersBox.setValue("Newest First");

        statusFilterBox.setOnAction(e -> applyOfferFilters());
        sortOffersBox.setOnAction(e -> applyOfferFilters());

        offersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                aiMentorBox.setVisible(true);
                aiMentorAdvice.setText("");
                btnMentor.setDisable(false);
                btnMentor.setText("💡 Ask AI Mentor");

                boolean isPending = "PENDING".equalsIgnoreCase(newV.getStatus());
                btnAccept.setDisable(!isPending);
                btnDecline.setDisable(!isPending);
            } else {
                aiMentorBox.setVisible(false);
            }
        });

        refreshData();
    }

    // ─── Navigation Logic ───────────────────────────────────────────────────────

    @FXML
    void showProjectsPage() {
        focusedProjectForOffers = null;
        togglePage(projectsPage, navProjects);
    }

    @FXML
    void showOffersPage() {
        if (focusedProjectForOffers == null) {
            showAlert(Alert.AlertType.WARNING, "No Project Selected",
                    "Please select a project from 'My Projects' to view its offers.");
            showProjectsPage();
            return;
        }
        offersPageTitle.setText("Offers for: " + focusedProjectForOffers.getTitle());
        togglePage(offersPage, null);
    }

    @FXML
    void showCollaborationPage() {
        togglePage(collaborationPage, navCollaboration);
        buildCollaborationPage();
    }

    private void togglePage(VBox pageToShow, Button activeNavBtn) {
        projectsPage.setVisible(false);
        projectsPage.setManaged(false);
        offersPage.setVisible(false);
        offersPage.setManaged(false);
        collaborationPage.setVisible(false);
        collaborationPage.setManaged(false);

        navProjects.getStyleClass().remove("active");
        navCollaboration.getStyleClass().remove("active");

        pageToShow.setVisible(true);
        pageToShow.setManaged(true);
        if (activeNavBtn != null) {
            activeNavBtn.getStyleClass().add("active");
        }
    }

    @FXML
    void logout() {
        Platform.exit();
    }

    // ─── Data & Projects Rendering ──────────────────────────────────────────────

    @FXML
    void refreshData() {
        myProjects = projectService.getProjectsByEntrepreneur(currentEntrepreneurId);

        allRelevantOffers.clear();
        for (Investment inv : concat(investmentService.getInvestmentsByStatus("PENDING"),
                investmentService.getInvestmentsByStatus("ACCEPTED"),
                investmentService.getInvestmentsByStatus("REFUSED"))) {
            if (myProjects.stream().anyMatch(p -> p.getProjectId() == inv.getProjectId())) {
                allRelevantOffers.add(inv);
            }
        }

        renderProjectCards();
        applyOfferFilters();
        if (collaborationPage.isVisible())
            buildCollaborationPage();
    }

    private void renderProjectCards() {
        projectsContainer.getChildren().clear();
        if (myProjects.isEmpty()) {
            projectsContainer.getChildren().add(new Label("You haven't created any projects yet."));
            return;
        }

        for (Project p : myProjects) {
            VBox card = new VBox(15);
            card.getStyleClass().add("project-card");

            HBox header = new HBox(10);
            Label catBadge = new Label(p.getCategory() != null ? p.getCategory() : "Other");
            catBadge.getStyleClass().add("category-badge");

            Label statusBadge = new Label(p.getStatus());
            statusBadge.getStyleClass().add("category-badge");
            statusBadge
                    .setStyle("-fx-background-color: " + ("OPEN".equals(p.getStatus()) ? "#456990" : "#A62639") + ";");

            Label title = new Label(p.getTitle());
            title.getStyleClass().add("card-title");

            header.getChildren().addAll(catBadge, statusBadge, title);

            Label desc = new Label(p.getDescription());
            desc.getStyleClass().add("card-desc");
            desc.setWrapText(true);
            desc.setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);

            Label goal = new Label("Requested: $" + String.format("%.2f", p.getAmountRequested()));
            goal.getStyleClass().add("card-price");

            HBox actions = new HBox(10);
            Button editBtn = new Button("Edit");
            editBtn.getStyleClass().add("add-btn");
            editBtn.setOnAction(e -> {
                if ("FUNDED".equalsIgnoreCase(p.getStatus()) || "CLOSED".equalsIgnoreCase(p.getStatus())) {
                    showAlert(Alert.AlertType.ERROR, "Forbidden", "Cannot edit a FUNDED or CLOSED project.");
                    return;
                }
                editProject(p);
            });

            Button viewOffersBtn = new Button("View Offers");
            viewOffersBtn.getStyleClass().add("add-btn");
            viewOffersBtn.setStyle("-fx-background-color: #456990;");
            viewOffersBtn.setOnAction(e -> {
                focusedProjectForOffers = p;
                showOffersPage();
            });

            Button deleteBtn = new Button("Delete");
            deleteBtn.getStyleClass().add("add-btn");
            deleteBtn.setStyle("-fx-background-color: #A62639;");
            deleteBtn.setOnAction(e -> deleteProject(p));

            actions.getChildren().addAll(viewOffersBtn, editBtn, deleteBtn);
            card.getChildren().addAll(header, desc, goal, actions);
            projectsContainer.getChildren().add(card);
        }
    }

    // ─── AI Mentor Logic (Idea 3) ───────────────────────────────────────────────

    @FXML
    void askAiMentor() {
        Investment selectedOffer = offersTable.getSelectionModel().getSelectedItem();
        if (selectedOffer == null)
            return;

        Optional<Project> projOpt = myProjects.stream().filter(p -> p.getProjectId() == selectedOffer.getProjectId())
                .findFirst();
        if (projOpt.isEmpty())
            return;
        Project p = projOpt.get();

        btnMentor.setText("AI is analyzing... ⏳");
        btnMentor.setDisable(true);

        new Thread(() -> {
            String advice = AiService.evaluateLogicForEntrepreneur(selectedOffer.getTotalAmount(),
                    selectedOffer.getEquityRequested(), p.getDescription());
            Platform.runLater(() -> {
                btnMentor.setText("💡 Mentor Responded");
                aiMentorAdvice.setText("AI Mentor: " + advice);
            });
        }).start();
    }

    // ─── Collaboration Tracker Logic (Idea 5) ───────────────────────────────────

    private void buildCollaborationPage() {
        if (collaborationContainer == null)
            return;
        collaborationContainer.getChildren().clear();

        List<Investment> activeCollabs = allRelevantOffers.stream()
                .filter(i -> "ACCEPTED".equalsIgnoreCase(i.getStatus()))
                .collect(Collectors.toList());

        if (activeCollabs.isEmpty()) {
            collaborationContainer.getChildren()
                    .add(new Label("No active collaborations. Accept an offer to start tracking progress."));
            return;
        }

        for (Investment i : activeCollabs) {
            VBox card = new VBox(15);
            card.getStyleClass().add("progress-card");

            Label title = new Label(
                    "Track Project ID #" + i.getProjectId() + " (Investment #" + i.getInvestmentId() + ")");
            title.getStyleClass().add("progress-title");

            // Progress Bar visualizer
            ProgressBar pb = new ProgressBar(i.getProgressPercentage() / 100.0);
            pb.setPrefWidth(500); // Changed from Double.MAX_VALUE to prevent squishing
            pb.getStyleClass().add("progress-bar");

            // Progress Controls
            VBox controls = new VBox(20);

            HBox progBox = new HBox(15);
            progBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            Label lblProg = new Label("Set Progress %:");
            Slider slider = new Slider(0, 100, i.getProgressPercentage());
            slider.setShowTickLabels(true);
            slider.setMajorTickUnit(25);
            progBox.getChildren().addAll(lblProg, slider);

            HBox logBox = new HBox(15);
            logBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            Label lblLog = new Label("Update Log:");
            TextField textLog = new TextField(i.getLatestProgressLog() != null ? i.getLatestProgressLog() : "");
            textLog.setPrefWidth(400);
            logBox.getChildren().addAll(lblLog, textLog);

            Button saveBtn = new Button("Save Progress");
            saveBtn.getStyleClass().add("add-btn");
            saveBtn.setOnAction(e -> {
                int newVal = (int) slider.getValue();
                boolean s = investmentService.updateProgress(i.getInvestmentId(), newVal, textLog.getText(),
                        i.getPaymentMonthsCompleted());
                if (s) {
                    pb.setProgress(newVal / 100.0);
                    refreshData(); // Updates the base lists too
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Progress updated and sent to Investor!");
                }
            });

            controls.getChildren().addAll(progBox, logBox, saveBtn);

            // Financial Status shown to Entrepreneur
            GridPane finGrid = new GridPane();
            finGrid.setHgap(30);
            finGrid.add(new Label("Total Funding:"), 0, 0);
            finGrid.add(new Label("$" + String.format("%.2f", i.getTotalAmount())), 1, 0);

            finGrid.add(new Label("Investor Paid Months:"), 0, 1);
            finGrid.add(new Label(i.getPaymentMonthsCompleted() + " / " + i.getDurationMonths()), 1, 1);

            card.getChildren().addAll(title, pb, controls, new Separator(), finGrid);
            collaborationContainer.getChildren().add(card);
        }
    }

    // ─── Offers Table Logic ─────────────────────────────────────────────────────

    private void setupOffersTable() {
        colOfferId.setCellValueFactory(new PropertyValueFactory<>("investmentId"));
        colOfferProject.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        colOfferInvestor.setCellValueFactory(new PropertyValueFactory<>("investorId"));
        colOfferAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colOfferEquity.setCellValueFactory(new PropertyValueFactory<>("equityRequested"));
        colOfferDuration.setCellValueFactory(new PropertyValueFactory<>("durationMonths"));
        colOfferStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        offersTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Investment item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                    return;
                }
                switch (item.getStatus()) {
                    case "ACCEPTED" -> setStyle("-fx-background-color: #e8f5e9;");
                    case "REFUSED" -> setStyle("-fx-background-color: #fff5f5; -fx-opacity: 0.7;");
                    default -> setStyle("");
                }
            }
        });
    }

    private void applyOfferFilters() {
        if (focusedProjectForOffers == null)
            return;

        String statusFilt = statusFilterBox.getValue();
        String sort = sortOffersBox.getValue();

        List<Investment> filtered = allRelevantOffers.stream()
                .filter(i -> i.getProjectId() == focusedProjectForOffers.getProjectId())
                .filter(i -> statusFilt == null || statusFilt.equals("All") || statusFilt.equals(i.getStatus()))
                .collect(Collectors.toList());

        if (sort != null) {
            switch (sort) {
                case "Amount: High→Low" ->
                    filtered.sort((a, b) -> Double.compare(b.getTotalAmount(), a.getTotalAmount()));
                case "Amount: Low→High" ->
                    filtered.sort((a, b) -> Double.compare(a.getTotalAmount(), b.getTotalAmount()));
                case "Equity: High→Low" ->
                    filtered.sort((a, b) -> Double.compare(b.getEquityRequested(), a.getEquityRequested()));
                case "Equity: Low→High" ->
                    filtered.sort((a, b) -> Double.compare(a.getEquityRequested(), b.getEquityRequested()));
            }
        }
        offersTable.setItems(FXCollections.observableArrayList(filtered));
        offersTable.refresh();
    }

    // ─── Actions ────────────────────────────────────────────────────────────────

    @FXML
    void createNewProject() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddProject.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Create New Project");
            stage.showAndWait();
            refreshData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    void acceptOffer() {
        Investment selected = offersTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;
        boolean alreadyFunded = allRelevantOffers.stream()
                .anyMatch(inv -> inv.getProjectId() == selected.getProjectId() && "ACCEPTED".equals(inv.getStatus()));
        if (alreadyFunded) {
            showAlert(Alert.AlertType.ERROR, "Project Funded", "This project already has an accepted investment.");
            return;
        }
        if (confirm("Accept Offer", "This will FUND your project and REJECT all other pending offers.")) {
            if (investmentService.acceptInvestment(selected.getInvestmentId(), selected.getProjectId())) {
                String pTitle = myProjects.stream().filter(p -> p.getProjectId() == selected.getProjectId())
                        .map(Project::getTitle).findFirst().orElse("");
                EmailService.sendInvestmentAccepted("entrepreneur@example.com", "investor@example.com", pTitle,
                        selected.getTotalAmount());
                showAlert(Alert.AlertType.INFORMATION, "Congratulations!",
                        "Investment accepted. Project is now FUNDED! 🎉");
                refreshData();
            }
        }
    }

    @FXML
    void declineOffer() {
        Investment selected = offersTable.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;
        if (confirm("Decline Offer", "Are you sure you want to decline this offer?")) {
            selected.setStatus("REFUSED");
            if (investmentService.update(selected.getInvestmentId(), selected)) {
                String pTitle = myProjects.stream().filter(p -> p.getProjectId() == selected.getProjectId())
                        .map(Project::getTitle).findFirst().orElse("");
                EmailService.sendInvestmentRefused("investor@example.com", pTitle, selected.getTotalAmount());
                refreshData();
            }
        }
    }

    private void editProject(Project p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateProject.fxml"));
            Parent root = loader.load();
            edu.collaboration.Controllers.Project.UpdateProjectController controller = loader.getController();
            controller.initData(p);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Update Project");
            stage.showAndWait();
            refreshData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void deleteProject(Project p) {
        if (confirm("Delete Project", "Delete '" + p.getTitle() + "'?")) {
            projectService.deleteEntity(p);
            refreshData();
        }
    }

    @SafeVarargs
    private <T> List<T> concat(List<T>... lists) {
        List<T> result = new ArrayList<>();
        for (List<T> l : lists)
            result.addAll(l);
        return result;
    }

    private boolean confirm(String title, String content) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(title);
        a.setContentText(content);
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setContentText(content);
        a.showAndWait();
    }
}
