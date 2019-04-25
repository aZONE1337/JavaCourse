package com.nagib.homeworkinf.EmployeeThings;

import java.io.Serializable;

public class Employee implements Serializable{
    int id;
    String name;
    String post;
    int age;
    int salary;
    int infoId;

    public Employee() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public void setInfoId(int infoId) {
        this.infoId = infoId;
    }

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
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Employee another = (Employee)obj;
        return another.id == this.id && another.age == this.age && another.salary == this.salary
                && another.infoId == this.infoId && another.name.equals(this.name) && another.post.equals(this.post);
    }

        @Override
    public String toString() {
        return id + "," + name + "," + post  + "," + age  + "," + salary;
    }
}
