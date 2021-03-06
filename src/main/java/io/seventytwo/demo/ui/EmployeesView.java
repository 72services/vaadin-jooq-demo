package io.seventytwo.demo.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import io.seventytwo.demo.database.tables.records.VEmployeeRecord;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.transaction.support.TransactionTemplate;

import static io.seventytwo.demo.database.tables.VEmployee.V_EMPLOYEE;
import static io.seventytwo.vaadinjooq.util.VaadinJooqUtil.orderFields;
import static org.jooq.impl.DSL.upper;

@Route
@PageTitle("Employees")
public class EmployeesView extends Div {

    private final DSLContext dsl;
    private final ConfigurableFilterDataProvider<VEmployeeRecord, Void, Condition> dataProvider;

    public EmployeesView(DSLContext dsl, TransactionTemplate transactionTemplate) {
        this.dsl = dsl;

        addClassName("centered-content");

        Grid<VEmployeeRecord> employeeGrid = new Grid<>();
        employeeGrid.setColumnReorderingAllowed(true);
        employeeGrid.setMultiSort(true);

        employeeGrid.addColumn(VEmployeeRecord::getEmployeeId)
                .setHeader("ID").setSortProperty(V_EMPLOYEE.EMPLOYEE_ID.getName());
        employeeGrid.addColumn(VEmployeeRecord::getEmployeeName)
                .setHeader("Name").setSortProperty(V_EMPLOYEE.EMPLOYEE_NAME.getName());
        employeeGrid.addColumn(VEmployeeRecord::getDepartmentName)
                .setHeader("Department").setSortProperty(V_EMPLOYEE.DEPARTMENT_NAME.getName());

        employeeGrid.addColumn(new ComponentRenderer<>(vEmployeeRecord ->
                new Button("Edit",
                        buttonClickEvent -> {
                            EmployeeDialog dialog = new EmployeeDialog(dsl, transactionTemplate);
                            dialog.open(vEmployeeRecord.getEmployeeId(), () -> refreshEmployee(vEmployeeRecord));
                        })))
                .setFrozen(true);

        dataProvider = new CallbackDataProvider<VEmployeeRecord, Condition>(
                query -> dsl
                        .selectFrom(V_EMPLOYEE)
                        .where(query.getFilter().orElse(DSL.noCondition()))
                        .orderBy(orderFields(V_EMPLOYEE, query))
                        .offset(query.getOffset())
                        .limit(query.getLimit())
                        .fetchStream(),
                query -> dsl
                        .selectCount()
                        .from(V_EMPLOYEE)
                        .where(query.getFilter().orElse(DSL.noCondition()))
                        .fetchOneInto(Integer.class),
                VEmployeeRecord::getEmployeeId)
                .withConfigurableFilter();

        employeeGrid.setDataProvider(dataProvider);

        TextField filter = new TextField("Filter");
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(event -> {
            if (StringUtils.isNotBlank(event.getValue())) {
                dataProvider.setFilter(
                        upper(V_EMPLOYEE.EMPLOYEE_NAME).like("%" + event.getValue().toUpperCase() + "%"));
            } else {
                dataProvider.setFilter(null);
            }
        });

        Button addButton = new Button("Add Employee", buttonClickEvent -> {
            EmployeeDialog dialog = new EmployeeDialog(dsl, transactionTemplate);
            dialog.open(null, dataProvider::refreshAll);
        });

        add(filter, employeeGrid, addButton);
    }

    private void refreshEmployee(VEmployeeRecord vEmployeeRecord) {
        VEmployeeRecord reloadedEmployee = dsl
                .selectFrom(V_EMPLOYEE)
                .where(V_EMPLOYEE.EMPLOYEE_ID.eq(vEmployeeRecord.getEmployeeId()))
                .fetchOne();
        dataProvider.refreshItem(reloadedEmployee);
    }

}
