// src/main/java/core/strategies/ICalculationStrategy.java
package core.strategies;

import core.Models.CalculationResult;
import java.util.List;

public interface ICalculationStrategy {
    CalculationResult calculate(List<Double> data);
}