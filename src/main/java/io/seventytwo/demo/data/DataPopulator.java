package io.seventytwo.demo.data;

import org.jooq.DSLContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static io.seventytwo.demo.database.tables.Department.DEPARTMENT;
import static io.seventytwo.demo.database.tables.Employee.EMPLOYEE;

@Component
public class DataPopulator implements CommandLineRunner {

    private final DSLContext dslContext;

    public DataPopulator(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    @Transactional
    public void run(String... args) {
        dslContext.insertInto(DEPARTMENT).columns(DEPARTMENT.ID, DEPARTMENT.NAME).values(1, "IT").execute();
        dslContext.insertInto(DEPARTMENT).columns(DEPARTMENT.ID, DEPARTMENT.NAME).values(2, "HR").execute();

        dslContext.insertInto(EMPLOYEE)
                .columns(EMPLOYEE.ID, EMPLOYEE.NAME, EMPLOYEE.DATE_OF_BIRTH, EMPLOYEE.DEPARTMENT_ID)
                .values(1, "Anna Muster", LocalDate.of(1981, 6, 21), 1)
                .execute();
        dslContext.insertInto(EMPLOYEE)
                .columns(EMPLOYEE.ID, EMPLOYEE.NAME, EMPLOYEE.DATE_OF_BIRTH, EMPLOYEE.DEPARTMENT_ID)
                .values(2, "Max Meier", LocalDate.of(1960, 11, 1), 1)
                .execute();
        dslContext.insertInto(EMPLOYEE)
                .columns(EMPLOYEE.ID, EMPLOYEE.NAME, EMPLOYEE.DATE_OF_BIRTH, EMPLOYEE.DEPARTMENT_ID)
                .values(3, "Kasimir Hofer", LocalDate.of(1970, 1, 3), 2)
                .execute();
    }
}
