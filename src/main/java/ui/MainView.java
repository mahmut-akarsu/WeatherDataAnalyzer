// src/main/java/ui/MainView.java
package ui;

import controllers.AppController;
import core.Enums;
import core.Models;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap; // Checkbox sırasını korumak için
import java.util.List;
import java.util.Map;

public class MainView extends JFrame {

    private AppController controller;

    // GUI Bileşenleri
    private JTextField folderPathTextField;
    private JButton selectFolderButton;
    private JButton calculateButton;
    private JLabel messageLabel;
    private JTextArea messageArea; // Daha uzun mesajlar için

    // Checkbox'ları tutmak için bir yapı
    // Key: OperationType, Value: [Normal CheckBox, Global CheckBox]
    private Map<Enums.OperationType, JCheckBox[]> operationCheckBoxes;

    public MainView() {
        // AppController constructor'da set edilecek
    }

    public void setController(AppController controller) {
        this.controller = controller;
    }

    public void initializeUI() {
        setTitle("İstatistiksel Analiz Uygulaması - BYM304 ÖDEV 2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setSize(700, 550); // Boyutu içeriğe göre ayarla veya pack() kullan
        setLayout(new BorderLayout(10, 10)); // Ana layout BorderLayout

        // Ana paneli oluştur ve kenar boşlukları ekle
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // 1. Klasör Seçim Paneli (North)
        JPanel folderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        folderPanel.add(new JLabel("Klasör Seç:"));
        folderPathTextField = new JTextField(30);
        folderPathTextField.setEditable(false); // Kullanıcı elle değiştiremesin
        folderPanel.add(folderPathTextField);
        selectFolderButton = new JButton("Gözat...");
        selectFolderButton.addActionListener(e -> selectFolderAction());
        folderPanel.add(selectFolderButton);
        mainPanel.add(folderPanel, BorderLayout.NORTH);

        // 2. Operasyon Seçim Paneli (Center) - GridLayout kullanalım
        JPanel operationsPanel = new JPanel(new GridLayout(0, 3, 10, 5)); // Satır, Sütun, Hgap, Vgap
        operationsPanel.setBorder(BorderFactory.createTitledBorder("Hesaplama İşlemleri"));

        operationCheckBoxes = new LinkedHashMap<>(); // Ekleme sırasını korumak için
        for (Enums.OperationType opType : Enums.OperationType.values()) {
            JCheckBox opCheckBox = new JCheckBox(opType.getDisplayName());
            JCheckBox globalCheckBox = new JCheckBox("GLOBAL");
            operationCheckBoxes.put(opType, new JCheckBox[]{opCheckBox, globalCheckBox});

            operationsPanel.add(opCheckBox);
            operationsPanel.add(globalCheckBox);
            operationsPanel.add(new JLabel()); // Sütunları hizalamak için boş label
        }
        mainPanel.add(new JScrollPane(operationsPanel), BorderLayout.CENTER); // Kaydırılabilir yapalım


        // 3. Hesapla Butonu ve Mesaj Paneli (South)
        JPanel bottomPanel = new JPanel(new BorderLayout(10,10));

        calculateButton = new JButton("HESAPLA");
        calculateButton.setFont(new Font("Arial", Font.BOLD, 16));
        calculateButton.addActionListener(e -> {
            if (controller != null) {
                controller.handleCalculateButtonClick();
            }
        });
        // Butonu bir panele ekleyip ortalamak için
        JPanel buttonContainerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonContainerPanel.add(calculateButton);
        bottomPanel.add(buttonContainerPanel, BorderLayout.NORTH);


        messageArea = new JTextArea(5, 40); // Satır, Sütun
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setBorder(BorderFactory.createTitledBorder("MESAJ İÇERİĞİ"));
        bottomPanel.add(messageScrollPane, BorderLayout.CENTER);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        pack(); // Pencere boyutunu içeriğe göre ayarla
        setLocationRelativeTo(null); // Pencereyi ekranın ortasında aç
        setVisible(true);
    }

    private void selectFolderAction() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Ölçüm Kök Klasörünü Seçin");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Sadece klasör seçilsin
        fileChooser.setAcceptAllFileFilterUsed(false); // "All Files" filtresini kaldır

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            folderPathTextField.setText(selectedFile.getAbsolutePath());
            if (controller != null) {
                // controller.handleFolderSelectionChanged(selectedFile.getAbsolutePath()); // Gerekirse
            }
        }
    }

    // Controller'a seçilen klasör yolunu vermek için
    public String getSelectedFolderPath() {
        return folderPathTextField.getText();
    }

    // Controller'a seçilen operasyonları vermek için
    public List<Models.OperationRequest> getSelectedOperations() {
        List<Models.OperationRequest> requests = new ArrayList<>();
        if (operationCheckBoxes == null) return requests;

        for (Map.Entry<Enums.OperationType, JCheckBox[]> entry : operationCheckBoxes.entrySet()) {
            Enums.OperationType opType = entry.getKey();
            JCheckBox opCheckBox = entry.getValue()[0];
            JCheckBox globalCheckBox = entry.getValue()[1];

            if (opCheckBox.isSelected()) {
                requests.add(new Models.OperationRequest(opType, globalCheckBox.isSelected()));
            } else if (globalCheckBox.isSelected()){
                // Eğer ana checkbox seçili değil ama global seçiliyse, bu bir global istektir.
                // Bu durumu UI tasarımına göre netleştirmek gerekebilir.
                // Şu anki mantık: Ana checkbox seçiliyse ve global de seçiliyse, globaldir.
                // Sadece global seçiliyse ve ana değilse, bunu da global sayabiliriz.
                // Ya da ana seçilmeden global'in aktif olmamasını sağlayabiliriz.
                // Şimdilik: Eğer sadece global seçiliyse, ana işlem de istenmiş gibi davranıp global yapıyoruz.
                 requests.add(new Models.OperationRequest(opType, true));
            }
        }
        return requests;
    }

    // Mesajları göstermek için
    public void displayMessage(String message, boolean isError) {
        messageArea.setText(message); // Eski mesajı silip yenisini yaz
        if (isError) {
            messageArea.setForeground(Color.RED);
        } else {
            messageArea.setForeground(Color.BLACK); // Veya sistem varsayılanı
        }
    }

    // Sonuçların yazıldığı klasör yolunu göstermek için (opsiyonel, mesaja eklendi)
    // public void displayResultsPath(String path) {
    //     // Ayrı bir label'da veya mesaj alanına ekleyerek gösterebiliriz.
    //     messageArea.append("\nSonuçların olduğu klasör: " + path);
    // }

}