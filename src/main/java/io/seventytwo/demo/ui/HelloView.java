package io.seventytwo.demo.ui;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route
@PageTitle("Hallo")
public class HelloView extends VerticalLayout {

    public HelloView() {
        TextField textField = new TextField("Your Name");
        Label label = new Label();

        Button button = new Button("Greet", e -> label.setText("Hello, " + textField.getValue()));
        button.addClickShortcut(Key.ENTER);

        add(textField, label, button);
    }

}
