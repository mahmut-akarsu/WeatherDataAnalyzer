<div align="center">

![image](https://github.com/user-attachments/assets/72e1d887-6613-4d73-a083-f82f1bdcd3e6)


# 📊 İstatistiksel Veri Analiz Uygulaması 🌡️💧

**BYM304 - Yazılım Mimarisi ve Tasarımı Ödevi**

</div>

Bu Java Swing uygulaması, belirli bir klasör yapısındaki sıcaklık ve nem ölçüm verilerini işleyerek çeşitli istatistiksel analizler yapar ve sonuçları yapılandırılmış dosyalara kaydeder.

---

## 🌟 Temel Özellikler

*    intuitive Kullanıcı Arayüzü (Java Swing)
*   📁 Ana klasörden veri kaynağı seçimi
*   ☀️ Sıcaklık ve 💧 Nem verileri için ayrı alt klasörlerden okuma
*   🧮 Dosya bazlı ve genel (global) istatistiksel hesaplamalar:
    *   Ortalama
    *   Maksimum / Minimum Değerler
    *   Standart Sapma
    *   Frekans Dağılımı
    *   Medyan
*   📝 Sonuçların `sonuc` klasörüne yapılandırılmış formatta yazılması

---

## 🏗️ Kullanılan Mimari ve Tasarım Desenleri

Uygulama, **Model-View-Controller (MVC)** benzeri bir mimari yaklaşımla geliştirilmiştir. Bu, sorumlulukların net bir şekilde ayrılmasını sağlar:

*   **👁️ View (`ui.MainView`):** Kullanıcı arayüzünü sunar ve kullanıcı etkileşimlerini yakalar.
*   **🧠 Controller (`controllers.AppController`):** Kullanıcı isteklerini alır, iş mantığını tetikler ve View'ı günceller.
*   **⚙️ Model (Çekirdek Mantık - `core` paketi):**
    *   **`StatisticsFacade`:** İşlemler için basitleştirilmiş bir arayüz sunar (Cephe Deseni - *Facade Pattern*).
    *   **Servisler (`FileReaderService`, `DataProcessor`, `ResultWriterService`):** Belirli görevleri yerine getirir.
    *   **`ICalculationStrategy` ve Somut Stratejiler:** Farklı hesaplama algoritmalarını kapsüller (Strateji Deseni - *Strategy Pattern*).
    *   **`CalculationStrategyFactory`:** Strateji nesnelerinin oluşturulmasını yönetir (Basit Fabrika Yaklaşımı - *Simple Factory*).
    *   **`core.Models`:** Veri yapılarını ve transfer nesnelerini tanımlar.

Bu tasarım, kodun **modüler**, **anlaşılır** ve **genişletilebilir** olmasını hedefler.

---

## 🛠️ Gereksinimler

*   **Java Development Kit (JDK):** JDK 11 veya daha yeni bir sürümü.
    *   İndirme Linkleri: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) | [OpenJDK](https://openjdk.java.net/)

---

## 🚀 Kurulum ve Çalıştırma

1.  **Projeyi Edinin:**
    Bu depoyu yerel makinenize indirin veya Git kullanarak klonlayın:
    ```bash
    git clone https://github.com/mahmut-akarsu/WeatherDataAnalyzer.git
    cd WeatherDataAnalyzer
    ```

2.  **Java IDE'sinde Açın (Tavsiye Edilen):**
    *   IntelliJ IDEA, Eclipse veya NetBeans gibi bir IDE kullanın.
    *   Projeyi IDE'niz üzerinden "Open Project" veya "Import Project" seçeneğiyle açın (projenin kök dizinini veya `pom.xml` dosyasını işaret ederek).

3.  **Uygulamayı Başlatın:**
    *   IDE'nizin proje gezgininde `src/main/java/` dizini altındaki `Main.java` dosyasını bulun.
    *   `Main.java` dosyasına sağ tıklayarak **"Run 'Main.main()'"** (veya benzeri) seçeneği ile uygulamayı çalıştırın.

---

## 📁 Gerekli Klasör Yapısı ve Dosya Formatları

Uygulamanın doğru çalışması için ölçüm verilerinizin aşağıdaki yapı ve formatlara uygun olması **zorunludur**:

### 1. Ana Klasör Yapısı
Uygulamada "Gözat..." butonu ile seçeceğiniz **ana ölçüm klasörünün** içinde `sıcaklık` ve `nem` adlı iki alt klasör bulunmalıdır:

```plaintext
ana_olcum_klasoru/
├── sicaklik/
│   └── [sıcaklık_ölçüm_dosyaları.txt]
└── nem/
    └── [nem_ölçüm_dosyaları.txt]

### Ölçüm Dosyası Adlandırması
Her bir `.txt` ölçüm dosyası şu şemaya göre isimlendirilmelidir:
`id<SAYI>_<ÖLÇÜM_TİPİ>_<YER_BİLGİSİ>_<GG.AA.YYYY>.txt`

*   **Örnekler:**
    *   `id1_Sıcaklık_YER_A_01.06.2025.txt`
    *   `id10_Nem_OFIS_B_02.06.2025.txt`

###  Ölçüm Dosyası İçeriği Formatı

*   **İlk Satır (Başlık/Meta Veri):**
    `id:<SAYI> ölçüm: <ölçüm_tipi_küçük_harf> - yer: <YER_BİLGİSİ> - tarih: <GG.AA.YYYY>`
    *   *Örnek (Sıcaklık):* `id:1 ölçüm: sıcaklık - yer: YER_A - tarih: 01.06.2025`
    *   *Örnek (Nem):* `id:10 ölçüm: nem - yer: OFIS_B - tarih: 02.06.2025`

*   **Sonraki Satırlar (Veri Noktaları):**
    `SAAT:DAKİKA:SANİYE,DEĞER`
    *   *Örnek Veri Noktaları:*
        ```
        08:00:00,22.5
        08:05:00,23.0
        08:10:00,55
        ```

### Örnek Test Dosyaları:

**`ana_olcum_klasoru/sıcaklık/id1_Sıcaklık_BAHCE_10.05.2025.txt`:**
```
id:1 ölçüm: sıcaklık - yer: BAHCE - tarih: 10.05.2025
09:00:00,18.5
09:05:00,19.0
09:10:00,18.0
```

**`ana_olcum_klasoru/nem/id5_Nem_SALON_10.05.2025.txt`:**
```
id:5 ölçüm: nem - yer: SALON - tarih: 10.05.2025
10:00:00,60
10:05:00,62.5
10:10:00,61
```

---

## 💾 Sonuçların Kaydedilmesi

Hesaplama sonuçları, seçtiğiniz ana ölçüm klasörünün altında otomatik olarak oluşturulacak olan `sonuc` adlı bir klasörün içine (ayrı `sıcaklık` ve `nem` alt klasörleriyle) aşağıdaki yapıda kaydedilecektir:

```plaintext
ana_olcum_klasoru/
├── sicaklik/
├── nem/
└── sonuc/
    ├── sicaklik/
    │   └── [sonuc_dosyalari.txt]  // Örn: maximumdegerler.txt, globalortalama.txt
    └── nem/
        └── [sonuc_dosyalari.txt]
```

---
