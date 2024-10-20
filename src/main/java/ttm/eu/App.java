package ttm.eu;

import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@SpringBootApplication
public class App {

    public static void main( String[] args ) { SpringApplication.run( App.class, args );}

    @Bean
    public CommandLineRunner execute() {
        return args -> {
            //------------------ WAY 1 --------------------
            Set<Link> visitedLinks = new HashSet<>();
            Set<Link> nonDiscoveredLinks = new HashSet<>();
            List<String[]> datalist = new ArrayList<>(); // contains doubled
            String url = "https://google.com/"; // "https://mkyong.com/";
            new WebCrawlerWithDepth().getPageLinks(url, 0, visitedLinks, nonDiscoveredLinks, datalist);
            Set<Link> linkToIndex = Stream.concat(visitedLinks.stream(), nonDiscoveredLinks.stream().filter(link -> link.getStatus() != 500)).collect(Collectors.toSet());
            Util.saveToCsv(datalist, "frontier1");
            // index into status:  both links list from frontier(csv) to index-status


            //------------------ WAY 2 --------------------
            Set<String> visitedUrls = new HashSet<>();
            Set<String> allVisitedPages = new HashSet<>();
            List<String[]> datalist2 = new ArrayList<>(); // contains doubled
            new WebCrawler().crawl(url, visitedUrls, allVisitedPages, datalist2);
            // index into status:  both links list from frontier(csv) to index-status

            //---------------------
            // start scraping
            // start indexing
            System.out.println("end");


            ChromeOptions chromeOptions = new ChromeOptions();
            // chromeOptions.addArguments("--no-sandbox"); // Bypass OS security model, MUST BE THE VERY FIRST OPTION
            // chromeOptions.addArguments("--headless");
            // chromeOptions.addArguments("--no-sandbox", "--headless=new", "--disable-web-security", "--allow-running-insecure-content", "--blink-settings=imagesEnabled=false", "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36", "--disable-software-rasterizer", "--disable-blink-features", "--window-size=1200,730", "--disable-gpu", "--disable-notifications", "--disable-extensions", "--ignore-certificate-errors", "--remote-allow-origins=*", "--no-sandbox", "--disable-dev-shm-usage", "--port=" + curPort);
            chromeOptions.addArguments("--no-sandbox", "--headless=new", "--disable-web-security", "--allow-running-insecure-content", "--blink-settings=imagesEnabled=false", "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36", "--disable-software-rasterizer", "--disable-blink-features", "--disable-gpu", "--disable-notifications", "--disable-extensions", "--ignore-certificate-errors", "--remote-allow-origins=*", "--disable-dev-shm-usage");
            chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);
            chromeOptions.setExperimentalOption("useAutomationExtension", false);
            chromeOptions.setAcceptInsecureCerts(true);
            chromeOptions.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.DISMISS_AND_NOTIFY);

            WebDriver driver = new ChromeDriver(chromeOptions);

            // connecting to the target web page
            driver.get(url);
            log.info("title: {} - url: {}",driver.getTitle(), driver.getCurrentUrl());

            //*********Web Elements By Using Page Factory*********
            //@FindBy(how = How.CLASS_NAME, using = "btnSignIn")
            //WebElement signInButton;

            // Try to get tilte:
            By title = By.tagName("title");
            WebElement elementTitle = driver.findElement(title);
            log.info("title: {} - link: {}",elementTitle.getText(), driver.getCurrentUrl());

            // Try to get tilte:
            By title2 = By.cssSelector("title, meta[name='title'], meta[name='og:title'], h1, h2");
            List<WebElement> titleElements = driver.findElements(title2);
            for (WebElement titleElement : titleElements){
                log.info("Text: {} - link: {}", titleElement.getText(), titleElement.getAttribute("abs:href"));
            }

            // try to get all link
            By by = By.cssSelector("a[href]");
            List<WebElement> elements = driver.findElements(by);
            for (WebElement webElement : elements){
                log.info("Text: {} - link: {}",webElement.getText(), webElement.getAttribute("abs:href"));
            }

            driver.quit();
        };
    }
}
