// src/main/java/core/strategies/FrequencyStrategy.java
package core.strategies;

import core.Models.CalculationResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrequencyStrategy implements ICalculationStrategy {
    @Override
    public CalculationResult calculate(List<Double> data) {
        Map<Double, Integer> frequencies = new HashMap<>();
        if (data != null) {
            for (Double value : data) {
                frequencies.put(value, frequencies.getOrDefault(value, 0) + 1);
            }
        }
        return new CalculationResult(frequencies); // CalculationResult'ın value'su Map olacak
    }
}