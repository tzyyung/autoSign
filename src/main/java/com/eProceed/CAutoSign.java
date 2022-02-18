package com.eProceed;

import org.apache.commons.cli.*;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Arrays;

/**
 * Created by Anson on 2017/8/1.
 */
public class CAutoSign {

    public void checkAlert(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 2);
            wait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            System.out.println(alert.getText());
            alert.accept();
        } catch (Exception e) {
            //exception handling
        }
    }

    private void waitElement(final WebDriver driver, final By by) {
        (new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                WebElement img = d.findElement(by);
                return img != null;
            }
        });
    }

    public static final String CHECK_IN = "icon22_04.png";
    public static final String CHECK_OUT = "icon22_05.png";

    public void run(String account, String password, String action) {
        //WebDriver driver = new FirefoxDriver();
        //driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        DesiredCapabilities capability = DesiredCapabilities.chrome();
        //System.setProperty("webdriver.chrome.driver","C:/chromedriver.exe --whitelisted-ips=\"\"");
        capability.setCapability("chrome.switches", Arrays.asList("–disable-extensions,"));
        //capability.setCapability("chrome.binary","C:/Users/user_name/AppData/Local/Google/Chrome/Application/chrome.exe");
        //ChromeOptions options = new ChromeOptions();
        //options.addArguments("user-data-dir=C:/Users/user_name/AppData/Local/Google/Chrome/User Data/Default");
        WebDriver driver = new ChromeDriver(capability);

        //進入首頁
        driver.get("http://eip.incrte.com/UOF/");

        //等待 網頁 - 帳密文字框 載入
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("txtAccount")));


        //輸入帳密
        driver.findElement(By.id("txtAccount")).sendKeys(account);
        driver.findElement(By.id("txtPwd")).sendKeys(password);
        driver.findElement(By.id("btnSubmit")).click();

        if (CHECK_IN.equals(action) || CHECK_OUT.equals(action)) {
            //切換至 frame1
            WebElement frame1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("Frame1")));
            //WebElement frame1= driver.findElement(By.id("Frame1"));
            driver.switchTo().frame(frame1);

            //等待 簽退 img 出現
            WebElement img = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//img[contains(@src, '" + action + "')]")));
            //點選 簽退 img
            //WebElement img = driver.findElement(By.xpath("//img[contains(@src, 'icon22_05.png')]"));
            img.click();

            //點選 alert - 自動確定
            checkAlert(driver);
        }

        //關閉browser
        //driver.quit();
        //driver.close();
    }

    /**
     * 自動簽到/簽退程式 -a yyyyxxx -p your_password -o
     * -o 簽退
     * -i 簽到
     * <p/>
     * 需安裝 firefox 或使用 chromedriver.exe
     */
    public static void main(String args[]) {
        Options options = new Options();
        Option optAccount = new Option("a", "account", true, "輸入帳號");
        optAccount.setRequired(true);
        options.addOption(optAccount);

        Option optPassword = new Option("p", "password", true, "輸入密碼");
        optPassword.setRequired(true);
        options.addOption(optPassword);

        Option optLogin = new Option("l", "login", false, "登入");
        optLogin.setRequired(false);
        options.addOption(optLogin);

        Option optIn = new Option("i", "in", false, "簽到");
        optIn.setRequired(false);
        options.addOption(optIn);

        Option optOut = new Option("o", "out", false, "簽退");
        optOut.setRequired(false);
        options.addOption(optOut);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
            return;
        }

        String txtAccount = cmd.getOptionValue("account");
        String txtPassword = cmd.getOptionValue("password");
        String txtTarget = "login";

        if (cmd.hasOption("in")) {
            txtTarget = CHECK_IN;
        } else if (cmd.hasOption("out")) {
            txtTarget = CHECK_OUT;
        }
        new CAutoSign().run(txtAccount, txtPassword, txtTarget);
    }
}
