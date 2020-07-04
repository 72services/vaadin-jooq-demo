package io.seventytwo.demo.ui;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route
public class HalloView extends VerticalLayout {

    public HalloView() {
        TextField textField = new TextField("Ihr Name");
        Label label = new Label();

        Button button = new Button("Gruss", e -> label.setText("Hallo, " + textField.getValue()));
        button.addClickShortcut(Key.ENTER);

        add(textField, label, button);
    }

}
