// src/main/java/core/strategies/MinStrategy.java
package core.strategies;

import core.Models.CalculationResult;
import java.util.Collections;
import java.util.List;

public class MinStrategy implements ICalculationStrategy {
    @Override
    public CalculationResult calculate(List<Double> data) {
        if (data == null || data.isEmpty()) {
            return new CalculationResult(Double.POSITIVE_INFINITY); // Veya uygun bir varsayılan
        }
        return new CalculationResult(Collections.min(data));
    }
}