package io.seventytwo.demo.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import org.springframework.core.task.TaskExecutor;

/**
 * Use the @PWA annotation make the application installable on phones, tablets and some desktop browsers.
 */
@Push
@Route
@PWA(name = "jug.ch Application", shortName = "jug.ch App", description = "This is an example Vaadin application")
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends VerticalLayout {

    private final TaskExecutor taskExecutor;

    public MainView(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
        add(new RouterLink("Hello", HelloView.class));
        add(new RouterLink("Employees", EmployeesView.class));

        Button asyncButton = new Button("Async");
        asyncButton.addClickListener(event ->
                this.taskExecutor.execute(() -> {
                    try {
                        Thread.sleep(3000);

                        event.getSource().getUI().ifPresent(ui -> ui.access(() -> Notification.show("Hello from Thread")));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }));

        add(asyncButton);
    }

}
