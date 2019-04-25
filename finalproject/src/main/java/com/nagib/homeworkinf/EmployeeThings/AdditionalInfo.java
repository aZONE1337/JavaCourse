package com.nagib.homeworkinf.EmployeeThings;

import java.io.Serializable;

public class AdditionalInfo implements Serializable {
    int id;
    String phone;
    String address;

    public int getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public AdditionalInfo(int id, String phone, String address) {
        this.id = id;
        this.phone = phone;
        this.address = address;
    }

    @Override
    public String toString() {
        return "," + phone + "," + address;
    }
}
