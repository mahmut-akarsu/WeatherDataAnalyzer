// src/main/java/core/strategies/StdDevStrategy.java
package core.strategies;

import core.Models.CalculationResult;
import java.util.List;

public class StdDevStrategy implements ICalculationStrategy {
    @Override
    public CalculationResult calculate(List<Double> data) {
        if (data == null || data.size() < 2) { // Standart sapma için en az 2 veri noktası
            return new CalculationResult(0.0); // Veya NaN
        }

        double sum = 0.0;
        double standardDeviation = 0.0;
        int length = data.size();

        for (double num : data) {
            sum += num;
        }

        double mean = sum / length;

        for (double num : data) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return new CalculationResult(Math.sqrt(standardDeviation / length)); // Örneklem std sapması için (length-1) olabilir
    }
}