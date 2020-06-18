package io.seventytwo.demo.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import io.seventytwo.demo.database.tables.records.DepartmentRecord;
import io.seventytwo.demo.database.tables.records.EmployeeRecord;
import org.jooq.DSLContext;
import org.springframework.transaction.support.TransactionTemplate;

import static io.seventytwo.demo.database.tables.Department.DEPARTMENT;
import static io.seventytwo.demo.database.tables.Employee.EMPLOYEE;

public class EmployeeDialog extends Dialog {

    private final DSLContext dsl;
    private final Binder<EmployeeRecord> binder;
    private EmployeeRecord employeeRecord;
    private Runnable onClose;

    public EmployeeDialog(DSLContext dsl, TransactionTemplate transactionTemplate) {
        this.dsl = dsl;

        setWidth("800px");
        setHeight("600px");

        binder = new Binder<>();

        FormLayout formLayout = new FormLayout();
        add(formLayout);

        TextField id = new TextField("ID");
        formLayout.add(id);

        binder.forField(id)
                .withConverter(new StringToIntegerConverter("ID must not be null"))
                .bind(EmployeeRecord::getId, null);


        TextField name = new TextField("Name");
        name.setRequiredIndicatorVisible(true);
        formLayout.add(name);

        binder.forField(name).bind(EmployeeRecord::getName, EmployeeRecord::setName);

        DatePicker dateOfBirth = new DatePicker("Date of Birth");
        dateOfBirth.setRequiredIndicatorVisible(true);
        formLayout.add(dateOfBirth);

        binder.forField(dateOfBirth).bind(EmployeeRecord::getDateOfBirth, EmployeeRecord::setDateOfBirth);

        Select<DepartmentRecord> department = new Select<>();
        department.setLabel("Department");
        department.setRequiredIndicatorVisible(true);
        department.setItemLabelGenerator(DepartmentRecord::getName);
        department.setItems(dsl.selectFrom(DEPARTMENT).orderBy(DEPARTMENT.NAME).fetch());

        formLayout.add(department);

        Button save = new Button("Save", buttonClickEvent ->
                transactionTemplate.executeWithoutResult(transactionStatus -> {
                    dsl.attach(employeeRecord);
                    employeeRecord.store();
                    close();
                }));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancel = new Button("Cancel", buttonClickEvent -> close());
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        add(new HorizontalLayout(save, cancel));
    }

    public void open(Integer employeeId, Runnable onClose) {
        super.open();

        this.onClose = onClose;

        if (employeeId == null) {
            employeeRecord = EMPLOYEE.newRecord();
        } else {
            employeeRecord = dsl.selectFrom(EMPLOYEE).where(EMPLOYEE.ID.eq(employeeId)).fetchOne();
        }
        binder.setBean(employeeRecord);
    }

    @Override
    public void close() {
        super.close();

        onClose.run();
    }
}
