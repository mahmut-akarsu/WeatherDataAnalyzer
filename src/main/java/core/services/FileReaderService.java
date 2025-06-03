// src/main/java/core/services/FileReaderService.java
package core.services;

import core.Enums;
import core.Models;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileReaderService {

	private static final Pattern FILENAME_PATTERN = Pattern.compile(
	        "id(\\d+)_([^_]+)_(.+?)_(\\d{2}\\.\\d{2}\\.\\d{4})\\.txt", Pattern.CASE_INSENSITIVE);

    private static final Pattern HEADER_PATTERN = Pattern.compile(
            "id:(\\w+)\\s+ölçüm:\\s*(\\S+)\\s*-\\s*yer:\\s*(\\S+)\\s*-\\s*tarih:\\s*(\\S+)");


    public List<Models.MeasurementFile> readMeasurementFiles(String rootFolderPath, Enums.DataType dataTypeToRead) throws IOException {
        System.out.println("[FileReader] " + dataTypeToRead + " için okunacak kök klasör: " + rootFolderPath); // LOG
        List<Models.MeasurementFile> measurementFiles = new ArrayList<>();
        Path dataSpecificFolderPath = Paths.get(rootFolderPath, dataTypeToRead.getFolderName());
        System.out.println("[FileReader] " + dataTypeToRead + " için spesifik klasör: " + dataSpecificFolderPath); // LOG

        if (!Files.exists(dataSpecificFolderPath) || !Files.isDirectory(dataSpecificFolderPath)) {
            System.err.println("[FileReader] UYARI: Klasör bulunamadı veya bir klasör değil: " + dataSpecificFolderPath);
            return measurementFiles;
        }

        try (Stream<Path> paths = Files.walk(dataSpecificFolderPath, 1)) {
            List<File> filesInFolder = paths
                    .filter(Files::isRegularFile)
                    .peek(path -> System.out.println("[FileReader] Bulunan dosya adayı: " + path.getFileName())) // LOG
                    .filter(path -> FILENAME_PATTERN.matcher(path.getFileName().toString()).matches())
                    .peek(path -> System.out.println("[FileReader] FILENAME_PATTERN eşleşti: " + path.getFileName())) // LOG
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            System.out.println("[FileReader] " + dataSpecificFolderPath + " klasöründe işlenecek dosya sayısı: " + filesInFolder.size()); // LOG

            for (File file : filesInFolder) {
                System.out.println("[FileReader] İşleniyor: " + file.getAbsolutePath()); // LOG
                try {
                    Models.MeasurementFile measurementFile = parseFileContent(file.getAbsolutePath(), dataTypeToRead);
                    if (measurementFile != null) {
                        System.out.println("[FileReader] Başarıyla parse edildi: " + measurementFile.getFilePath() + ", Veri noktası: " + measurementFile.getDataPoints().size()); // LOG
                        measurementFiles.add(measurementFile);
                    } else {
                        System.err.println("[FileReader] UYARI: parseFileContent null döndürdü: " + file.getName());
                    }
                } catch (IOException e) {
                    System.err.println("[FileReader] HATA: Dosya okunurken hata: " + file.getName() + " - " + e.getMessage());
                }
            }
        }
        System.out.println("[FileReader] " + dataTypeToRead + " için toplam okunan ve parse edilen MeasurementFile sayısı: " + measurementFiles.size()); // LOG
        return measurementFiles;
    }

    private Models.FileMetadata parseMetadataFromHeader(String headerLine) {
        Matcher matcher = HEADER_PATTERN.matcher(headerLine.trim());
        if (matcher.matches()) {
            String id = matcher.group(1);
            String type = matcher.group(2);
            String location = matcher.group(3);
            String date = matcher.group(4);
            System.out.println("[FileReader] Başlık parse edildi: id=" + id + ", type=" + type + ", loc=" + location + ", date=" + date); // LOG
            return new Models.FileMetadata(id, type, location, date);
        }
        System.err.println("[FileReader] UYARI: Başlık satırı (" + headerLine + ") beklenen formatta değil. Regex: " + HEADER_PATTERN.pattern());
        return null;
    }

    private Models.MeasurementFile parseFileContent(String filePath, Enums.DataType expectedDataType) throws IOException {
        File file = new File(filePath);
        Models.FileMetadata metadataFromHeader = null;
        List<Models.DataPoint> dataPoints = new ArrayList<>();
        System.out.println("[FileReader_parseFileContent] Başlanıyor: " + filePath); // LOG

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                System.out.println("[FileReader_parseFileContent] Okunan satır " + lineNumber + ": '" + line + "'"); // LOG
                if (line.isEmpty()) {
                    System.out.println("[FileReader_parseFileContent] Satır " + lineNumber + " boş, atlanıyor."); // LOG
                    continue;
                }

                if (firstLine) {
                    metadataFromHeader = parseMetadataFromHeader(line);
                    if (metadataFromHeader == null) {
                        System.err.println("[FileReader_parseFileContent] HATA: " + filePath + " için başlık bilgisi (satır 1) okunamadı.");
                        return null;
                    }
                    // Ölçüm tipi kontrolü
                    String headerMeasurementType = metadataFromHeader.getMeasurementType().toLowerCase();
                    String expectedFolderName = expectedDataType.getFolderName().toLowerCase();
                    String expectedEnumName = expectedDataType.name().toLowerCase();

                    if (!headerMeasurementType.equals(expectedFolderName) && !headerMeasurementType.equals(expectedEnumName)) {
                        System.err.println("[FileReader_parseFileContent] UYARI: " + filePath +
                                " dosyasındaki başlık ölçüm tipi (" + headerMeasurementType +
                                ") beklenen tiple (" + expectedFolderName + " veya " + expectedEnumName + ") eşleşmiyor.");
                        // return null; // Eşleşmiyorsa dosyayı işlemeyi durdurabiliriz. Test için şimdilik devam etsin.
                    }
                    firstLine = false;
                } else {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        try {
                            String timestamp = parts[0].trim();
                            double value = Double.parseDouble(parts[1].trim());
                            dataPoints.add(new Models.DataPoint(timestamp, value));
                            System.out.println("[FileReader_parseFileContent] Veri noktası eklendi: " + timestamp + ", " + value); // LOG
                        } catch (NumberFormatException e) {
                            System.err.println("[FileReader_parseFileContent] UYARI: Geçersiz sayı formatı (satır " + lineNumber + "): '" + line + "' dosya: " + filePath);
                        }
                    } else {
                        System.err.println("[FileReader_parseFileContent] UYARI: Geçersiz veri satırı formatı (satır " + lineNumber + "): '" + line + "' dosya: " + filePath + ". Beklenen format: zaman,değer");
                    }
                }
            }
        }

        if (metadataFromHeader == null && dataPoints.isEmpty()) {
             // Eğer dosya tamamen boşsa veya ilk satır okunamadıysa ve hiç veri yoksa.
            System.err.println("[FileReader_parseFileContent] UYARI: " + filePath + " için metadata başlık satırından okunamadı ve hiç veri noktası bulunamadı.");
            return null;
        }
        if (metadataFromHeader == null && !dataPoints.isEmpty()){
            // Bu durum olmamalı, başlık olmadan veri olmamalı ama yine de bir log
            System.err.println("[FileReader_parseFileContent] KRİTİK UYARI: " + filePath + " için metadata yok ama veri noktası var! Bu durum incelenmeli.");
            // Geçici olarak null bir metadata ile devam etmesini sağlayabiliriz ya da null dönebiliriz.
            // return null;
            // Test için sahte bir metadata oluşturalım:
            metadataFromHeader = new Models.FileMetadata("SAHTE_ID", expectedDataType.name(), "SAHTE_YER", "00.00.0000");
            System.err.println("[FileReader_parseFileContent] SAHTE metadata oluşturuldu.");
        }


        System.out.println("[FileReader_parseFileContent] Tamamlandı: " + filePath + ", Toplam veri noktası: " + dataPoints.size()); // LOG
        return new Models.MeasurementFile(filePath, metadataFromHeader, dataPoints, expectedDataType);
    }
}