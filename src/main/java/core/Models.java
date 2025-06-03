// src/main/java/core/Models.java
package core;

import java.util.List;
import java.util.Map;

// DataPoint sınıfı
public class Models {

    public static class DataPoint {
        private String timestamp;
        private double value; // Değerleri double olarak tutalım

        public DataPoint(String timestamp, double value) {
            this.timestamp = timestamp;
            this.value = value;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public double getValue() {
            return value;
        }

        @Override
        public String toString() {
            return timestamp + "," + value;
        }
    }

    public static class FileMetadata {
        private String id;
        private String measurementType; // "sıcaklık" veya "nem"
        private String location;
        private String date; // "GG.AA.YYYY" formatında

        public FileMetadata(String id, String measurementType, String location, String date) {
            this.id = id;
            this.measurementType = measurementType;
            this.location = location;
            this.date = date;
        }

        public String getId() { return id; }
        public String getMeasurementType() { return measurementType; }
        public String getLocation() { return location; }
        public String getDate() { return date; }

        @Override
        public String toString() {
            return "id:" + id + " ölçüm: " + measurementType + " - yer: " + location + " - tarih: " + date;
        }
    }

    public static class MeasurementFile {
        private String filePath;
        private FileMetadata metadata;
        private List<DataPoint> dataPoints;
        private Enums.DataType dataType; // Sıcaklık mı Nem mi olduğunu belirtmek için

        public MeasurementFile(String filePath, FileMetadata metadata, List<DataPoint> dataPoints, Enums.DataType dataType) {
            this.filePath = filePath;
            this.metadata = metadata;
            this.dataPoints = dataPoints;
            this.dataType = dataType;
        }

        public String getFilePath() { return filePath; }
        public FileMetadata getMetadata() { return metadata; }
        public List<DataPoint> getDataPoints() { return dataPoints; }
        public Enums.DataType getDataType() { return dataType; }
    }

    public static class CalculationResult {
        // Frekans sonucu Map<Double, Integer> olabilir, diğerleri tek bir Double
        private Object value; // Genel bir tip kullanalım, sonra cast ederiz

        public CalculationResult(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return value;
        }
    }

    // FrequencyResult CalculationResult'tan türeyebilir veya doğrudan kullanılabilir.
    // Şimdilik CalculationResult içinde Map olarak tutmayı deneyelim.
    // public static class FrequencyResult extends CalculationResult {
    //     public FrequencyResult(Map<Double, Integer> frequencies) {
    //         super(frequencies);
    //     }
    //     @SuppressWarnings("unchecked")
    //     public Map<Double, Integer> getFrequencies() {
    //         return (Map<Double, Integer>) getValue();
    //     }
    // }

    public static class FileCalculationResult {
        private FileMetadata fileMetadata;
        private CalculationResult result;
        private Enums.OperationType operationType; // Hangi işlem için olduğu

        public FileCalculationResult(FileMetadata fileMetadata, CalculationResult result, Enums.OperationType operationType) {
            this.fileMetadata = fileMetadata;
            this.result = result;
            this.operationType = operationType;
        }

        public FileMetadata getFileMetadata() { return fileMetadata; }
        public CalculationResult getResult() { return result; }
        public Enums.OperationType getOperationType() { return operationType; }
    }

    public static class CalculationSummary {
        // Her bir dosya için yapılan hesaplamalar (Sıcaklık)
        private List<FileCalculationResult> temperatureFileResults;
        // Her bir dosya için yapılan hesaplamalar (Nem)
        private List<FileCalculationResult> humidityFileResults;

        // Global hesaplamalar (Sıcaklık)
        private Map<Enums.OperationType, CalculationResult> globalTemperatureResults;
        // Global hesaplamalar (Nem)
        private Map<Enums.OperationType, CalculationResult> globalHumidityResults;

        private String outputFolderPath;
        private boolean success;
        private String message;

        public CalculationSummary(List<FileCalculationResult> temperatureFileResults,
                                  List<FileCalculationResult> humidityFileResults,
                                  Map<Enums.OperationType, CalculationResult> globalTemperatureResults,
                                  Map<Enums.OperationType, CalculationResult> globalHumidityResults,
                                  String outputFolderPath, boolean success, String message) {
            this.temperatureFileResults = temperatureFileResults;
            this.humidityFileResults = humidityFileResults;
            this.globalTemperatureResults = globalTemperatureResults;
            this.globalHumidityResults = globalHumidityResults;
            this.outputFolderPath = outputFolderPath;
            this.success = success;
            this.message = message;
        }

        // Getter'lar
        public List<FileCalculationResult> getTemperatureFileResults() { return temperatureFileResults; }
        public List<FileCalculationResult> getHumidityFileResults() { return humidityFileResults; }
        public Map<Enums.OperationType, CalculationResult> getGlobalTemperatureResults() { return globalTemperatureResults; }
        public Map<Enums.OperationType, CalculationResult> getGlobalHumidityResults() { return globalHumidityResults; }
        public String getOutputFolderPath() { return outputFolderPath; }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }

        // Setter'lar (Gerekirse, genellikle builder pattern veya constructor ile ayarlanır)
        public void setOutputFolderPath(String outputFolderPath) { this.outputFolderPath = outputFolderPath; }
        public void setSuccess(boolean success) { this.success = success; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class OperationRequest {
        private Enums.OperationType operationType;
        private boolean isGlobal;
        private Enums.DataType specificDataType; // Eğer tek bir tip içinse (örn: sadece sıcaklık ortalaması)

        public OperationRequest(Enums.OperationType operationType, boolean isGlobal) {
            this.operationType = operationType;
            this.isGlobal = isGlobal;
            this.specificDataType = null; // Varsayılan olarak her iki tip için
        }

        public OperationRequest(Enums.OperationType operationType, boolean isGlobal, Enums.DataType specificDataType) {
            this.operationType = operationType;
            this.isGlobal = isGlobal;
            this.specificDataType = specificDataType;
        }


        public Enums.OperationType getOperationType() { return operationType; }
        public boolean isGlobal() { return isGlobal; }
        public Enums.DataType getSpecificDataType() { return specificDataType; }
    }
}