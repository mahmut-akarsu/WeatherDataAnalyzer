// src/main/java/core/strategies/MaxStrategy.java
package core.strategies;

import core.Models.CalculationResult;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;

public class MaxStrategy implements ICalculationStrategy {
    @Override
    public CalculationResult calculate(List<Double> data) {
        if (data == null || data.isEmpty()) {
            return new CalculationResult(Double.NEGATIVE_INFINITY); // Veya uygun bir varsayılan
        }
        // Alternatif: data.stream().mapToDouble(d -> d).max().orElse(Double.NEGATIVE_INFINITY);
        return new CalculationResult(Collections.max(data));
    }
}