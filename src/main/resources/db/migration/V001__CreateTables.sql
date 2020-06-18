create table department
(
    id   int primary key auto_increment,
    name varchar(255) not null
);

create table employee
(
    id            int primary key auto_increment,
    name          varchar(255) not null,
    date_of_birth date not null,
    department_id int          not null,

    foreign key (department_id) references department (id)
);

create view v_employee as
    select e.id as employee_id, e.name as employee_name, d.name as department_name
    from employee e join department d on e.department_id = d.id;
