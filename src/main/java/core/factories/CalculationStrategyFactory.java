// src/main/java/core/factories/CalculationStrategyFactory.java
package core.factories;

import core.Enums.OperationType;
import core.strategies.*;

public class CalculationStrategyFactory {

    public ICalculationStrategy createStrategy(OperationType operationType) {
        if (operationType == null) {
            throw new IllegalArgumentException("Operation type cannot be null.");
        }
        switch (operationType) {
            case AVERAGE:
                return new AverageStrategy();
            case MAXIMUM:
                return new MaxStrategy();
            case MINIMUM:
                return new MinStrategy();
            case STANDARD_DEVIATION:
                return new StdDevStrategy();
            case FREQUENCY:
                return new FrequencyStrategy();
            case MEDIAN:
                return new MedianStrategy();
            default:
                throw new IllegalArgumentException("Unknown operation type: " + operationType);
        }
    }
}