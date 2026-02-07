package ldg.progettoispw.view.tutor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ldg.progettoispw.controller.AppRispostiTutorCtrlApplicativo;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.view.HomeCtrlGrafico;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppRispostiTutorCtrlGrafico extends HomeCtrlGrafico {

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

    @FXML private Button btnAnnulla;
    @FXML private Button btnCompletato;

    private static final Logger LOGGER = Logger.getLogger(AppRispostiTutorCtrlGrafico.class.getName());
    private final AppRispostiTutorCtrlApplicativo ctrlApp = new AppRispostiTutorCtrlApplicativo();
    private AppointmentBean selectedAppointment;

    @Override
    @FXML
    public void initialize() {
        loadAppointments();
    }

    private void loadAppointments() {
        resultsContainer.getChildren().clear();
        try {
            List<AppointmentBean> appointments = ctrlApp.getAppuntamentiTutor();

            if (appointments.isEmpty()) {
                Label empty = new Label("Nessuna richiesta completata o confermata.");
                empty.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                resultsContainer.getChildren().add(empty);
                return;
            }

            for (AppointmentBean bean : appointments) {
                resultsContainer.getChildren().add(createBox(bean));
            }
        } catch (DBException e) {
            LOGGER.log(Level.SEVERE, "Errore nel caricamento appuntamenti tutor", e);
            showError("Errore Caricamento", "Impossibile recuperare gli appuntamenti dal database.");
        }
    }

    private VBox createBox(AppointmentBean bean) {
        VBox box = new VBox();
        box.setSpacing(5);
        // Stile uniforme
        box.setStyle(ITEM_STYLE);

        Label s = new Label("Studente: " + bean.getStudenteEmail());
        Label d = new Label("Data: " + bean.getData() + " ore " + bean.getOra());
        Label st = new Label("Stato: " + bean.getStato());

        String whiteText = "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;";
        s.setStyle(whiteText);
        st.setStyle(whiteText);
        d.setStyle("-fx-text-fill: #ecf0f1; -fx-font-style: italic; -fx-font-size: 13px;");

        box.getChildren().addAll(s, d, st);

        box.setOnMouseClicked(event -> showAppointmentDetails(bean));
        return box;
    }

    private void showAppointmentDetails(AppointmentBean bean) {
        selectedAppointment = bean;

        lblStudente.setText("Studente: " + bean.getStudenteEmail());
        lblData.setText("Data: " + bean.getData());
        lblOra.setText("Ora: " + bean.getOra());
        lblStato.setText("Stato: " + bean.getStato());

        // Mostra i bottoni solo se lo stato è "confermato" (cioè la lezione deve ancora avvenire)
        boolean showActions = "confermato".equalsIgnoreCase(bean.getStato());
        btnAnnulla.setVisible(showActions);
        btnCompletato.setVisible(showActions);

        appointmentPane.setVisible(true);
        centerPopup();
    }

    @FXML
    private void onAnnullaClick(ActionEvent event) {
        if (selectedAppointment == null) return;

        // Logica applicativa
        ctrlApp.updateAppointmentStatus(
                selectedAppointment.getStudenteEmail(),
                selectedAppointment.getData(),
                selectedAppointment.getOra(),
                selectedAppointment.getStato(),
                "cancel");

        showSuccess("Annullato", "La lezione è stata annullata.");
        appointmentPane.setVisible(false);
        loadAppointments(); // Ricarica la lista per vedere le modifiche
    }

    @FXML
    private void onCompletatoClick(ActionEvent event) {
        if (selectedAppointment == null) return;

        // Logica applicativa
        ctrlApp.updateAppointmentStatus(
                selectedAppointment.getStudenteEmail(),
                selectedAppointment.getData(),
                selectedAppointment.getOra(),
                selectedAppointment.getStato(),
                "complete");

        showSuccess("Completato", "Lezione segnata come completata.");
        appointmentPane.setVisible(false);
        loadAppointments(); // Ricarica la lista
    }

    @FXML private void onChiudiClick() { appointmentPane.setVisible(false); }

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