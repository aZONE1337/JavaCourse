import com.nagib.homeworkinf.EmployeeThings.Employee;
import com.nagib.homeworkinf.EmployeeThings.EmployeeIO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/*
There are no other tests. Only this one...
 */
public class TestIO {
    private List<Employee> employees;

    @Before
    public void initObjects() {
        employees = Arrays.asList(new Employee(1, "Bob", "Manager", 23,	80600, 10),
                new Employee(2,	"Mary", "Developer", 25, 77500, 15),
                new Employee(3, "Michael", "Top Manager", 24, 115150, 20),
                new Employee(4, "Janette", "Designer", 27, 58750, 25),
                new Employee(5, "Peter", "Designer", 22, 62300, 30));
    }

    @Test
    public void testImport() throws IOException {
        Assert.assertArrayEquals(employees.toArray(), EmployeeIO.importEmployeesFromJSON(Paths.get("testImport.json")).toArray());
    }
}
