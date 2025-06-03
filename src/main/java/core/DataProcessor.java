// src/main/java/core/DataProcessor.java
package core;

import core.Enums.DataType;
import core.Enums.OperationType;
import core.Models.CalculationResult;
import core.Models.FileCalculationResult;
import core.Models.MeasurementFile;
import core.factories.CalculationStrategyFactory;
import core.strategies.ICalculationStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataProcessor {
    private final CalculationStrategyFactory strategyFactory;

    public DataProcessor(CalculationStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    /**
     * Verilen bir veri listesi üzerinde belirli bir operasyonu gerçekleştirir.
     */
    public CalculationResult calculate(List<Double> data, OperationType operationType) {
        if (data == null) {
            // Duruma göre boş bir sonuç veya hata döndür
            if (operationType == OperationType.FREQUENCY) {
                return new CalculationResult(new ArrayList<>()); // Boş frekans listesi
            }
            return new CalculationResult(0.0); // Veya Double.NaN
        }
        ICalculationStrategy strategy = strategyFactory.createStrategy(operationType);
        return strategy.calculate(data);
    }

    /**
     * Birden fazla ölçüm dosyası için belirli bir operasyonu gerçekleştirir.
     * Her dosyanın sonucu ayrı bir FileCalculationResult olarak döner.
     */
    public List<FileCalculationResult> calculateForAllFiles(List<MeasurementFile> measurementFiles, OperationType operationType) {
        List<FileCalculationResult> results = new ArrayList<>();
        if (measurementFiles == null) return results;

        ICalculationStrategy strategy = strategyFactory.createStrategy(operationType);

        for (MeasurementFile file : measurementFiles) {
            if (file.getDataPoints() == null || file.getDataPoints().isEmpty()) {
                // Boş veri noktası olan dosyalar için ne yapılacağına karar verilmeli
                // Şimdilik boş bir sonuçla veya null ile atlayabiliriz.
                // Ya da ödevdeki çıktı formatına göre bir değer üretebiliriz (örn: 0, NaN)
                CalculationResult emptyResult;
                if (operationType == OperationType.FREQUENCY) {
                    emptyResult = new CalculationResult(new ArrayList<>());
                } else {
                    emptyResult = new CalculationResult(0.0); // Veya Double.NaN
                }
                results.add(new FileCalculationResult(file.getMetadata(), emptyResult, operationType));
                continue;
            }

            List<Double> dataValues = file.getDataPoints().stream()
                    .map(Models.DataPoint::getValue)
                    .collect(Collectors.toList());

            CalculationResult result = strategy.calculate(dataValues);
            results.add(new FileCalculationResult(file.getMetadata(), result, operationType));
        }
        return results;
    }

    /**
     * Belirli bir veri tipi için (sıcaklık veya nem) tüm dosyalardaki tüm veriler
     * üzerinden global bir hesaplama yapar.
     * allDataForType: İlgili veri tipine ait tüm dosyalardan toplanmış double değer listesi.
     */
    public CalculationResult calculateGlobal(List<Double> allDataForType, OperationType operationType) {
         if (allDataForType == null || allDataForType.isEmpty()) {
            if (operationType == OperationType.FREQUENCY) {
                return new CalculationResult(new ArrayList<>());
            }
            return new CalculationResult(0.0); // Veya Double.NaN
        }
        ICalculationStrategy strategy = strategyFactory.createStrategy(operationType);
        return strategy.calculate(allDataForType);
    }
}