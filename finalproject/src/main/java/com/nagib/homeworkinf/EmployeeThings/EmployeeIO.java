package com.nagib.homeworkinf.EmployeeThings;

import java.io.*;
import java.util.ArrayList;

public class EmployeeIO {
    public static void exportEmployeesToCSV(File file, ArrayList<Employee> listEmp, ArrayList<AdditionalInfo> listInf) {     //Ex 2
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)))) {
            for (int i = 0; i < listEmp.size(); i++) {
                Employee temp = listEmp.get(i);
                AdditionalInfo tempo = listInf.get(i);
                out.write(temp.getId() + "," + temp.getName() + "," + temp.getPost() + ","
                        + temp.getAge() + "," + temp.getSalary() + "," + temp.getInfoId() + ","
                        + tempo.getId() + "," + tempo.getPhone() + "," + tempo.getAddress() + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Employee> importFromFileStudents(File file) {                //Ex 1
        ArrayList<Employee> studList = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String field;
            while ((field = in.readLine()) != null) {
                String[] fields = field.split(",");
                try {     //Если встретилась битая строка, то выдам сообщение о происхождении ошибки и продолжу дальше.
                    studList.add(new Employee(Integer.valueOf(fields[0]), fields[1], fields[2],
                            Integer.valueOf(fields[3]), Integer.valueOf(fields[4]), Integer.valueOf(fields[5])));
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName() + " " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return studList;
    }
}
