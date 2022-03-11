package com.qa.baseTest;

import com.aventstack.extentreports.Status;
import com.qa.reports.ExtentReport;
import com.qa.utils.TestUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class BaseTest {

    protected static AppiumDriver driver;
    protected static Properties prop;
    private static AppiumDriverLocalService server;
    InputStream inputstream;
    TestUtils util = new TestUtils();

    public BaseTest() {

        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    @BeforeClass
    @Parameters({"platformName", "deviceName", "udid"})
    public void initialSetup(String platformName, String deviceName, String udid) {
        try {

            util.log().info("Initial set up has been started ...");
            prop = new Properties();
            String propFileName = "config.properties";
            inputstream = getClass().getClassLoader().getResourceAsStream(propFileName);
            prop.load(inputstream);
            util.log().info(propFileName + "loaded sucessfully...");

            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability(MobileCapabilityType.PLATFORM_NAME, platformName);
            util.log().info("Mobile automation platform used : " + platformName);

            caps.setCapability("newCommandTime", 300);
            caps.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
            caps.setCapability(MobileCapabilityType.AUTOMATION_NAME, prop.getProperty("androidAutomationName"));
            caps.setCapability(MobileCapabilityType.UDID, udid);
            caps.setCapability("avd", "Anubhav_Pixel_1");
            caps.setCapability("avdLaunchTimeOut", 180000);
            caps.setCapability("newCommandTimeOUt", 300);
            String appUrl = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" +
                    File.separator + "resources" + File.separator + "app" + File.separator + "app-release.apk";
            caps.setCapability(MobileCapabilityType.APP, appUrl);
           // caps.setCapability("appPackage", prop.getProperty("androidAppPackage"));
          // caps.setCapability("appActivity", prop.getProperty("androidAppActivity"));

            URL url = new URL(prop.getProperty("appiumURL"));
            driver = new AppiumDriver(url, caps);

            System.out.println("session id :" + driver.getSessionId());
            if (driver.getSessionId() != null) {
                util.log().info("Appium driver has been initialized sucessfully");
                util.log().info("Appirum driver session id is " + driver.getSessionId());
            }


            driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
            util.log().info("implicitly time out wait set to 20 second");
        } catch (Exception e) {
            e.printStackTrace();
            util.log().fatal("Appium Driver not initialized");
        }
    }


   /* @BeforeSuite
    public void beforeSuite() {

//        server = getAppiumServerDefault();
//        server.start();
//        server.clearOutPutStreams();
        util.log().info("server started successfully");
    }*/

    @AfterClass

        public void tearDown(){
            driver.quit();
        }


    public AppiumDriverLocalService getAppiumServerDefault() {
        return AppiumDriverLocalService.buildDefaultService();
    }


    public AppiumDriver getDriver() {
        return driver;
    }


    public void waitForVisibility(MobileElement ele) {
        WebDriverWait wait = new WebDriverWait(driver, 50);
        wait.until(ExpectedConditions.visibilityOf(ele));
    }

    public void clickOnElement(MobileElement ele) {
        waitForVisibility(ele);
        ele.click();
    }

    public boolean clickOnElement(MobileElement ele, String msg) {
        try {
            waitForVisibility(ele);
            ele.click();
            util.log().info(msg);
            ExtentReport.getTest().log(Status.PASS, msg);
            return true;
        } catch (Exception e) {
            util.log().error(msg + "is failed.");
            ExtentReport.getTest().log(Status.FAIL, msg);
            return false;
        }

    }


    public void enterTxtOnElement(MobileElement ele,String txt){
        waitForVisibility(ele);
        ele.sendKeys(txt);
    }

    public void enterTxtOnElement(MobileElement ele,String txt,String msg){
        waitForVisibility(ele);

        ele.click();
      //  ele.setValue(txt);
      ele.sendKeys(txt);
        util.log().info(msg+ ": "+txt);
        ExtentReport.getTest().log(Status.PASS, msg+ " : "+txt);


    }

    public String getText(MobileElement ele){
        String txt = null;
        waitForVisibility(ele);
        txt = ele.getAttribute("text");
        return txt;
    }

    public String getText(MobileElement ele, String msg) {
        String txt = null;
        waitForVisibility(ele);
        txt = ele.getAttribute("text");
        if(txt!=null){
            util.log().info(msg + txt);
            ExtentReport.getTest().log(Status.PASS, msg +"  "+ txt); }
         else{
            util.log().info(msg + txt);
            ExtentReport.getTest().log(Status.FAIL, msg +"  "+ txt);
        }
        return txt;

        }




    public void closeApp() {
        ((InteractsWithApps) getDriver()).closeApp();
    }

    public void launchApp() {
        ((InteractsWithApps) getDriver()).launchApp();
    }


   /* @AfterTest()
   // public void tearDown(){
        driver.quit();
    }
*/


}
