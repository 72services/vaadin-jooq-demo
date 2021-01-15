package io.seventytwo.demo.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import org.springframework.core.task.TaskExecutor;

/**
 * Use the @PWA annotation make the application installable on phones, tablets and some desktop browsers.
 */
@Push
@Route
@PageTitle("Demo Application")
@PWA(name = "Demo Application", shortName = "Demo", description = "This is a Vaadin Demo Application")
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends VerticalLayout {

    private final TaskExecutor taskExecutor;

    public MainView(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;

        addClassName("centered-content");

        add(new RouterLink("Hello", HelloView.class));
        add(new RouterLink("Notification", NotificationView.class));
        add(new RouterLink("Employees", EmployeesView.class));

        Button asyncButton = new Button("Async Execution");
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
