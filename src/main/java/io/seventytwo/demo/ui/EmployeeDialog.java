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
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import io.seventytwo.demo.database.tables.records.DepartmentRecord;
import io.seventytwo.demo.database.tables.records.EmployeeRecord;
import org.jooq.DSLContext;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.seventytwo.demo.database.tables.Department.DEPARTMENT;
import static io.seventytwo.demo.database.tables.Employee.EMPLOYEE;

public class EmployeeDialog extends Dialog {

    private final DSLContext dsl;
    private final Binder<EmployeeRecord> binder;
    private EmployeeRecord employeeRecord;
    private Runnable onClose;
    private Map<Integer, DepartmentRecord> departmentMap;

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

        binder.forField(name)
                .asRequired()
                .withValidator(s -> s.length() >=3, "Name must be at least 3 characters")
                .bind(EmployeeRecord::getName, EmployeeRecord::setName);

        DatePicker dateOfBirth = new DatePicker("Date of Birth");
        dateOfBirth.setRequiredIndicatorVisible(true);
        formLayout.add(dateOfBirth);

        binder.forField(dateOfBirth).asRequired().bind(EmployeeRecord::getDateOfBirth, EmployeeRecord::setDateOfBirth);

        Select<DepartmentRecord> department = new Select<>();
        department.setLabel("Department");
        department.setRequiredIndicatorVisible(true);
        department.setRequiredIndicatorVisible(true);
        department.setItemLabelGenerator(DepartmentRecord::getName);
        department.setItems(loadDepartments());

        binder.forField(department)
                .withConverter(new Converter<DepartmentRecord, Integer>() {
                    @Override
                    public Result<Integer> convertToModel(DepartmentRecord value, ValueContext context) {
                        return Result.ok(value == null ? null : value.getId());
                    }

                    @Override
                    public DepartmentRecord convertToPresentation(Integer value, ValueContext context) {
                        return departmentMap.get(value);
                    }
                })
                .asRequired()
                .bind(EmployeeRecord::getDepartmentId, EmployeeRecord::setDepartmentId);

        formLayout.add(department);

        Button save = new Button("Save", buttonClickEvent ->
                transactionTemplate.executeWithoutResult(transactionStatus -> {
                    if (binder.validate().isOk()) {
                        dsl.attach(employeeRecord);
                        employeeRecord.store();
                        close();
                    }
                }));

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancel = new Button("Cancel", buttonClickEvent -> close());
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        add(new HorizontalLayout(save, cancel));
    }

    private List<DepartmentRecord> loadDepartments() {
        List<DepartmentRecord> departments = dsl.selectFrom(DEPARTMENT).orderBy(DEPARTMENT.NAME).fetch();
        departmentMap = departments.stream().collect(Collectors.toMap(DepartmentRecord::getId, departmentRecord -> departmentRecord));
        return departments;
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
