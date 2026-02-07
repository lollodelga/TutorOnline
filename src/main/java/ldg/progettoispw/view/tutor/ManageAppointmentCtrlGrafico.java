package ldg.progettoispw.view.tutor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ldg.progettoispw.controller.ManageAppointmentCtrlApplicativo;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.view.HomeCtrlGrafico;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageAppointmentCtrlGrafico extends HomeCtrlGrafico {

    // Costante per lo stile della lista (Glassmorphism)
    private static final String ITEM_STYLE = "-fx-background-color: #3498DB55; -fx-padding: 10; " +
            "-fx-background-radius: 10; -fx-border-color: white; " +
            "-fx-cursor: hand;";

    @FXML private VBox resultsContainer;
    @FXML private AnchorPane appointmentPane;

    @FXML private Label lblStudente;
    @FXML private Label lblData;
    @FXML private Label lblOra;
    @FXML private Label lblStato;

    private static final Logger LOGGER = Logger.getLogger(ManageAppointmentCtrlGrafico.class.getName());
    private ManageAppointmentCtrlApplicativo ctrlApplicativo;
    private AppointmentBean selectedAppointment;

    @Override
    @FXML
    public void initialize() {
        ctrlApplicativo = new ManageAppointmentCtrlApplicativo();
        appointmentPane.setVisible(false);
        loadPendingAppointments();
    }

    private void loadPendingAppointments() {
        resultsContainer.getChildren().clear();
        try {
            List<AppointmentBean> pendingAppointments = ctrlApplicativo.getAppuntamentiInAttesa();

            if (pendingAppointments.isEmpty()) {
                Label noData = new Label("Nessuna richiesta di appuntamento in attesa.");
                noData.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
                resultsContainer.getChildren().add(noData);
                return;
            }

            for (AppointmentBean bean : pendingAppointments) {
                resultsContainer.getChildren().add(createBox(bean));
            }

        } catch (DBException e) {
            LOGGER.log(Level.SEVERE, "Errore caricamento attesa", e);
            showError("Errore Database", "Impossibile caricare gli appuntamenti.");
        }
    }

    private VBox createBox(AppointmentBean bean) {
        VBox box = new VBox();
        box.setSpacing(5);
        box.setStyle(ITEM_STYLE);

        Label s = new Label("Studente: " + bean.getStudenteEmail());
        Label d = new Label("Data: " + bean.getData() + " ore " + bean.getOra());

        String whiteText = "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;";
        s.setStyle(whiteText);
        d.setStyle("-fx-text-fill: #ecf0f1; -fx-font-style: italic; -fx-font-size: 13px;");

        box.getChildren().addAll(s, d);

        box.setOnMouseClicked(event -> openAppointmentPanel(bean));
        return box;
    }

    private void openAppointmentPanel(AppointmentBean bean) {
        selectedAppointment = bean;

        lblStudente.setText("Studente: " + bean.getStudenteEmail());
        lblData.setText("Data: " + bean.getData());
        lblOra.setText("Ora: " + bean.getOra());
        lblStato.setText("Stato: " + bean.getStato());

        appointmentPane.setVisible(true);
        centerPopup();
    }

    @FXML
    private void onConfermaClick(ActionEvent event) {
        try {
            ctrlApplicativo.confermaAppuntamento(selectedAppointment);
            showSuccess("Confermato", "Appuntamento confermato con successo.");

            // Ricarica la pagina per aggiornare la lista
            appointmentPane.setVisible(false);
            loadPendingAppointments();

        } catch (DBException e) {
            LOGGER.log(Level.SEVERE, "Errore conferma", e);
            showError("Errore Database", "Impossibile confermare l'appuntamento:\n" + e.getMessage());
        } catch (IllegalArgumentException e) {
            showError("Attenzione", e.getMessage());
        }
    }

    @FXML
    private void onRifiutaClick(ActionEvent event) {
        try {
            ctrlApplicativo.rifiutaAppuntamento(selectedAppointment);
            showSuccess("Rifiutato", "L'appuntamento è stato rifiutato.");

            // Ricarica la pagina per aggiornare la lista
            appointmentPane.setVisible(false);
            loadPendingAppointments();

        } catch (DBException e) {
            LOGGER.log(Level.SEVERE, "Errore rifiuto", e);
            showError("Errore Database", "Impossibile rifiutare l'appuntamento:\n" + e.getMessage());
        } catch (IllegalArgumentException e) {
            showError("Attenzione", e.getMessage());
        }
    }

    @FXML
    private void onChiudiClick(ActionEvent event) {
        appointmentPane.setVisible(false);
    }

    @FXML
    private void backAction(ActionEvent event) {
        switchScene("/ldg/progettoispw/HomePageTutor.fxml", event);
    }

    private void centerPopup() {
        if (appointmentPane.getScene() != null) {
            double sceneWidth = appointmentPane.getScene().getWidth();
            double paneWidth = appointmentPane.getPrefWidth();
            appointmentPane.setLayoutX((sceneWidth - paneWidth) / 2);
        }
    }
}