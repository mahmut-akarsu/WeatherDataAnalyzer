// src/main/java/controllers/AppController.java
package controllers;

import core.Enums;
import core.Models;
import core.StatisticsFacade;
import ui.MainView; // MainView sınıfını import etmemiz gerekecek

import java.io.File;
import java.util.List;

public class AppController {
    private StatisticsFacade statisticsFacade;
    private MainView mainView; // Controller'ın View'a referansı

    // Constructor
    public AppController(StatisticsFacade statisticsFacade, MainView mainView) {
        this.statisticsFacade = statisticsFacade;
        this.mainView = mainView;
        // this.mainView.setController(this); // View'a Controller'ı tanıtmak için (Main.java'da yaptık)
    }

    // View tarafından çağrılacak metod (GUI'deki "Hesapla" butonu için)
    public void handleCalculateButtonClick() {
        String selectedFolderPath = mainView.getSelectedFolderPath();
        if (selectedFolderPath == null || selectedFolderPath.trim().isEmpty()) {
            mainView.displayMessage("Lütfen geçerli bir klasör seçin.", true);
            return;
        }

        File folder = new File(selectedFolderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            mainView.displayMessage("Seçilen yol geçerli bir klasör değil.", true);
            return;
        }

        List<Models.OperationRequest> selectedOperations = mainView.getSelectedOperations();
        if (selectedOperations == null || selectedOperations.isEmpty()) {
            mainView.displayMessage("Lütfen en az bir hesaplama türü seçin.", true);
            return;
        }

        // Arka planda çalıştırılması daha iyi olabilir (SwingWorker kullanarak)
        // ama şimdilik direkt çağırıyoruz.
        mainView.displayMessage("Hesaplamalar yapılıyor, lütfen bekleyin...", false);

        // Facade üzerinden işlemleri başlat
        Models.CalculationSummary summary = statisticsFacade.processCalculations(selectedFolderPath, selectedOperations);

        // Sonucu View'a bildir
        if (summary.isSuccess()) {
            mainView.displayMessage(summary.getMessage() + " Sonuçların olduğu klasör: " + summary.getOutputFolderPath(), false);
            // mainView.displayResultsPath(summary.getOutputFolderPath()); // Ayrı bir metod da olabilir
        } else {
            mainView.displayMessage("Hata: " + summary.getMessage(), true);
        }
    }

    // Opsiyonel: Klasör seçimi değiştiğinde çağrılabilir
    public void handleFolderSelectionChanged(String newPath) {
        // Gerekirse burada bir işlem yapılabilir, örneğin önizleme vs.
        // Şimdilik boş bırakabiliriz.
        System.out.println("Seçilen klasör değişti: " + newPath);
    }

}