package io.seventytwo.demo;

import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.flow.theme.lumo.Lumo;
import org.openqa.selenium.chrome.ChromeDriver;

public class NotificationViewIT extends AbstractViewTest {

    @Before
    public void setup() {
        setDriver(new ChromeDriver());
        getDriver().get("http://localhost:8080/notification");
    }

    @Test
    public void clickingButtonShowsNotification() {
        Assert.assertFalse($(NotificationElement.class).exists());
        $(ButtonElement.class).first().click();
        Assert.assertTrue($(NotificationElement.class).waitForFirst().isOpen());
    }

    @Test
    public void clickingButtonTwiceShowsTwoNotifications() {
        Assert.assertFalse($(NotificationElement.class).exists());
        ButtonElement button = $(ButtonElement.class).first();
        button.click();
        button.click();
        Assert.assertEquals(2, $(NotificationElement.class).all().size());
    }

    @Test
    public void buttonIsUsingLumoTheme() {
        WebElement element = $(ButtonElement.class).first();
        assertThemePresentOnElement(element, Lumo.class);
    }

    @Test
    public void testClickButtonShowsHelloAnonymousUserNotificationWhenUserNameIsEmpty() {
        ButtonElement button = $(ButtonElement.class).first();
        button.click();
        Assert.assertTrue($(NotificationElement.class).exists());
        NotificationElement notification = $(NotificationElement.class).first();
        Assert.assertEquals("Hello anonymous user", notification.getText());
    }

    @Test
    public void testClickButtonShowsHelloUserNotificationWhenUserIsNotEmpty() {
        TextFieldElement textField = $(TextFieldElement.class).first();
        textField.setValue("DawsCon");
        ButtonElement button = $(ButtonElement.class).first();
        button.click();
        Assert.assertTrue($(NotificationElement.class).exists());
        NotificationElement notification = $(NotificationElement.class).first();
        Assert.assertEquals("Hello DawsCon", notification.getText());
    }

    @Test
    public void testEnterShowsHelloUserNotificationWhenUserIsNotEmpty() {
        TextFieldElement textField = $(TextFieldElement.class).first();
        textField.setValue("DawsCon");
        textField.sendKeys(Keys.ENTER);
        Assert.assertTrue($(NotificationElement.class).exists());
        NotificationElement notification = $(NotificationElement.class).first();
        Assert.assertEquals("Hello DawsCon", notification.getText());
    }
}
