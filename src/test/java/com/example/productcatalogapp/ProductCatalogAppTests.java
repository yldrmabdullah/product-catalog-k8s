package com.example.productcatalogapp;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Test metotlarını sırayla çalıştırmak için
public class ProductCatalogAppTests {

    private static WebDriver driver;
    private final String BASE_URL = "http://localhost:8081"; // Uygulamanızın çalıştığı portu güncelleyin

    // Bu metot, tüm testlerden önce bir kez çalışır
    @BeforeAll
    static void setup() {
        // ChromeDriver'ın yolunu ayarlayın.
        // Eğer PATH'inize eklediyseniz bu satıra gerek kalmaz.
        // Ya da projenizin kök dizininde "drivers" klasörü oluşturup içine koyduysanız:
        // System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe"); // Windows için
        // System.setProperty("webdriver.chrome.driver", "drivers/chromedriver"); // macOS/Linux için
        // VEYA daha robust bir yaklaşım:
        String chromeDriverPath = System.getProperty("user.dir") + "/drivers/chromedriver.exe"; // veya .exe

        // ChromeDriver'ın varlığını kontrol et
        java.io.File driverFile = new java.io.File(chromeDriverPath);
        if (!driverFile.exists()) {
            System.err.println("ChromeDriver bulunamadı: " + chromeDriverPath);
            System.err.println("Lütfen Chrome tarayıcınızın sürümüne uygun ChromeDriver'ı indirip 'drivers' klasörüne yerleştirin.");
            System.exit(1); // Uygulamayı sonlandır
        }
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);


        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Tarayıcıyı gizli modda çalıştır (GUI olmadan)
        options.addArguments("--disable-gpu"); // GPU kullanımı devre dışı bırak (headless için önerilir)
        options.addArguments("--window-size=1920,1080"); // Pencere boyutunu ayarla
        options.addArguments("--no-sandbox"); // Sandbox'ı devre dışı bırak (Linux ortamları için gerekebilir)
        options.addArguments("--disable-dev-shm-usage"); // /dev/shm kullanımı devre dışı bırak (Docker/Linux için)


        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10)); // Elementlerin yüklenmesini beklemek için bekleme süresi
    }

    // Her test metodundan önce çalışır (sayfayı yenilemek gibi)
    @BeforeEach
    void navigateToHome() {
        driver.get(BASE_URL);
        // Ana sayfa başlığını kontrol et
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.titleContains("Ürün Kataloğu"));
        assertEquals("Ürün Kataloğu", driver.getTitle(), "Ana sayfa başlığı doğru değil!");
    }

    @Test
    @Order(1) // Bu test ilk çalışsın
    @DisplayName("Ana Sayfaya Erişim ve Bağlantıları Kontrol Et")
    void testHomePageAccessAndLinks() {
        System.out.println("Test: Ana Sayfaya Erişim ve Bağlantıları Kontrol Et");
        // "Ürün Kataloğu Uygulamasına Hoş Geldiniz!" başlığını kontrol et
        WebElement welcomeHeader = driver.findElement(By.tagName("h1"));
        assertEquals("Ürün Kataloğu Uygulamasına Hoş Geldiniz!", welcomeHeader.getText(), "Hoş geldiniz başlığı bulunamadı veya yanlış!");

        // "Ürünleri Görüntüle" bağlantısını kontrol et
        WebElement viewProductsLink = driver.findElement(By.linkText("Ürünleri Görüntüle"));
        assertTrue(viewProductsLink.isDisplayed(), "Ürünleri Görüntüle bağlantısı görünmüyor!");
        assertEquals(BASE_URL + "/products", viewProductsLink.getAttribute("href"), "Ürünleri Görüntüle bağlantısı yanlış!");

        // "Yeni Ürün Ekle" bağlantısını kontrol et
        WebElement addNewProductLink = driver.findElement(By.linkText("Yeni Ürün Ekle"));
        assertTrue(addNewProductLink.isDisplayed(), "Yeni Ürün Ekle bağlantısı görünmüyor!");
        assertEquals(BASE_URL + "/products/new", addNewProductLink.getAttribute("href"), "Yeni Ürün Ekle bağlantısı yanlış!");
    }

    @Test
    @Order(2) // Bu test ikinci çalışsın
    @DisplayName("Ürün Listesi Sayfasına Erişim ve Liste Kontrolü")
    void testProductListPage() {
        System.out.println("Test: Ürün Listesi Sayfasına Erişim ve Liste Kontrolü");
        driver.findElement(By.linkText("Ürünleri Görüntüle")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/products"));
        wait.until(ExpectedConditions.titleContains("Ürün Listesi"));

        assertEquals("Ürün Listesi", driver.getTitle(), "Ürün listesi sayfası başlığı doğru değil!");

        // Tabloda en az bir ürün olup olmadığını kontrol et (başlangıçtaki örnek ürünler)
        WebElement productTable = driver.findElement(By.tagName("table"));
        assertTrue(productTable.isDisplayed(), "Ürün tablosu görünmüyor!");

        // Satır sayısını kontrol et (header hariç)
        int rowCount = driver.findElements(By.cssSelector("table tbody tr")).size();
        assertTrue(rowCount >= 3, "Beklenen sayıda ürün bulunamadı (en az 3)"); // Başlangıçta 3 ürün eklemiştik

        // "Yeni Ürün Ekle" bağlantısını kontrol et
        WebElement addNewProductLink = driver.findElement(By.xpath("//div[@class='add-product-link']/a"));
        assertTrue(addNewProductLink.isDisplayed(), "Ürün listesinde Yeni Ürün Ekle bağlantısı görünmüyor!");
    }

    @Test
    @Order(3) // Bu test üçüncü çalışsın
    @DisplayName("Yeni Ürün Ekleme Akışı")
    void testAddNewProductFlow() {
        System.out.println("Test: Yeni Ürün Ekleme Akışı");
        driver.findElement(By.linkText("Yeni Ürün Ekle")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/products/new"));
        wait.until(ExpectedConditions.titleContains("Yeni Ürün Ekle"));

        assertEquals("Yeni Ürün Ekle", driver.getTitle(), "Yeni Ürün Ekle sayfası başlığı doğru değil!");

        // Form elementlerini bul ve doldur
        WebElement nameField = driver.findElement(By.id("name"));
        WebElement descriptionField = driver.findElement(By.id("description"));
        WebElement priceField = driver.findElement(By.id("price"));
        WebElement saveButton = driver.findElement(By.cssSelector("button[type='submit']"));

        String testProductName = "Selenium Test Ürünü";
        String testProductDescription = "Bu ürün Selenium otomasyon testi ile eklendi.";
        String testProductPrice = "99.99";

        nameField.sendKeys(testProductName);
        descriptionField.sendKeys(testProductDescription);
        priceField.sendKeys(testProductPrice);

        saveButton.click();

        // Ürün eklendikten sonra ürün listesi sayfasına geri dönüldüğünü kontrol et
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/products"));
        wait.until(ExpectedConditions.titleContains("Ürün Listesi"));
        assertEquals("Ürün Listesi", driver.getTitle(), "Ürün eklendikten sonra ürün listesine dönülmedi!");

        // Eklenen ürünün listede olup olmadığını kontrol et
        WebElement productTable = driver.findElement(By.tagName("table"));
        WebElement addedProductRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[text()='" + testProductName + "']")));
        assertTrue(addedProductRow.isDisplayed(), "Eklenen 'Selenium Test Ürünü' listede bulunamadı!");

        System.out.println("Yeni ürün başarıyla eklendi ve listede bulundu: " + testProductName);
    }

    // Tüm testler bittikten sonra çalışır
    @AfterAll
    static void teardown() {
        if (driver != null) {
            driver.quit(); // Tarayıcıyı kapat ve WebDriver oturumunu sonlandır
            System.out.println("WebDriver kapatıldı.");
        }
    }
}