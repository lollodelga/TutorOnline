package ldg.progettoispw.view.tutor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ldg.progettoispw.controller.ManageReviewCtrlApplicativo;
import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.view.HomeCtrlGrafico;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageReviewsCtrlGrafico extends HomeCtrlGrafico {

    // Costante per lo stile della lista (Glassmorphism)
    private static final String ITEM_STYLE = "-fx-background-color: #3498DB55; -fx-padding: 10; " +
            "-fx-background-radius: 10; -fx-border-color: white; ";

    @FXML private BarChart<String, Number> barChart;
    @FXML private VBox reviewsContainer;

    private ManageReviewCtrlApplicativo controllerApplicativo;
    private static final Logger LOGGER = Logger.getLogger(ManageReviewsCtrlGrafico.class.getName());

    @Override
    @FXML
    public void initialize() {
        controllerApplicativo = new ManageReviewCtrlApplicativo();

        try {
            // Recupero dati
            ManageReviewCtrlApplicativo.RecensioniResult result = controllerApplicativo.getRecensioniEValoriSentiment();
            List<RecensioneBean> recensioni = result.getRecensioni();
            List<Integer> sentimentValues = result.getSentimentValues();

            popolaGrafico(sentimentValues);
            popolaListaRecensioni(recensioni);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nel caricamento delle recensioni", e);
            showError("Errore", "Impossibile caricare le statistiche.");
        }
    }

    private void popolaGrafico(List<Integer> sentimentValues) {
        barChart.getData().clear();

        // Raggruppiamo per valore sentiment (1-5)
        int[] counts = new int[5]; // indice 0 -> 1 stella, indice 4 -> 5 stelle
        for (int val : sentimentValues) {
            if (val >= 1 && val <= 5) {
                counts[val - 1]++;
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Valutazioni");

        for (int i = 0; i < counts.length; i++) {
            series.getData().add(new XYChart.Data<>(String.valueOf(i + 1), counts[i]));
        }

        barChart.getData().add(series);

        // Configurazioni assi
        CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();
        xAxis.setLabel("Stelle");

        NumberAxis yAxis = (NumberAxis) barChart.getYAxis();
        yAxis.setLabel("Numero");
    }

    private void popolaListaRecensioni(List<RecensioneBean> recensioni) {
        reviewsContainer.getChildren().clear();

        if (recensioni.isEmpty()) {
            Label empty = new Label("Nessuna recensione ricevuta.");
            empty.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            reviewsContainer.getChildren().add(empty);
            return;
        }

        for (RecensioneBean bean : recensioni) {
            reviewsContainer.getChildren().add(createReviewBox(bean));
        }
    }

    // Helper per creare il box in stile Glassmorphism (sostituisce la vecchia TextArea)
    private VBox createReviewBox(RecensioneBean bean) {
        VBox box = new VBox();
        box.setSpacing(5);
        box.setStyle(ITEM_STYLE);

        Label studentLabel = new Label("Studente: " + bean.getStudentEmail());
        studentLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label ratingLabel = new Label("Voto: " + bean.getSentimentValue() + "/5");
        ratingLabel.setStyle("-fx-text-fill: #f1c40f; -fx-font-weight: bold;"); // Giallo per le stelle

        Label reviewText = new Label(bean.getRecensione());
        reviewText.setStyle("-fx-text-fill: #ecf0f1; -fx-font-style: italic;");
        reviewText.setWrapText(true);

        box.getChildren().addAll(studentLabel, ratingLabel, reviewText);
        return box;
    }

    @FXML
    private void backAction(ActionEvent event) {
        switchScene("/ldg/progettoispw/HomePageTutor.fxml", event);
    }
}