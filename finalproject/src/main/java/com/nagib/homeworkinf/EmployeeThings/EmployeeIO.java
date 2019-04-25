package com.nagib.homeworkinf.EmployeeThings;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EmployeeIO {
    public static void exportEmployeesToCSV(Path path, ArrayList<String> employees) throws IOException{
        Files.write(path, employees);
    }

    public static ArrayList<Employee> importEmployeesFromCSV(Path path) throws IOException{     //just additional method
        List<String> allLines = Files.readAllLines(path);
        ArrayList<Employee> employees = new ArrayList<>();
        for (String line: allLines) {
            String[] temp = line.split(",");
            employees.add(new Employee(Integer.valueOf(temp[0]), temp[1], temp[2],
                    Integer.valueOf(temp[3]), Integer.valueOf(temp[4]), Integer.valueOf(temp[5])));
        }
        return employees;
    }

    public static ArrayList<Employee> importEmployeesFromJSON(Path path) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Employee> employees = mapper.readValue(path.toFile(), new TypeReference<ArrayList<Employee>>(){});
        return employees;
    }


    public static void exportEmployeesToJSON(Path path, ArrayList<Employee> employees) throws IOException {     //just additional method
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(path.toFile(), employees);
    }
}
