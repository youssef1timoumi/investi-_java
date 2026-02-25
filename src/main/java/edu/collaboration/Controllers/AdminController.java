package edu.collaboration.Controllers;

import edu.collaboration.entities.Investment;
import edu.collaboration.entities.Project;
import edu.collaboration.services.EmailService;
import edu.collaboration.services.InvestmentService;
import edu.collaboration.services.ProjectService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

public class AdminController implements Initializable {

    // ─── Pages ─────────────────────────────────────────────────────────────────
    @FXML
    private VBox queuePage;
    @FXML
    private VBox projectsPage;
    @FXML
    private VBox investmentsPage;
    @FXML
    private VBox statsPage;

    // ─── Nav Buttons ───────────────────────────────────────────────────────────
    @FXML
    private Button navQueue;
    @FXML
    private Button navProjects;
    @FXML
    private Button navInvestments;
    @FXML
    private Button navStats;

    // ─── Tab 1: Validation Queue ───────────────────────────────────────────
    @FXML
    private TableView<Project> projectTableQueue;
    @FXML
    private TableColumn<Project, Integer> colProjectIdQ;
    @FXML
    private TableColumn<Project, String> colProjectTitleQ;
    @FXML
    private TableColumn<Project, String> colProjectDescQ;
    @FXML
    private TableColumn<Project, Integer> colProjectRepoQ;
    @FXML
    private TableColumn<Project, String> colProjectStatusQ;

    @FXML
    private TableView<Investment> investmentTableQueue;
    @FXML
    private TableColumn<Investment, Integer> colInvIdQ;
    @FXML
    private TableColumn<Investment, Integer> colInvProjectQ;
    @FXML
    private TableColumn<Investment, Integer> colInvInvestorQ;
    @FXML
    private TableColumn<Investment, Double> colInvAmountQ;
    @FXML
    private TableColumn<Investment, Double> colInvEquityQ;
    @FXML
    private TableColumn<Investment, String> colInvStatusQ;

    // ─── Tab 2: All Projects + Search ─────────────────────────────────────
    @FXML
    private TableView<Project> projectTableAll;
    @FXML
    private TableColumn<Project, Integer> colProjectIdAll;
    @FXML
    private TableColumn<Project, String> colProjectTitleAll;
    @FXML
    private TableColumn<Project, String> colProjectDescAll;
    @FXML
    private TableColumn<Project, Integer> colProjectRepoAll;
    @FXML
    private TableColumn<Project, String> colProjectStatusAll;
    @FXML
    private TextField searchProjectsField;

    // ─── Tab 3: All Investments + Search ──────────────────────────────────
    @FXML
    private TableView<Investment> investmentTableAll;
    @FXML
    private TableColumn<Investment, Integer> colInvIdAll;
    @FXML
    private TableColumn<Investment, Integer> colInvProjectAll;
    @FXML
    private TableColumn<Investment, Integer> colInvInvestorAll;
    @FXML
    private TableColumn<Investment, Double> colInvAmountAll;
    @FXML
    private TableColumn<Investment, Double> colInvEquityAll;
    @FXML
    private TableColumn<Investment, String> colInvStatusAll;
    @FXML
    private TextField searchInvestmentsField;

    // ─── Tab 4: Statistics ────────────────────────────────────────────────
    @FXML
    private GridPane statsGrid;

    @FXML
    private PieChart projectStatusChart;

    @FXML
    private BarChart<String, Number> fundingBarChart;

    private final ProjectService projectService = new ProjectService();
    private final InvestmentService investmentService = new InvestmentService();

    private List<Project> allProjectsList;
    private List<Investment> allInvestmentsList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTables();
        refreshData();

        if (searchProjectsField != null) {
            searchProjectsField.textProperty().addListener((obs, o, n) -> applyProjectSearch(n));
        }
        if (searchInvestmentsField != null) {
            searchInvestmentsField.textProperty().addListener((obs, o, n) -> applyInvestmentSearch(n));
        }
    }

    // ─── Sidebar Navigation ─────────────────────────────────────────────────────

    @FXML
    void showQueuePage() {
        togglePage(queuePage, navQueue);
    }

    @FXML
    void showProjectsPage() {
        togglePage(projectsPage, navProjects);
    }

    @FXML
    void showInvestmentsPage() {
        togglePage(investmentsPage, navInvestments);
    }

    @FXML
    void showStatsPage() {
        togglePage(statsPage, navStats);
    }

    private void togglePage(VBox pageToShow, Button activeNavBtn) {
        queuePage.setVisible(false);
        queuePage.setManaged(false);
        projectsPage.setVisible(false);
        projectsPage.setManaged(false);
        investmentsPage.setVisible(false);
        investmentsPage.setManaged(false);
        statsPage.setVisible(false);
        statsPage.setManaged(false);

        navQueue.getStyleClass().remove("active");
        navProjects.getStyleClass().remove("active");
        navInvestments.getStyleClass().remove("active");
        navStats.getStyleClass().remove("active");

        pageToShow.setVisible(true);
        pageToShow.setManaged(true);
        activeNavBtn.getStyleClass().add("active");
    }

    @FXML
    void logout() {
        Platform.exit();
    }

    // ─── Search Logic ──────────────────────────────────────────────────────────

    private void applyProjectSearch(String keyword) {
        if (allProjectsList == null)
            return;
        if (keyword == null || keyword.isBlank()) {
            projectTableAll.setItems(FXCollections.observableArrayList(allProjectsList));
            return;
        }
        String kw = keyword.toLowerCase();
        List<Project> filtered = allProjectsList.stream()
                .filter(p -> p.getTitle().toLowerCase().contains(kw) || p.getDescription().toLowerCase().contains(kw)
                        || p.getStatus().toLowerCase().contains(kw) || String.valueOf(p.getProjectId()).contains(kw))
                .collect(Collectors.toList());
        projectTableAll.setItems(FXCollections.observableArrayList(filtered));
    }

    private void applyInvestmentSearch(String keyword) {
        if (allInvestmentsList == null)
            return;
        if (keyword == null || keyword.isBlank()) {
            investmentTableAll.setItems(FXCollections.observableArrayList(allInvestmentsList));
            return;
        }
        String kw = keyword.toLowerCase();
        List<Investment> filtered = allInvestmentsList.stream()
                .filter(i -> String.valueOf(i.getProjectId()).contains(kw)
                        || String.valueOf(i.getInvestorId()).contains(kw)
                        || i.getStatus().toLowerCase().contains(kw) || String.valueOf(i.getTotalAmount()).contains(kw))
                .collect(Collectors.toList());
        investmentTableAll.setItems(FXCollections.observableArrayList(filtered));
    }

    // ─── Table Setup ───────────────────────────────────────────────────────────

    private void setupTables() {
        setupProjectColumns(colProjectIdQ, colProjectTitleQ, colProjectDescQ, colProjectRepoQ, colProjectStatusQ);
        setupProjectColumns(colProjectIdAll, colProjectTitleAll, colProjectDescAll, colProjectRepoAll,
                colProjectStatusAll);
        setupInvestmentColumns(colInvIdQ, colInvProjectQ, colInvInvestorQ, colInvAmountQ, colInvEquityQ, colInvStatusQ);
        setupInvestmentColumns(colInvIdAll, colInvProjectAll, colInvInvestorAll, colInvAmountAll, colInvEquityAll,
                colInvStatusAll);

        applyTableStyles(projectTableQueue);
        applyTableStyles(projectTableAll);
        applyTableStyles(investmentTableQueue);
        applyTableStyles(investmentTableAll);
    }

    private <T> void applyTableStyles(TableView<T> tv) {
        tv.setRowFactory(table -> new TableRow<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                    return;
                }
                String status = "";
                if (item instanceof Project)
                    status = ((Project) item).getStatus();
                else if (item instanceof Investment)
                    status = ((Investment) item).getStatus();

                if ("UNDER_REVIEW".equals(status))
                    setStyle("-fx-background-color: #fffbee;");
                else if ("OPEN".equals(status) || "ACCEPTED".equals(status))
                    setStyle("-fx-background-color: #eff6ff;");
                else if ("REFUSED".equals(status))
                    setStyle("-fx-background-color: #fef2f2; -fx-opacity: 0.7;");
                else
                    setStyle("");
            }
        });
    }

    private void setupProjectColumns(TableColumn<Project, Integer> id, TableColumn<Project, String> title,
            TableColumn<Project, String> desc, TableColumn<Project, Integer> repo,
            TableColumn<Project, String> status) {
        id.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        desc.setCellValueFactory(new PropertyValueFactory<>("description"));
        repo.setCellValueFactory(new PropertyValueFactory<>("entrepreneurId"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupInvestmentColumns(TableColumn<Investment, Integer> id, TableColumn<Investment, Integer> pid,
            TableColumn<Investment, Integer> iid, TableColumn<Investment, Double> amt,
            TableColumn<Investment, Double> eq, TableColumn<Investment, String> stat) {
        id.setCellValueFactory(new PropertyValueFactory<>("investmentId"));
        pid.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        iid.setCellValueFactory(new PropertyValueFactory<>("investorId"));
        amt.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        eq.setCellValueFactory(new PropertyValueFactory<>("equityRequested"));
        stat.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    // ─── Actions & Stats ───────────────────────────────────────────────────────

    @FXML
    void refreshDataAction() {
        refreshData();
    }

    private void refreshData() {
        allProjectsList = projectService.getData();
        projectTableAll.setItems(FXCollections.observableArrayList(allProjectsList));
        projectTableQueue.setItems(FXCollections.observableArrayList(
                allProjectsList.stream().filter(p -> "UNDER_REVIEW".equals(p.getStatus()))
                        .collect(Collectors.toList())));

        allInvestmentsList = investmentService.getData();
        investmentTableAll.setItems(FXCollections.observableArrayList(allInvestmentsList));
        investmentTableQueue.setItems(FXCollections.observableArrayList(
                allInvestmentsList.stream().filter(i -> "UNDER_REVIEW".equals(i.getStatus()))
                        .collect(Collectors.toList())));

        loadStats();
    }

    private void loadStats() {
        if (statsGrid == null)
            return;
        statsGrid.getChildren().clear();

        int total = projectService.getTotalProjectCount();
        int funded = projectService.getFundedProjectCount();
        int open = projectService.getOpenProjectCount();
        int invCount = investmentService.getTotalInvestmentCount();
        double invVolume = investmentService.getTotalInvestedVolume();
        double fundedPct = total > 0 ? (funded * 100.0 / total) : 0;

        addStatCard(statsGrid, 0, 0, "Total Projects", String.valueOf(total));
        addStatCard(statsGrid, 0, 1, "Open Projects", String.valueOf(open));
        addStatCard(statsGrid, 1, 0, "Funded Projects", String.valueOf(funded));
        addStatCard(statsGrid, 1, 1, "Success Rate", String.format("%.1f%%", fundedPct));
        addStatCard(statsGrid, 2, 0, "Total Investments", String.valueOf(invCount));
        addStatCard(statsGrid, 2, 1, "Total Injected Capital", String.format("$%.0f", invVolume));

        if (projectStatusChart != null) {
            projectStatusChart.getData().clear();
            long openCount = allProjectsList.stream().filter(p -> "OPEN".equals(p.getStatus())).count();
            long underReviewCount = allProjectsList.stream().filter(p -> "UNDER_REVIEW".equals(p.getStatus())).count();
            long closedCount = allProjectsList.stream().filter(p -> "CLOSED".equals(p.getStatus())
                    || "FUNDED".equals(p.getStatus()) || "VALIDATED".equals(p.getStatus())).count();

            projectStatusChart.getData().addAll(
                    new PieChart.Data("Open", openCount),
                    new PieChart.Data("Under Review", underReviewCount),
                    new PieChart.Data("Closed/Funded", closedCount));
        }

        if (fundingBarChart != null) {
            fundingBarChart.getData().clear();
            double totalRequested = allProjectsList.stream().mapToDouble(Project::getAmountRequested).sum();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Capital Flow");
            series.getData().add(new XYChart.Data<>("Target Capital", totalRequested));
            series.getData().add(new XYChart.Data<>("Capital Injected", invVolume));

            fundingBarChart.getData().add(series);
        }
    }

    private void addStatCard(GridPane grid, int row, int col, String label, String value) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(250, 150);
        card.getStyleClass().add("project-card");

        Label val = new Label(value);
        val.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: -app-primary;");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 14px; -fx-text-fill: -app-muted-foreground;");

        card.getChildren().addAll(val, lbl);
        grid.add(card, col, row);
    }

    // ─── Queue Approvals & Deletions ───────────────────────────────────────────

    @FXML
    void validateProject() {
        Project selected = projectTableQueue.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;
        if (confirm("Validate Project", "Approve '" + selected.getTitle() + "'?")) {
            selected.setStatus("OPEN");
            if (projectService.update(selected.getProjectId(), selected)) {
                EmailService.sendProjectValidated("entrepreneur@example.com", selected.getTitle());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Project validated and is now OPEN! ✅");
                refreshData();
            }
        }
    }

    @FXML
    void deleteProjectQueue() {
        deleteProject(projectTableQueue);
    }

    @FXML
    void deleteProjectAll() {
        deleteProject(projectTableAll);
    }

    private void deleteProject(TableView<Project> table) {
        Project selected = table.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;
        if (confirm("Delete Project", "Permanently delete '" + selected.getTitle() + "'?")) {
            projectService.deleteEntity(selected);
            refreshData();
        }
    }

    @FXML
    void validateInvestment() {
        Investment selected = investmentTableQueue.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;
        if (confirm("Validate Investment", "Approve this investment?")) {
            selected.setStatus("PENDING");
            if (investmentService.update(selected.getInvestmentId(), selected)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Investment validated. ✅");
                refreshData();
            }
        }
    }

    @FXML
    void deleteInvestmentQueue() {
        deleteInvestment(investmentTableQueue);
    }

    @FXML
    void deleteInvestmentAll() {
        deleteInvestment(investmentTableAll);
    }

    private void deleteInvestment(TableView<Investment> table) {
        Investment selected = table.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;
        if (confirm("Delete Investment", "Permanently delete this investment?")) {
            investmentService.deleteEntity(selected);
            refreshData();
        }
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
