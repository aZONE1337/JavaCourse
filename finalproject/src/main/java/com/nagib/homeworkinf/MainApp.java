package com.nagib.homeworkinf;

import com.nagib.homeworkinf.EmployeeThings.AdditionalInfo;
import com.nagib.homeworkinf.EmployeeThings.Employee;
import com.nagib.homeworkinf.EmployeeThings.EmployeeIO;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;

public class MainApp {
    private static Connection connection;
    private static PreparedStatement prepStmt;
    private static Statement stmt;
    private static Path path;

    public static void main(String[] args) {
        try {
            /*
            Нужны: логи, тесты, нормальная обработка исключений!!
            Плохо работает экспорт из БД в файл. Не пишется AdditionalInfo
            Нет экспорта в БД из файла.
            Нет работы через консоль.(в конце)

            ___________________________пометочки (:__________________________
            Найти, как удалить все записи из таблицы, а не дропать её, если так можно.
            Разобраться с дозаписью/перезаписью в csv.
            Подумать над WorkWithDB классом.
            Лишний раз посмотреть на код, вдруг чего не понравится/придумаю нового.
             */
            connect();
            createTable(true);
            addEmployee("Bob", "Manager", "23", "80600", "10", "23-45-17", "Moscow");
            addEmployee("Mary", "Developer", "25", "77500", "15", "23-45-13", "St.Petersburg");
            addEmployee("Michael", "Top Manager", "24", "115150", "20", "23-45-20", "Samara");
            addEmployee("Janette", "Designer", "27", "58750", "25", "23-50-17", "Moscow");
            addEmployee("Peter", "Designer", "22", "62300", "30", "26-20-51", "St.Petersburg");
            System.out.println(getAverageSalary());
            System.out.println(getAveragePostSalary("Designer"));
            System.out.println(findByPhone("23-50-17"));
            System.out.println(getEmployeesAndInfo());
            path = Paths.get("employees.csv");
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
            EmployeeIO.exportEmployeesToCSV(path.toFile(), getAllEmployees(), getAllAdditionalInfo()); //Пишутся только работники, без доп. информации.
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:workers.db");
            stmt = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            new RuntimeException("Невозможно подключиться к БД");
        }
    }

    public static void disconnect() {
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTable(boolean dropDecision) throws SQLException {
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

    public static void addEmployee(String name, String post, String age, String salary, String info_id, String phone, String address) throws SQLException {
        prepStmt = connection.prepareStatement("INSERT INTO AdditionalInfo (id, phone, address) VALUES (?, ?, ?)");
        prepStmt.setString(1, info_id);
        prepStmt.setString(2, phone);
        prepStmt.setString(3, address);
        prepStmt.addBatch();
        prepStmt.executeBatch();
        prepStmt = connection.prepareStatement("INSERT INTO Employee (name, post, age, salary, info_id) VALUES (?, ?, ?, ?, ?)");
        prepStmt.setString(1, name);
        prepStmt.setString(2, post);
        prepStmt.setString(3, age);
        prepStmt.setString(4, salary);
        prepStmt.setString(5, info_id);
        prepStmt.addBatch();
        prepStmt.executeBatch();
    }

    public static int getAverageSalary() throws SQLException {
        prepStmt = connection.prepareStatement("SELECT salary FROM Employee");
        ResultSet rs = prepStmt.executeQuery();
        int count = 0, sum = 0;
        while (rs.next()) {
            sum += rs.getInt(1);
            count++;
        }
        return sum / count;
    }

    public static int getAveragePostSalary(String post) throws SQLException {
        prepStmt = connection.prepareStatement("SELECT salary FROM Employee WHERE post = ?");
        prepStmt.setString(1, post);
        ResultSet rs = prepStmt.executeQuery();
        int count = 0, sum = 0;
        while (rs.next()) {
            sum += rs.getInt(1);
            count++;
        }
        return sum / count;
    }

    public static String findByPhone(String phone) throws SQLException {
        prepStmt = connection.prepareStatement("SELECT name " +
                "FROM (Employee, AdditionalInfo) WHERE phone = ? AND Employee.info_id = AdditionalInfo.id");
        prepStmt.setString(1, phone);
        ResultSet rs = prepStmt.executeQuery();
        return rs.getString(1);
    }

    public static ArrayList<Employee> getAllEmployees() throws SQLException {
        ArrayList<Employee> employees = new ArrayList<>();
        prepStmt = connection.prepareStatement("SELECT * FROM Employee");
        ResultSet rs = prepStmt.executeQuery();
        while (rs.next()) {
            employees.add(new Employee(rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getInt(4), rs.getInt(5), rs.getInt(6)));
        }
        employees.sort(Comparator.comparingInt(Employee::getId));
        return employees;
    }

    public static ArrayList<AdditionalInfo> getAllAdditionalInfo() throws SQLException {
        ArrayList<AdditionalInfo> infoArrayList = new ArrayList<>();
        prepStmt = connection.prepareStatement("SELECT * FROM AdditionalInfo");
        ResultSet rs = prepStmt.executeQuery();
        while (rs.next()) {
            infoArrayList.add(new AdditionalInfo(rs.getInt(1), rs.getString(2), rs.getString(3)));
        }
        infoArrayList.sort(Comparator.comparingInt(AdditionalInfo::getId));
        return infoArrayList;
    }

    public static ArrayList<String> getEmployeesAndInfo() throws SQLException{
        ArrayList<Employee> employees = getAllEmployees();
        ArrayList<AdditionalInfo> infos = getAllAdditionalInfo();
        ArrayList<String> everything = new ArrayList<>();
        for (int i = 0; i < employees.size(); i++) {
            everything.add(employees.get(i).toString() + infos.get(i).toString());
        }
        return everything;
    }
}
