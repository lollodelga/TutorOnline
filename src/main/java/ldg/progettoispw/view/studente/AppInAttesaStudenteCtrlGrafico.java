package ldg.progettoispw.view.studente;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ldg.progettoispw.controller.AppInAttesaStudenteCtrlApplicativo;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.view.HomeCtrlGrafico;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppInAttesaStudenteCtrlGrafico extends HomeCtrlGrafico {

    // Costante per lo stile della lista (Glassmorphism)
    private static final String ITEM_STYLE = "-fx-background-color: #3498DB55; -fx-padding: 10; " +
            "-fx-background-radius: 10; -fx-border-color: white; " +
            "-fx-cursor: hand;";

    @FXML private VBox requestsContainer;
    @FXML private AnchorPane requestPane;
    @FXML private Label lblTutor;
    @FXML private Label lblData;
    @FXML private Label lblOra;
    @FXML private Label lblStato;

    private static final Logger LOGGER = Logger.getLogger(AppInAttesaStudenteCtrlGrafico.class.getName());
    private final AppInAttesaStudenteCtrlApplicativo ctrlApplicativo = new AppInAttesaStudenteCtrlApplicativo();
    private AppointmentBean selectedAppointment;

    @Override
    @FXML
    public void initialize() {
        loadPendingRequests();
    }

    private void loadPendingRequests() {
        List<AppointmentBean> pendingAppointments = new ArrayList<>();
        try {
            pendingAppointments = ctrlApplicativo.getAppuntamentiInAttesa();
        } catch (DBException e) {
            LOGGER.log(Level.SEVERE, "Errore DB recupero attesa", e);
            showError("Errore Connessione", "Impossibile recuperare le richieste dal database.");
            return;
        }

        requestsContainer.getChildren().clear();

        if (pendingAppointments.isEmpty()) {
            Label emptyLabel = new Label("Nessuna richiesta in attesa.");
            emptyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            requestsContainer.getChildren().add(emptyLabel);
            return;
        }

        for (AppointmentBean app : pendingAppointments) {
            // Creo l'etichetta per la lista con stile uniforme
            Label item = new Label(
                    app.getData() + " - " + app.getOra() + "\nTutor: " + app.getTutorEmail()
            );

            item.setStyle(ITEM_STYLE);

            // Stile testo bianco
            item.setStyle(ITEM_STYLE + " -fx-text-fill: white; -fx-font-size: 14px;");

            item.setMaxWidth(Double.MAX_VALUE);
            item.setWrapText(true);

            item.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> showAppointmentDetails(app));
            requestsContainer.getChildren().add(item);
        }
    }

    private void showAppointmentDetails(AppointmentBean app) {
        selectedAppointment = app;
        lblTutor.setText("Tutor: " + app.getTutorEmail());
        lblData.setText("Data: " + app.getData());
        lblOra.setText("Ora: " + app.getOra());
        lblStato.setText("Stato: " + app.getStato());

        requestPane.setVisible(true);
        centerPopup();
    }

    @FXML
    private void onChiudiClick(ActionEvent event) {
        requestPane.setVisible(false);
    }

    @FXML
    private void backAction(ActionEvent event) {
        switchScene("/ldg/progettoispw/HomePageStudent.fxml", event);
    }

    private void centerPopup() {
        if (requestPane.getScene() != null) {
            double sceneWidth = requestPane.getScene().getWidth();
            double paneWidth = requestPane.getPrefWidth();
            requestPane.setLayoutX((sceneWidth - paneWidth) / 2);
        }
    }
}