// src/main/java/core/strategies/AverageStrategy.java
package core.strategies;

import core.Models.CalculationResult;
import java.util.List;
import java.util.OptionalDouble;

public class AverageStrategy implements ICalculationStrategy {
    @Override
    public CalculationResult calculate(List<Double> data) {
        if (data == null || data.isEmpty()) {
            return new CalculationResult(0.0); // Veya NaN, veya hata fırlat
        }
        OptionalDouble average = data.stream().mapToDouble(d -> d).average();
        return new CalculationResult(average.isPresent() ? average.getAsDouble() : 0.0);
    }
}