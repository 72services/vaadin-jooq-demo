package io.seventytwo.demo.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import io.seventytwo.demo.database.tables.records.VEmployeeRecord;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.OrderField;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

import static io.seventytwo.demo.database.tables.VEmployee.V_EMPLOYEE;

@Route
public class EmployeesView extends Div {

    private final DSLContext dsl;
    private final CallbackDataProvider<VEmployeeRecord, Void> dataProvider;

    public EmployeesView(DSLContext dsl, TransactionTemplate transactionTemplate) {
        this.dsl = dsl;

        Grid<VEmployeeRecord> employeeGrid = new Grid<>();
        employeeGrid.setColumnReorderingAllowed(true);
        employeeGrid.setMultiSort(true);

        dataProvider = new CallbackDataProvider<>(
                query -> dsl.selectFrom(V_EMPLOYEE).orderBy(createOrderBy(query)).offset(query.getOffset()).limit(query.getLimit()).fetchStream(),
                query -> dsl.fetchCount(V_EMPLOYEE),
                VEmployeeRecord::getEmployeeId);

        employeeGrid.setDataProvider(dataProvider);

        employeeGrid.addColumn(VEmployeeRecord::getEmployeeId).setHeader("ID").setSortProperty(V_EMPLOYEE.EMPLOYEE_ID.getName());
        employeeGrid.addColumn(VEmployeeRecord::getEmployeeName).setHeader("Name").setSortProperty(V_EMPLOYEE.EMPLOYEE_NAME.getName());
        employeeGrid.addColumn(VEmployeeRecord::getDepartmentName).setHeader("Department").setSortProperty(V_EMPLOYEE.DEPARTMENT_NAME.getName());

        employeeGrid.addColumn(new ComponentRenderer<>(vEmployeeRecord ->
                new Button("Edit",
                        buttonClickEvent -> {
                            EmployeeDialog dialog = new EmployeeDialog(dsl, transactionTemplate);
                            dialog.open(vEmployeeRecord.getEmployeeId(), () -> refreshEmployee(vEmployeeRecord));
                        })))
                .setFrozen(true);

        Button addButton = new Button("Add Employee", buttonClickEvent -> {
            EmployeeDialog dialog = new EmployeeDialog(dsl, transactionTemplate);
            dialog.open(null, () -> dataProvider.refreshAll());
        });

        add(employeeGrid, addButton);
    }

    private void refreshEmployee(VEmployeeRecord vEmployeeRecord) {
        VEmployeeRecord reloadedEmployee = dsl
                .selectFrom(V_EMPLOYEE)
                .where(V_EMPLOYEE.EMPLOYEE_ID.eq(vEmployeeRecord.getEmployeeId()))
                .fetchOne();
        dataProvider.refreshItem(reloadedEmployee);
    }

    private List<OrderField<?>> createOrderBy(Query<VEmployeeRecord, Void> query) {
        List<OrderField<?>> orderFields = new ArrayList<>();
        for (QuerySortOrder sortOrder : query.getSortOrders()) {
            Field<?> field = V_EMPLOYEE.field(sortOrder.getSorted());
            orderFields.add(sortOrder.getDirection().equals(SortDirection.DESCENDING) ? field.desc() : field);
        }
        return orderFields;
    }
}
