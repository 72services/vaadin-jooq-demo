package io.seventytwo.demo.data;

import com.github.javafaker.Faker;
import org.jooq.DSLContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

        Faker faker = new Faker();

        for (int i = 1; i < 101; i++) {
            dslContext.insertInto(EMPLOYEE)
                    .columns(EMPLOYEE.ID, EMPLOYEE.NAME, EMPLOYEE.DATE_OF_BIRTH, EMPLOYEE.DEPARTMENT_ID)
                    .values(i, faker.name().fullName(), new java.sql.Date(faker.date().birthday().getTime()).toLocalDate(), 1)
                    .execute();
        }

        for (int i = 101; i < 111; i++) {
            dslContext.insertInto(EMPLOYEE)
                    .columns(EMPLOYEE.ID, EMPLOYEE.NAME, EMPLOYEE.DATE_OF_BIRTH, EMPLOYEE.DEPARTMENT_ID)
                    .values(i, faker.name().fullName(), new java.sql.Date(faker.date().birthday().getTime()).toLocalDate(), 2)
                    .execute();
        }
    }
}
