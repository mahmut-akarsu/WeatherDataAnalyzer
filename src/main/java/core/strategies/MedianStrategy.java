// src/main/java/core/strategies/MedianStrategy.java
package core.strategies;

import core.Models.CalculationResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MedianStrategy implements ICalculationStrategy {
    @Override
    public CalculationResult calculate(List<Double> data) {
        if (data == null || data.isEmpty()) {
            return new CalculationResult(0.0); // Veya NaN
        }

        List<Double> sortedData = new ArrayList<>(data); // Orijinal listeyi değiştirmemek için kopyala
        Collections.sort(sortedData);

        int size = sortedData.size();
        if (size % 2 == 1) {
            // Tek sayıda eleman varsa ortadaki eleman
            return new CalculationResult(sortedData.get(size / 2));
        } else {
            // Çift sayıda eleman varsa ortadaki iki elemanın ortalaması
            double mid1 = sortedData.get(size / 2 - 1);
            double mid2 = sortedData.get(size / 2);
            return new CalculationResult((mid1 + mid2) / 2.0);
        }
    }
}