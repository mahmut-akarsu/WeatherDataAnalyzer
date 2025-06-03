// src/main/java/core/services/ResultWriterService.java
package core.services;

import core.Enums;
import core.Models;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ResultWriterService {

    public void writeResults(Models.CalculationSummary summary, String baseInputFolder) throws IOException {
        Path outputBaseFolderPath = Paths.get(baseInputFolder, "sonuc"); // Ana "sonuc" klasörü
        summary.setOutputFolderPath(outputBaseFolderPath.toString()); // Summary'ye yolu kaydet

        // "sonuc" klasörünü ve altındaki sıcaklık/nem klasörlerini oluştur
        createOutputDirectory(outputBaseFolderPath);
        Path tempOutputFolder = outputBaseFolderPath.resolve(Enums.DataType.TEMPERATURE.getFolderName());
        createOutputDirectory(tempOutputFolder);
        Path humOutputFolder = outputBaseFolderPath.resolve(Enums.DataType.HUMIDITY.getFolderName());
        createOutputDirectory(humOutputFolder);

        // Sıcaklık için dosya bazlı sonuçları yaz
        for (Models.FileCalculationResult fileResult : summary.getTemperatureFileResults()) {
            writeSingleFileResult(tempOutputFolder, fileResult);
        }
        // Sıcaklık için global sonuçları yaz
        for (Map.Entry<Enums.OperationType, Models.CalculationResult> entry : summary.getGlobalTemperatureResults().entrySet()) {
            writeGlobalResult(tempOutputFolder, entry.getKey(), entry.getValue());
        }

        // Nem için dosya bazlı sonuçları yaz
        for (Models.FileCalculationResult fileResult : summary.getHumidityFileResults()) {
            writeSingleFileResult(humOutputFolder, fileResult);
        }
        // Nem için global sonuçları yaz
        for (Map.Entry<Enums.OperationType, Models.CalculationResult> entry : summary.getGlobalHumidityResults().entrySet()) {
            writeGlobalResult(humOutputFolder, entry.getKey(), entry.getValue());
        }
    }

    private void createOutputDirectory(Path dirPath) throws IOException {
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
    }

    private String getResultFilename(Enums.OperationType operationType, boolean isGlobal) {
        String baseName = "";
        switch (operationType) {
            case AVERAGE: baseName = isGlobal ? "globalortalama" : "ortalamadegerler"; break;
            case MAXIMUM: baseName = isGlobal ? "globalmaximum" : "maximumdegerler"; break;
            case MINIMUM: baseName = isGlobal ? "globalminimum" : "minimumdegerler"; break;
            case STANDARD_DEVIATION: baseName = isGlobal ? "globalstandartsapma" : "standartsapmadegerler"; break;
            case FREQUENCY: baseName = isGlobal ? "globalfrekanslar" : "frekanslar"; break;
            case MEDIAN: baseName = isGlobal ? "globalmedyan" : "medyandegerler"; break;
            default: baseName = "bilinmeyensonuc"; break;
        }
        return baseName + ".txt";
    }


    private void writeSingleFileResult(Path outputFolder, Models.FileCalculationResult fileResult) throws IOException {
        Enums.OperationType opType = fileResult.getOperationType();
        String filename = getResultFilename(opType, false);
        Path filePath = outputFolder.resolve(filename);

        // Dosya zaten varsa append modunda aç, yoksa oluştur.
        // Her operasyon tipi için ayrı dosya olduğundan, dosya bazlı sonuçlar her zaman append edilecek.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), true))) {
            writer.write(fileResult.getFileMetadata().toString()); // id:1 ölçüm: sıcaklık - yer: YER - tarih: 11.11.2011
            writer.newLine();

            if (opType == Enums.OperationType.FREQUENCY) {
                @SuppressWarnings("unchecked")
                Map<Double, Integer> frequencies = (Map<Double, Integer>) fileResult.getResult().getValue();
                for (Map.Entry<Double, Integer> entry : frequencies.entrySet()) {
                    // Örnek çıktı: "9 Derece 8 defa ölçüldü"
                    // Değerin tam sayı olup olmadığını kontrol edip ona göre formatlayabiliriz.
                    String valueStr = (entry.getKey() == Math.floor(entry.getKey())) ?
                                      String.valueOf(entry.getKey().intValue()) :
                                      String.valueOf(entry.getKey());
                    writer.write(valueStr + " Derece " + entry.getValue() + " defa ölçüldü");
                    writer.newLine();
                }
            } else {
                // Diğer sonuçlar (ortalama, max, min, stddev, median)
                // Örnek çıktı: "max: 20" veya "ortalama: 15.5"
                String prefix = "";
                switch(opType) {
                    case AVERAGE: prefix = "ortalama: "; break;
                    case MAXIMUM: prefix = "max: "; break;
                    case MINIMUM: prefix = "min: "; break;
                    case STANDARD_DEVIATION: prefix = "stddev: "; break;
                    case MEDIAN: prefix = "medyan: "; break;
                }
                writer.write(prefix + fileResult.getResult().getValue().toString());
                writer.newLine();
            }
            writer.write("--------------------"); // Ayırıcı
            writer.newLine();
        }
    }

    private void writeGlobalResult(Path outputFolder, Enums.OperationType operationType, Models.CalculationResult result) throws IOException {
        String filename = getResultFilename(operationType, true);
        Path filePath = outputFolder.resolve(filename);

        // Global sonuçlar için dosya her zaman yeniden yazılır (overwrite).
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), false))) {
            if (operationType == Enums.OperationType.FREQUENCY) {
                 @SuppressWarnings("unchecked")
                Map<Double, Integer> frequencies = (Map<Double, Integer>) result.getValue();
                for (Map.Entry<Double, Integer> entry : frequencies.entrySet()) {
                     String valueStr = (entry.getKey() == Math.floor(entry.getKey())) ?
                                      String.valueOf(entry.getKey().intValue()) :
                                      String.valueOf(entry.getKey());
                    writer.write(valueStr + " Derece " + entry.getValue() + " defa ölçüldü");
                    writer.newLine();
                }
            } else {
                String prefix = "";
                 switch(operationType) {
                    case AVERAGE: prefix = "ortalama: "; break;
                    case MAXIMUM: prefix = "max: "; break;
                    case MINIMUM: prefix = "min: "; break;
                    case STANDARD_DEVIATION: prefix = "stddev: "; break;
                    case MEDIAN: prefix = "medyan: "; break;
                }
                writer.write(prefix + result.getValue().toString());
                writer.newLine();
            }
        }
    }
}