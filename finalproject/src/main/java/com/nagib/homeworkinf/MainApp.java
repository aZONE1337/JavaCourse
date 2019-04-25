package com.nagib.homeworkinf;

import com.nagib.homeworkinf.DBThings.MyDBOperation;
import com.nagib.homeworkinf.EmployeeThings.EmployeeIO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Scanner;
import java.util.logging.*;

public class MainApp {
    private static final Logger logger = Logger.getLogger(MainApp.class.getName());

    public static void main(String[] args) {
        MyDBOperation dbOperation = new MyDBOperation("jdbc:sqlite:workers.db");
        try {
            Handler handler = new FileHandler("exceptions.log", true);
            handler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord logRecord) {
                    return logRecord.getSourceMethodName() + " " + logRecord.getLevel() + " " + logRecord.getMessage() + "\n";
                }
            });
            logger.setUseParentHandlers(false);
            logger.addHandler(handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dbOperation.connect();
            consoleWork(dbOperation);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Не удалось установить соединение с БД.");
        } finally {
            try {
                dbOperation.disconnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void consoleWork(MyDBOperation dbOperation) {
        Scanner scanner = new Scanner(System.in);
        boolean flag;
        getHelp();
        String cmd = scanner.nextLine();
        String[] cmdAndArgs;
        while (!(cmd.equals("."))) {
            flag = false;
            if (cmd.contains("??")) {
                getHelp();
                cmd = scanner.nextLine();
                continue;
            }
            cmdAndArgs = cmd.split("_");
            switch (makeDecision(cmdAndArgs[0])) {
                case (1):
                    try {
                        if (Boolean.valueOf(cmdAndArgs[1]) && !Boolean.valueOf(cmdAndArgs[1])) {
                            System.out.println("Ошибка в слове true или false");
                            System.out.println("Повторите ввод.");
                            cmd = scanner.nextLine();
                            continue;
                        }
                        dbOperation.createTable(Boolean.valueOf(cmdAndArgs[1]));
                        flag = true;
                    } catch (SQLException e) {
                        System.out.println("Не удалось удалить или создать таблицы. Повторите ввод.");
                        logger.log(Level.SEVERE,"Ошибка: " + e.getClass().getSimpleName() + ". Инфо.:" + e.getMessage());
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Некорректно введена команда. Повторите ввод.");
                        logger.log(Level.SEVERE,"Ошибка: " + e.getClass().getSimpleName() + ". Инфо.:" + e.getMessage());
                    }
                    break;
                case (2):
                    try {
                        dbOperation.addEmployee(cmdAndArgs[1], cmdAndArgs[2], cmdAndArgs[3], cmdAndArgs[4], cmdAndArgs[5],
                                cmdAndArgs[6], cmdAndArgs[7]);
                        flag = true;
                    } catch (SQLException e) {
                        System.out.println("Не удалось добавить данные. Повторите ввод.");
                        logger.log(Level.SEVERE,"Ошибка: " + e.getClass().getSimpleName() + ". Инфо.:" + e.getMessage());
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Некорректно введена команда. Повторите ввод.");
                        logger.log(Level.SEVERE,"Ошибка: " + e.getClass().getSimpleName() + ". Инфо.:" + e.getMessage());
                    }
                    break;
                case (3):
                    try {
                        System.out.println("Результат:" + dbOperation.getAverageSalary());
                    } catch (SQLException e) {
                        System.out.println("Не удалось выполнить запрос. Повторите ввод.");
                        logger.log(Level.SEVERE,"Ошибка: " + e.getClass().getSimpleName() + ". Инфо.:" + e.getMessage());
                    }
                    break;
                case (4):
                    try {
                        System.out.println("Результат:" + dbOperation.getAverageSalary(cmdAndArgs[1]));
                    } catch (SQLException e) {
                        System.out.println("Не удалось выполнить запрос. Повторите ввод.");
                        logger.log(Level.SEVERE,"Ошибка: " + e.getClass().getSimpleName() + ". Инфо.:" + e.getMessage());
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Некорректно введена команда. Повторите ввод.");
                        logger.log(Level.SEVERE,"Ошибка: " + e.getClass().getSimpleName() + ". Инфо.:" + e.getMessage());
                    }
                    break;
                case (5):
                    try {
                        System.out.println("Результат:" + dbOperation.findByPhone(cmdAndArgs[1]));
                    } catch (SQLException e) {
                        System.out.println("Не удалось выполнить поиск. Повторите ввод.");
                        logger.log(Level.SEVERE,"Ошибка: " + e.getClass().getSimpleName() + ". Инфо.:" + e.getMessage());
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Некорректно введена команда. Повторите ввод.");
                        logger.log(Level.SEVERE,"Ошибка: " + e.getClass().getSimpleName() + ". Инфо.:" + e.getMessage());
                    }
                    break;
                case (6):
                    try {
                        System.out.println("Результат:\n" + dbOperation.getEmployeesAndInfo());
                    } catch (SQLException e) {
                        System.out.println("Не удалось загрузить информацию о сотрудниках. Повторите ввод.");
                        logger.log(Level.SEVERE,"Ошибка: " + e.getClass().getSimpleName() + ". Инфо.:" + e.getMessage());
                    }
                    break;
                case (7):
                    try {
                        Path path = Paths.get(cmdAndArgs[1]);
                        if (Boolean.valueOf(cmdAndArgs[1]) && !Boolean.valueOf(cmdAndArgs[1])) {
                            System.out.println("Ошибка в слове true или false");
                            System.out.println("Повторите ввод.");
                            cmd = scanner.nextLine();
                            continue;
                        }
                        EmployeeIO.exportEmployeesToCSV(path, dbOperation.getEmployeesAndInfo());
                        flag = true;
                    } catch (SQLException | IOException e) {
                        System.out.println("Не удалось загрузить информацию о сотрудниках. Повторите ввод.");
                        logger.log(Level.SEVERE,"Ошибка: " + e.getClass().getSimpleName() + ". Инфо.:" + e.getMessage());
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Некорректно введена команда. Повторите ввод.");
                        logger.log(Level.SEVERE,"Ошибка: " + e.getClass().getSimpleName() + ". Инфо.:" + e.getMessage());
                    }
                    break;
                case (8):
                    try {
                        Path path = Paths.get(cmdAndArgs[1]);
                        dbOperation.importEmployees(path);
                        flag = true;
                    } catch (IOException | SQLException e) {
                        System.out.println("Не удалось произвести импорт сотрудников. Повторите ввод.");
                        logger.log(Level.SEVERE,"Ошибка: " + e.getClass().getSimpleName() + ". Инфо.:" + e.getMessage());
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Некорректно введена команда. Повторите ввод.");
                        logger.log(Level.SEVERE,"Ошибка: " + e.getClass().getSimpleName() + ". Инфо.:" + e.getMessage());
                    }
                    break;
                default:
                    System.out.println("Некорректно введена команда. Повторите ввод.");
                    flag = false;
            }
            if (flag) {
                System.out.println("Успешно!");
            }
            cmd = scanner.nextLine();
        }
    }

    private static int makeDecision(String cmd) {
        if (cmd.equals("create table")) {
            return 1;
        }
        if (cmd.equals("add employee")) {
            return 2;
        }
        if (cmd.equals("get avg salary")) {
            return 3;
        }
        if (cmd.equals("get avg post salary")) {
            return 4;
        }
        if (cmd.equals("find by phone")) {
            return 5;
        }
        if (cmd.equals("get employees")) {
            return 6;
        }
        if (cmd.equals("export"))
            return 7;
        if (cmd.equals("import"))
            return 8;
        return 0;
    }

    private static void getHelp() {
        System.out.println("Справка:\n" +
                "Общий формат команд: *команда*_*аргумент1_*аргумент2*_*аргумент n*\n" +
                "Доступные команды:\n\n" +
                "create table_*bool dropDecision* - Создание таблицы. При арг1 = true Пересоздание существующей таблицы.\n" +
                "Пример: create table_false\n\n" +
                "add employee_*name*_*post*_*int age*_*int salary*_*int info id*_*phone ??-??-??*_*address*\n" +
                "Добавление сотрудника. !!info id разных сотрудников должны различаться!!\n" +
                "Пример: add employee_Bob_Manager_25_57000_5_77-22-33_Moscow Amurskaya st. 29\n\n" +
                "get avg salary - Выводит среднюю з/п среди всех сотруников\n" +
                "Пример: get avg salary\n\n" +
                "get avg post salary_*post* - Выводит среднюю з/п сотрудников на посте post\n" +
                "Пример: get avg salary_Manager\n\n" +
                "find by phone_*phone ??-??-?? - Поиск сотрудника по номеру телефона phone\n" +
                "Пример: find by phone_77-22-33\n\n" +
                "get employees - Выводит всех добавленных сотрудников.\n" +
                "Пример: get employees\n\n" +
                "export_*path.csv*  - Экспорт списка сотрудников в csv файл.\n" +
                "Пример: import_employees.csv\n\n" +
                "import_*path.json* - Ипорт в БД списка сотрудников из JSON файла." +
                "Пример: export_employees.json\n\n" +
                "введите \".\" чтобы выйти из приложения.\n" +
                "введите \"??\" чтобы получить справку повторно.\n");
    }
}
