package com.nagib.homeworkinf.DBThings;

import com.nagib.homeworkinf.EmployeeThings.AdditionalInfo;
import com.nagib.homeworkinf.EmployeeThings.Employee;
import com.nagib.homeworkinf.EmployeeThings.EmployeeIO;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;

public class MyDBOperation {

    private Connection connection;
    private Statement stmt;
    private PreparedStatement prepStmt;
    private String connURL;

    public MyDBOperation(String connURL) {
        this.connURL = connURL;
    }

    public void connect() throws SQLException, ClassNotFoundException{
        Class.forName("org.sqlite.JDBC");
        this.connection = DriverManager.getConnection(connURL);
        this.stmt = connection.createStatement();
    }

    public void disconnect() throws SQLException {
        connection.close();
        stmt.close();
        if (prepStmt != null) {
            prepStmt.close();
        }
    }

    public void createTable(boolean dropDecision) throws SQLException {
        if (dropDecision) {
            stmt.executeUpdate("DROP TABLE IF EXISTS Employee");
            stmt.executeUpdate("DROP TABLE IF EXISTS AdditionalInfo");
        }
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS AdditionalInfo (\n"
                + "	id integer PRIMARY KEY UNIQUE,\n"
                + " phone integer NOT NULL,\n"
                + " address text NOT NULL\n"
                + ");");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Employee (\n"
                + "	id integer PRIMARY KEY AUTOINCREMENT,\n"
                + "	name text NOT NULL,\n"
                + "	post text NOT NULL,\n"
                + " age integer NOT NULL,\n"
                + " salary integer NOT NULL,\n"
                + " info_id integer NOT NULL\n"
                + " REFERENCES AdditionalInfo (id)\n"
                + ");");
    }

    public void addEmployee(String name, String post, String age, String salary, String info_id, String phone, String address) throws SQLException {
        /*
        Дальнейший блок try catch для того, чтобы не было ситуации,
        когда age или salary не приводимы к integer.
        Потому что в таком случае, будет добавлена запись в AdditionalInfo,
        но не будет ничего добавлено в Employee, чего быть не должно,
        чтобы не нарушалась целостность ссылок в БД.
         */
        try {
            Integer.valueOf(age);
            Integer.valueOf(salary);
            Integer.valueOf(info_id);
        } catch (Exception e) {
            throw new SQLException();
        }
        try {
            String sqlRequest;
            sqlRequest = "INSERT INTO AdditionalInfo (id, phone, address) VALUES (?, ?, ?)";
            prepStmt = connection.prepareStatement(sqlRequest);
            prepStmt.setInt(1, Integer.valueOf(info_id));
            prepStmt.setString(2, phone);
            prepStmt.setString(3, address);
            prepStmt.addBatch();
            prepStmt.executeBatch();
            prepStmt.close();
            sqlRequest = "INSERT INTO Employee (name, post, age, salary, info_id) VALUES (?, ?, ?, ?, ?)";
            prepStmt = connection.prepareStatement(sqlRequest);
            prepStmt.setString(1, name);
            prepStmt.setString(2, post);
            prepStmt.setInt(3, Integer.valueOf(age));
            prepStmt.setInt(4, Integer.valueOf(salary));
            prepStmt.setInt(5, Integer.valueOf(info_id));
            prepStmt.addBatch();
            prepStmt.executeBatch();
            prepStmt.close();
        } catch (Exception e) {         //Чтобы бросить SQLException, если будет исключение в одном из кастов выше.
            throw new SQLException();
        }
    }

    public int getAverageSalary() throws SQLException {
        String sqlRequest;
        sqlRequest = "SELECT salary FROM Employee";
        prepStmt = connection.prepareStatement(sqlRequest);
        ResultSet rs = prepStmt.executeQuery();
        int count = 0, sum = 0;
        while (rs.next()) {
            sum += rs.getInt(1);
            count++;
        }
        return sum / count;
    }

    public int getAverageSalary(String post) throws SQLException {
        String sqlRequest;
        sqlRequest = "SELECT salary FROM Employee WHERE post = ?";
        prepStmt = connection.prepareStatement(sqlRequest);
        prepStmt.setString(1, post);
        ResultSet rs = prepStmt.executeQuery();
        int count = 0, sum = 0;
        while (rs.next()) {
            sum += rs.getInt(1);
            count++;
        }
        return sum / count;
    }

    public String findByPhone(String phone) throws SQLException {
        String sqlRequest;
        sqlRequest = "SELECT name " +
                "FROM (Employee, AdditionalInfo) WHERE phone = ? AND Employee.info_id = AdditionalInfo.id";
        prepStmt = connection.prepareStatement(sqlRequest);
        prepStmt.setString(1, phone);
        ResultSet rs = prepStmt.executeQuery();
        return rs.getString(1);
    }

    public ArrayList<Employee> getAllEmployees() throws SQLException{
        ArrayList<Employee> employees = new ArrayList<>();
        String sqlRequest;
        sqlRequest = "SELECT * FROM Employee";
        prepStmt = connection.prepareStatement(sqlRequest);
        ResultSet rs = prepStmt.executeQuery();
        while (rs.next()) {
            employees.add(new Employee(rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getInt(4), rs.getInt(5), rs.getInt(6)));
        }
        employees.sort(Comparator.comparingInt(Employee::getId));
        return employees;
    }

    public ArrayList<AdditionalInfo> getAllAdditionalInfo() throws SQLException {
        ArrayList<AdditionalInfo> infoArrayList = new ArrayList<>();
        String sqlRequest;
        sqlRequest = "SELECT * FROM AdditionalInfo";
        prepStmt = connection.prepareStatement(sqlRequest);
        ResultSet rs = prepStmt.executeQuery();
        while (rs.next()) {
            infoArrayList.add(new AdditionalInfo(rs.getInt(1), rs.getString(2), rs.getString(3)));
        }
        infoArrayList.sort(Comparator.comparingInt(AdditionalInfo::getId));
        return infoArrayList;
    }

    public ArrayList<String> getEmployeesAndInfo() throws SQLException {
        ArrayList<Employee> employees = getAllEmployees();
        ArrayList<AdditionalInfo> infos = getAllAdditionalInfo();
        ArrayList<String> everything = new ArrayList<>();
        if (employees.size() != infos.size()) {
            System.out.println("Количество сотрудников не соответствует количествую дополнительной информации о них.\n" +
                    "Возможно, была заполнена только таблица Employee");
            if (infos.size() == 0) {
                for (Employee employee: employees) {
                    everything.add(employee.toString());
                }
            }
            return everything;
        }
        for (int i = 0; i < employees.size(); i++) {
            everything.add(employees.get(i).toString() + infos.get(i).toString());
        }
        return everything;
    }

    public void importEmployees(Path path) throws SQLException, IOException {
        ArrayList<Employee> employees = EmployeeIO.importEmployeesFromJSON(path);
        String sqlRequest;
        sqlRequest = "INSERT INTO Employee (name, post, age, salary, info_id) VALUES (?, ?, ?, ?, ?)";
        prepStmt = connection.prepareStatement(sqlRequest);
        for (Employee employee: employees) {
            prepStmt.setString(1, employee.getName());
            prepStmt.setString(2, employee.getPost());
            prepStmt.setInt(3, employee.getAge());
            prepStmt.setInt(4, employee.getSalary());
            prepStmt.setInt(5, employee.getInfoId());
            prepStmt.addBatch();
            prepStmt.executeBatch();
        }
    }
}

