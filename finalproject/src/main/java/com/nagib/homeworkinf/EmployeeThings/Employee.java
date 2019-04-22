package com.nagib.homeworkinf.EmployeeThings;

import java.io.Serializable;

public class Employee implements Serializable{
    int id;
    String name;
    String post;
    int age;
    int salary;
    int infoId;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPost() {
        return post;
    }

    public int getAge() {
        return age;
    }

    public int getSalary() {
        return salary;
    }

    public int getInfoId() {
        return infoId;
    }

    public Employee(int id, String name, String post, int age, int salary, int infoId) {
        this.id = id;
        this.name = name;
        this.post = post;
        this.age = age;
        this.salary = salary;
        this.infoId = infoId;
    }

    @Override
    public String toString() {
        return "Employee: name=" + name +
                ", post=" + post +
                ", age=" + age +
                ", salary=" + salary;
    }
}
