// src/main/java/core/StatisticsFacade.java
package core;

import core.Enums.DataType;
import core.Enums.OperationType;
import core.Models.*;
import core.services.FileReaderService;
import core.services.ResultWriterService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsFacade {
    private final FileReaderService fileReaderService;
    private final DataProcessor dataProcessor;
    private final ResultWriterService resultWriterService;

    public StatisticsFacade(FileReaderService fileReaderService,
                            DataProcessor dataProcessor,
                            ResultWriterService resultWriterService) {
        this.fileReaderService = fileReaderService;
        this.dataProcessor = dataProcessor;
        this.resultWriterService = resultWriterService;
    }

    public CalculationSummary processCalculations(String rootFolderPath, List<OperationRequest> requests) {
        List<MeasurementFile> temperatureFiles = new ArrayList<>();
        List<MeasurementFile> humidityFiles = new ArrayList<>();

        try {
            temperatureFiles = fileReaderService.readMeasurementFiles(rootFolderPath, DataType.TEMPERATURE);
            humidityFiles = fileReaderService.readMeasurementFiles(rootFolderPath, DataType.HUMIDITY);
        } catch (IOException e) {
            System.err.println("Dosyalar okunurken kritik bir hata oluştu: " + e.getMessage());
            return new CalculationSummary(null, null, null, null, null, false, "Dosya okuma hatası: " + e.getMessage());
        }

        List<FileCalculationResult> tempFileResults = new ArrayList<>();
        List<FileCalculationResult> humFileResults = new ArrayList<>();
        Map<OperationType, CalculationResult> globalTempResults = new HashMap<>();
        Map<OperationType, CalculationResult> globalHumResults = new HashMap<>();

        // Global hesaplamalar için tüm verileri topla
        List<Double> allTemperatureData = temperatureFiles.stream()
                .flatMap(mf -> mf.getDataPoints().stream().map(DataPoint::getValue))
                .collect(Collectors.toList());
        List<Double> allHumidityData = humidityFiles.stream()
                .flatMap(mf -> mf.getDataPoints().stream().map(DataPoint::getValue))
                .collect(Collectors.toList());


        for (OperationRequest request : requests) {
            OperationType opType = request.getOperationType();

            if (request.isGlobal()) {
                // Global hesaplama
                if (request.getSpecificDataType() == null || request.getSpecificDataType() == DataType.TEMPERATURE) {
                    if (!allTemperatureData.isEmpty()) {
                         CalculationResult globalTempResult = dataProcessor.calculateGlobal(allTemperatureData, opType);
                         globalTempResults.put(opType, globalTempResult);
                    } else if (request.getSpecificDataType() == DataType.TEMPERATURE) {
                         globalTempResults.put(opType, new CalculationResult(opType == OperationType.FREQUENCY ? new ArrayList<>() : 0.0)); // Boş veri için varsayılan
                    }
                }
                if (request.getSpecificDataType() == null || request.getSpecificDataType() == DataType.HUMIDITY) {
                     if (!allHumidityData.isEmpty()) {
                        CalculationResult globalHumResult = dataProcessor.calculateGlobal(allHumidityData, opType);
                        globalHumResults.put(opType, globalHumResult);
                    } else if (request.getSpecificDataType() == DataType.HUMIDITY) {
                        globalHumResults.put(opType, new CalculationResult(opType == OperationType.FREQUENCY ? new ArrayList<>() : 0.0)); // Boş veri için varsayılan
                    }
                }
            } else {
                // Dosya bazlı hesaplama
                 if (request.getSpecificDataType() == null || request.getSpecificDataType() == DataType.TEMPERATURE) {
                    tempFileResults.addAll(dataProcessor.calculateForAllFiles(temperatureFiles, opType));
                }
                if (request.getSpecificDataType() == null || request.getSpecificDataType() == DataType.HUMIDITY) {
                    humFileResults.addAll(dataProcessor.calculateForAllFiles(humidityFiles, opType));
                }
            }
        }

        CalculationSummary summary = new CalculationSummary(
                tempFileResults, humFileResults,
                globalTempResults, globalHumResults,
                null, true, "Hesaplamalar başarıyla tamamlandı." // outputFolderPath ve mesaj sonra ayarlanacak
        );

        try {
            resultWriterService.writeResults(summary, rootFolderPath); // outputFolderPath burada summary'ye set edilecek
        } catch (IOException e) {
            System.err.println("Sonuçlar yazılırken hata: " + e.getMessage());
            summary.setSuccess(false);
            summary.setMessage("Sonuç yazma hatası: " + e.getMessage());
        }

        return summary;
    }
}