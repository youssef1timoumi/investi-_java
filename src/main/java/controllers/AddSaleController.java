package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Product;
import models.Sale;
import services.SaleService;
import services.ProductService;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import utils.TileServer;

public class AddSaleController implements Initializable {

    @FXML
    private TextField productNameField, referenceField, amountField, currencyField,
            shippingField, billingField;
    @FXML
    private ComboBox<String> paymentMethodCombo;
    @FXML
    private TextArea notesField;
    @FXML
    private Button saveBtn;
    @FXML
    private Label formTitle;

    private final SaleService saleService = new SaleService();
    private Consumer<Void> onSaleCreated;
    private Runnable onCancel;
    private Product product;
    private Sale existingSale;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        paymentMethodCombo.getItems().addAll("Credit Card", "PayPal", "Bank Transfer", "Cash");
        paymentMethodCombo.setValue("Credit Card");
        referenceField.setText("SALE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }

    public void setProduct(Product p) {
        this.product = p;
        if (p != null) {
            productNameField.setText(p.getName());
            amountField.setText(String.valueOf(p.getPrice()));
            currencyField.setText(p.getCurrency() != null ? p.getCurrency() : "USD");
        }
    }

    public void setSaleData(Sale s) {
        this.existingSale = s;
        if (s != null) {
            formTitle.setText("Update Sale");
            saveBtn.setText("Save Changes");
            referenceField.setText(s.getReference());
            referenceField.setEditable(false);
            amountField.setText(String.valueOf(s.getTotalAmount()));
            currencyField.setText(s.getCurrency());
            paymentMethodCombo.setValue(s.getPaymentMethod());
            shippingField.setText(s.getShippingAddress());
            billingField.setText(s.getBillingAddress());
            notesField.setText(s.getNotes());
        }
    }

    public void setOnSaleCreated(Consumer<Void> cb) {
        this.onSaleCreated = cb;
    }

    public void setOnCancel(Runnable cb) {
        this.onCancel = cb;
    }

    @FXML
    private void handleSave() {
        if (!validateForm())
            return;
        try {
            Sale s = (existingSale != null) ? existingSale : new Sale();
            s.setReference(referenceField.getText());
            s.setCustomerId(1L);
            s.setProductId(product != null ? product.getId()
                    : (existingSale != null ? existingSale.getProductId() : 0));
            s.setTotalAmount(Double.parseDouble(amountField.getText()));
            s.setCurrency(currencyField.getText());
            s.setPaymentMethod(paymentMethodCombo.getValue());
            s.setShippingAddress(shippingField.getText());
            s.setBillingAddress(billingField.getText());
            s.setNotes(notesField.getText());
            if (existingSale != null) {
                saleService.update(s);
            } else {
                s.setStatus("unpaid");
                s.setPaymentStatus("unpaid");
                s.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
                saleService.create(s);

                // Deduct stock if there is a known product
                if (product != null && product.getStock() > 0) {
                    product.setStock(product.getStock() - 1);
                    new ProductService().update(product);
                }
            }
            if (onSaleCreated != null)
                onSaleCreated.accept(null);
        } catch (SQLException | NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de finaliser la vente : " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        if (onCancel != null)
            onCancel.run();
    }

    @FXML
    private void handleOpenShippingMap() {
        openMapWindow(shippingField);
    }

    @FXML
    private void handleOpenBillingMap() {
        openMapWindow(billingField);
    }

    // ── Map window ────────────────────────────────────────────────────────

    private void openMapWindow(TextField targetField) {
        File tilesDir = resolveTilesDir();
        showMapStage(tilesDir, targetField);
    }

    private void showMapStage(File tilesDir, TextField targetField) {
        TileServer tileServer = new TileServer(tilesDir);
        int tilePort;
        try {
            tilePort = tileServer.start();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Serveur de tuiles : " + e.getMessage());
            return;
        }

        Stage stage = new Stage();
        stage.setTitle("Selectionner une adresse");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setOnHidden(ev -> tileServer.stop());

        WebView wv = new WebView();
        WebEngine engine = wv.getEngine();
        wv.setMinSize(850, 650);
        wv.setPrefSize(850, 650);
        wv.setMaxSize(850, 650);

        // Extract all map resources to temp dir so file:// relative paths work
        URL mapUrl;
        try {
            mapUrl = extractMapResources(tilePort);
        } catch (Exception e) {
            tileServer.stop();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Preparation carte : " + e.getMessage());
            return;
        }
        engine.load(mapUrl.toExternalForm());

        engine.titleProperty().addListener((obs, oldT, newT) -> {
            if (newT != null && newT.startsWith("LOCATION:")) {
                String address = newT.substring("LOCATION:".length());
                Platform.runLater(() -> {
                    targetField.setText(address);
                    stage.close();
                });
            }
        });

        engine.getLoadWorker().stateProperty().addListener((obs, oldS, newS) -> {
            if (newS == javafx.concurrent.Worker.State.SUCCEEDED) {
                Platform.runLater(() -> engine.executeScript("if(window.forceInit) window.forceInit();"));
            }
        });

        AnchorPane root = new AnchorPane(wv);
        AnchorPane.setTopAnchor(wv, 0.0);
        AnchorPane.setBottomAnchor(wv, 0.0);
        AnchorPane.setLeftAnchor(wv, 0.0);
        AnchorPane.setRightAnchor(wv, 0.0);

        stage.setScene(new Scene(root, 850, 650));
        stage.show();
    }

    /**
     * Copies map resources from the classpath JAR to a real temp directory
     * and injects the tile server port as a JS global variable.
     * Loading from file:// means relative paths (leaflet.css, leaflet.js) resolve.
     */
    private URL extractMapResources(int tilePort) throws Exception {
        Path tempDir = Files.createTempDirectory("map_picker_");
        tempDir.toFile().deleteOnExit();

        String[] resources = {
                "map_picker.html", "leaflet.css", "leaflet.js",
                "marker-icon.png", "marker-icon-2x.png", "marker-shadow.png"
        };

        for (String res : resources) {
            try (InputStream is = getClass().getResourceAsStream("/" + res)) {
                if (is != null) {
                    Files.copy(is, tempDir.resolve(res), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }

        // Inject tilePort as a JS global so the HTML can read it without query params
        Path htmlPath = tempDir.resolve("map_picker.html");
        String html = new String(Files.readAllBytes(htmlPath));
        html = html.replace("<body>", "<body><script>window._tilePort=" + tilePort + ";</script>");
        Files.write(htmlPath, html.getBytes());

        return htmlPath.toUri().toURL();
    }

    private File resolveTilesDir() {
        File dev = new File("src/main/resources/tiles");
        if (dev.exists())
            return dev.getAbsoluteFile();
        File jar = new File("tiles");
        if (jar.exists())
            return jar.getAbsoluteFile();
        dev.mkdirs();
        return dev.getAbsoluteFile();
    }

    private boolean validateForm() {
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setContentText(msg);
        a.show();
    }
}
