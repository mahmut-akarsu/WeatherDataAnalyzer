// src/main/java/core/Enums.java
package core;

public class Enums {

    public enum OperationType {
        AVERAGE("Ortalama Bul"),
        MAXIMUM("Maximum Bul"),
        MINIMUM("Minimum Bul"),
        STANDARD_DEVIATION("Standard Sapmayı Bul"),
        FREQUENCY("Frekansı Bul"),
        MEDIAN("Median Bul");

        private final String displayName;

        OperationType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static OperationType fromDisplayName(String displayName) {
            for (OperationType type : values()) {
                if (type.displayName.equals(displayName)) {
                    return type;
                }
            }
            return null; // veya IllegalArgumentException fırlat
        }
    }

    public enum DataType {
        TEMPERATURE("sıcaklık"),
        HUMIDITY("nem");

        private final String folderName;

        DataType(String folderName) {
            this.folderName = folderName;
        }

        public String getFolderName() {
            return folderName;
        }
    }
}