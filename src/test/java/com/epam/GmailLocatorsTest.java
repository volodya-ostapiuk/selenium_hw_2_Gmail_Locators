package com.epam;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GmailLocatorsTest {
    private WebDriver webDriver;
    private static final String DRIVER_NAME = "webdriver.chrome.driver";
    private static final String DRIVER_PATH = "src/main/resources/chromedriver.exe";
    private static final String BASE_URL = "https://mail.google.com";
    private static final int TIME_WAIT = 30;
    private static final int MINIMUM = 0;
    private static final String TEST_EMAIL = "test.automv";
    private static final String TEST_PASSWORD = "testaut50";
    private static final String RECEIVER_EMAIL = "ostap.volodya@gmail.com";
    private static final String LETTER_TOPIC = "Selenium test message";
    private static final String LETTER_TEXT = "Generated number = " +
            new Random().nextInt(Integer.MAX_VALUE - MINIMUM) + MINIMUM;

    @BeforeClass
    private void setUp() {
        System.setProperty(DRIVER_NAME, DRIVER_PATH);
        webDriver = new ChromeDriver();
        webDriver.manage()
                .timeouts()
                .implicitlyWait(TIME_WAIT, TimeUnit.SECONDS);
        webDriver.get(BASE_URL);
    }

    @Test
    private void verifyLogin() {
        verifyValidEmail();
        verifyPassword();
    }

    /*
     * Enter email, check does chosen profile link contains email
     */
    private void verifyValidEmail() {
        WebElement emailInput = webDriver.findElement(By.id("identifierId"));
        emailInput.sendKeys(TEST_EMAIL);
        emailInput.sendKeys(Keys.ENTER);
        WebElement chosenProfileLink = webDriver.findElement(By.cssSelector("div.aCayab > div"));
        Assert.assertTrue(chosenProfileLink.getAttribute("aria-label")
                .contains(TEST_EMAIL.toLowerCase()), "Not valid email.");
    }

    /*
     * Enter password. Wait until new page will be opened and element will be clickable on it
     * Check does page title contains email
     */
    private void verifyPassword() {
        WebElement passwordInput = (new WebDriverWait(webDriver, TIME_WAIT))
                .until(ExpectedConditions.elementToBeClickable(By.name("password")));
        passwordInput.sendKeys(TEST_PASSWORD);
        passwordInput.sendKeys(Keys.ENTER);
        (new WebDriverWait(webDriver, TIME_WAIT))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@class=\"T-I T-I-KE L3\"]")));
        Assert.assertTrue(webDriver.getTitle().toLowerCase()
                .contains(TEST_EMAIL.toLowerCase()), "Wrong password.");
    }

    /*
     * Create new letter
     * Wait until link in pop-up message about letter status will be clickable
     * Check if last sent letter contains needed email, text and topic
     */
    @Test(dependsOnMethods = "verifyLogin")
    private void verifyLetterSent() {
        createLetter();
        (new WebDriverWait(webDriver, TIME_WAIT))
                .until(ExpectedConditions.elementToBeClickable(By.id("link_vsm")));
        WebElement sentLettersFolder = webDriver.findElement(By.xpath("//*[@class='TK']/div[4]//a[@class='J-Ke n0']"));
        sentLettersFolder.click();
        WebElement lastSentLetter = webDriver.findElement(By.cssSelector(".ae4.UI tr:nth-child(1)"));
        lastSentLetter.click();
        WebElement lastSentTopic = webDriver.findElement(By.className("hP"));
        Assert.assertTrue(lastSentTopic.getText().contains(LETTER_TOPIC),
                "Last sent letter doesn't contain sent letter topic.");
        WebElement lastSentReceiver = webDriver.findElement(By.cssSelector(".hb > span"));
        Assert.assertTrue(lastSentReceiver.getAttribute("email").contains(RECEIVER_EMAIL),
                "Last sent letter doesn't contain sent letter receiver.");
        WebElement lastSentText = webDriver.findElement(By.xpath("//div[@class='ii gt']//*[@dir='ltr']"));
        Assert.assertTrue(lastSentText.getText().contains(LETTER_TEXT),
                "Last sent letter doesn't contain sent letter text.");
    }

    private void createLetter() {
        WebElement composeButton = webDriver.findElement(By.xpath("//*[@class=\"T-I T-I-KE L3\"]"));
        composeButton.click();
        WebElement sentToField = (new WebDriverWait(webDriver, TIME_WAIT))
                .until(ExpectedConditions.elementToBeClickable(By.name("to")));
        sentToField.sendKeys(RECEIVER_EMAIL);
        WebElement topicField = webDriver.findElement(By.name("subjectbox"));
        topicField.sendKeys(LETTER_TOPIC);
        WebElement letterTextField = webDriver.findElement(By.xpath("//*[@class=\"Am Al editable LW-avf tS-tW\"]"));
        letterTextField.sendKeys(LETTER_TEXT);
        WebElement sendButton = webDriver.findElement(By.cssSelector(".T-I.J-J5-Ji.aoO.v7.T-I-atl.L3"));
        sendButton.click();
    }

    @AfterClass
    private void quitDriver() {
        webDriver.quit();
    }
}
