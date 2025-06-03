// src/main/java/Main.java
import controllers.AppController;
import core.StatisticsFacade;
import core.factories.CalculationStrategyFactory;
import core.services.FileReaderService;
import core.services.ResultWriterService;
import ui.MainView;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Swing uygulamaları Event Dispatch Thread (EDT) üzerinde çalıştırılmalı
        SwingUtilities.invokeLater(() -> {
            // Bağımlılıkları oluştur
            FileReaderService fileReaderService = new FileReaderService();
            ResultWriterService resultWriterService = new ResultWriterService();
            CalculationStrategyFactory strategyFactory = new CalculationStrategyFactory();
            // DataProcessor'ı strategyFactory ile oluştur (DataProcessor constructor'ına ekleyeceğiz)
            // StatisticsFacade'i servislerle ve DataProcessor ile oluştur (Facade constructor'ına ekleyeceğiz)
            // AppController'ı Facade ile oluştur (AppController constructor'ına ekleyeceğiz)
            // MainView'ı oluştur

            // Şimdilik placeholder, bu constructor'ları daha sonra dolduracağız
            // DataProcessor dataProcessor = new DataProcessor(strategyFactory);
            // StatisticsFacade statisticsFacade = new StatisticsFacade(fileReaderService, dataProcessor, resultWriterService);
            // AppController appController = new AppController(statisticsFacade);
            // MainView mainView = new MainView(appController);
            // appController.setView(mainView); // Controller'a View'ı tanıtmak için

            // Yukarıdakiler yerine, tam bağımlılık enjeksiyonu ile:
            MainView mainView = new MainView(); // Önce View'ı oluşturalım
            CalculationStrategyFactory calculationStrategyFactory = new CalculationStrategyFactory();
            core.DataProcessor dataProcessor = new core.DataProcessor(calculationStrategyFactory);
            StatisticsFacade statisticsFacade = new StatisticsFacade(
                    new FileReaderService(),
                    dataProcessor,
                    new ResultWriterService()
            );
            AppController appController = new AppController(statisticsFacade, mainView);
            mainView.setController(appController); // View'a Controller'ı tanıt

            mainView.initializeUI(); // GUI'yi başlat ve görünür yap
        });
    }
}