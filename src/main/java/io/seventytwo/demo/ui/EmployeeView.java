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
import io.seventytwo.demo.database.tables.VEmployee;
import io.seventytwo.demo.database.tables.records.VEmployeeRecord;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.OrderField;

import java.util.ArrayList;
import java.util.List;

import static io.seventytwo.demo.database.tables.VEmployee.V_EMPLOYEE;

@Route
public class EmployeeView extends Div {

    public EmployeeView(DSLContext dslContext) {
        Grid<VEmployeeRecord> employeeGrid = new Grid<>();
        employeeGrid.setColumnReorderingAllowed(true);
        employeeGrid.setMultiSort(true);

        employeeGrid.addColumn(VEmployeeRecord::getEmployeeId).setHeader("ID").setSortable(true).setSortProperty(V_EMPLOYEE.EMPLOYEE_ID.getName());
        employeeGrid.addColumn(VEmployeeRecord::getEmployeeName).setHeader("Name").setSortable(true).setSortProperty(V_EMPLOYEE.EMPLOYEE_NAME.getName());
        employeeGrid.addColumn(VEmployeeRecord::getDepartmentName).setHeader("Department").setSortable(true).setSortProperty(V_EMPLOYEE.DEPARTMENT_NAME.getName());
        employeeGrid.addColumn(new ComponentRenderer<>(vEmployeeRecord -> new Button("Edit"))).setFrozen(true);

        CallbackDataProvider<VEmployeeRecord, Void> dataProvider = DataProvider.fromCallbacks(
                query -> dslContext.selectFrom(V_EMPLOYEE).orderBy(createOrderBy(query)).offset(query.getOffset()).limit(query.getLimit()).fetchStream(),
                query -> dslContext.fetchCount(V_EMPLOYEE));

        employeeGrid.setDataProvider(dataProvider);

        Button refreshButton = new Button("Refresh", buttonClickEvent -> dataProvider.refreshAll());

        add(employeeGrid, refreshButton);
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
