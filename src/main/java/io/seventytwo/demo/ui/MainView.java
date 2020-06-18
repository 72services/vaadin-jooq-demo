package io.seventytwo.demo.ui;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.Router;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import io.seventytwo.demo.service.GreetService;

/**
 * Use the @PWA annotation make the application installable on phones, tablets and some desktop browsers.
 */
@Route
@PWA(name = "jug.ch Application", shortName = "jug.ch App", description = "This is an example Vaadin application")
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends VerticalLayout {

    public MainView() {
        add(new RouterLink("Hello", HelloView.class));
        add(new RouterLink("Employees", EmployeesView.class));
    }

}
