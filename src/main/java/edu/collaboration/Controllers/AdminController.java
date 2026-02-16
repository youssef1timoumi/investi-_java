package edu.collaboration.Controllers;

import edu.collaboration.entities.Investment;
import edu.collaboration.entities.Project;
import edu.collaboration.services.InvestmentService;
import edu.collaboration.services.ProjectService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.control.ButtonType;

public class AdminController implements Initializable {

    // --- Tab 1: Validation Queue ---
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

    // --- Tab 2: All Projects ---
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

    // --- Tab 3: All Investments ---
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

    private final ProjectService projectService = new ProjectService();
    private final InvestmentService investmentService = new InvestmentService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTables();
        refreshData();
    }

    private void setupTables() {
        // Queue Projects
        setupProjectColumns(colProjectIdQ, colProjectTitleQ, colProjectDescQ, colProjectRepoQ, colProjectStatusQ);
        // All Projects
        setupProjectColumns(colProjectIdAll, colProjectTitleAll, colProjectDescAll, colProjectRepoAll,
                colProjectStatusAll);

        // Queue Investments
        setupInvestmentColumns(colInvIdQ, colInvProjectQ, colInvInvestorQ, colInvAmountQ, colInvEquityQ, colInvStatusQ);
        // All Investments
        setupInvestmentColumns(colInvIdAll, colInvProjectAll, colInvInvestorAll, colInvAmountAll, colInvEquityAll,
                colInvStatusAll);
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

    private void refreshData() {
        // Projects
        List<Project> allProjectsList = projectService.getData();
        projectTableAll.setItems(FXCollections.observableArrayList(allProjectsList));

        // Projects Queue (UNDER_REVIEW)
        ObservableList<Project> queueProjects = FXCollections.observableArrayList();
        for (Project p : allProjectsList) {
            if ("UNDER_REVIEW".equals(p.getStatus())) {
                queueProjects.add(p);
            }
        }
        projectTableQueue.setItems(queueProjects);

        // Investments
        List<Investment> allInvestmentsList = investmentService.getData();
        investmentTableAll.setItems(FXCollections.observableArrayList(allInvestmentsList));

        // Investments Queue (UNDER_REVIEW)
        ObservableList<Investment> queueInvestments = FXCollections.observableArrayList();
        for (Investment i : allInvestmentsList) {
            if ("UNDER_REVIEW".equals(i.getStatus())) {
                queueInvestments.add(i);
            }
        }
        investmentTableQueue.setItems(queueInvestments);
    }

    // --- Actions for Queue ---

    @FXML
    void validateProject() {
        Project selected = projectTableQueue.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a project to validate.");
            return;
        }

        if (confirm("Validate Project", "Approve '" + selected.getTitle() + "'?")) {
            selected.setStatus("OPEN");
            if (projectService.update(selected.getProjectId(), selected)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Project validated.");
                refreshData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update status.");
            }
        }
    }

    @FXML
    void deleteProjectQueue() {
        deleteProject(projectTableQueue);
    }

    @FXML
    void validateInvestment() {
        Investment selected = investmentTableQueue.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an investment to validate.");
            return;
        }

        if (confirm("Validate Investment", "Approve this investment?")) {
            selected.setStatus("PENDING");
            if (investmentService.update(selected.getInvestmentId(), selected)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Investment validated.");
                refreshData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update status.");
            }
        }
    }

    @FXML
    void deleteInvestmentQueue() {
        deleteInvestment(investmentTableQueue);
    }

    // --- Actions for Database (All) ---

    @FXML
    void deleteProjectAll() {
        deleteProject(projectTableAll);
    }

    @FXML
    void deleteInvestmentAll() {
        deleteInvestment(investmentTableAll);
    }

    // --- Helper Methods ---

    private void deleteProject(TableView<Project> table) {
        Project selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a project to delete.");
            return;
        }
        if (confirm("Delete Project", "Permanently delete '" + selected.getTitle() + "'?")) {
            projectService.deleteEntity(selected);
            refreshData();
        }
    }

    private void deleteInvestment(TableView<Investment> table) {
        Investment selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an investment to delete.");
            return;
        }
        if (confirm("Delete Investment", "Permanently delete this investment?")) {
            investmentService.deleteEntity(selected);
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
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
