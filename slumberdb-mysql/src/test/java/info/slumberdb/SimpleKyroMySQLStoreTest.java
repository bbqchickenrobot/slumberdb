package info.slumberdb;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.boon.Maps;
import org.boon.Str;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;

/**
 * Created by Richard on 4/5/14.
 */
public class SimpleKyroMySQLStoreTest {

    private SimpleKyroKeyValueStoreMySQL<Employee> store;
    String url = "jdbc:mysql://localhost:3306/slumberdb";
    String userName = "slumber";
    String password = "slumber1234";
    String table = "kyro-emp-test";


    public static class Employee implements Serializable {
        String firstName;
        String lastName;
        String id;


        public Employee() {

        }

        public Employee(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Employee)) return false;

            Employee employee = (Employee) o;

            if (firstName != null ? !firstName.equals(employee.firstName) : employee.firstName != null) return false;
            if (id != null ? !id.equals(employee.id) : employee.id != null) return false;
            if (lastName != null ? !lastName.equals(employee.lastName) : employee.lastName != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = firstName != null ? firstName.hashCode() : 0;
            result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
            result = 31 * result + (id != null ? id.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Employee{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }

    boolean ok;




    @Test
    public void testKyro() {

        Employee employee = new Employee("Rick", "Hightower");
        Kryo kryo = new Kryo();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);

        kryo.writeClassAndObject(output, employee);

        output.close();

        final byte[] bytes = outputStream.toByteArray();



        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        Input input = new Input(inputStream);

        Object object = kryo.readClassAndObject(input);

        puts(object);



    }

    @Before
    public void setup() {

        store = new SimpleKyroKeyValueStoreMySQL(url, userName, password, table, Employee.class);

    }

    @After
    public void close() {


        store.close();
    }


    @Test
    public void testBulkPut() {

        Map<String, Employee> map = Maps.map(

                "123", new Employee("Rick", "Hightower"),
                "456", new Employee("Paul", "Tabor"),
                "789", new Employee("Jason", "Daniel")

        );


        store.putAll(map);


        Employee employee;


        employee = store.get("789");
        Str.equalsOrDie("Jason", employee.getFirstName());
        Str.equalsOrDie("Daniel", employee.getLastName());


        employee = store.get("456");
        Str.equalsOrDie("Paul", employee.getFirstName());
        Str.equalsOrDie("Tabor", employee.getLastName());

        employee = store.get("123");
        Str.equalsOrDie("Rick", employee.getFirstName());
        Str.equalsOrDie("Hightower", employee.getLastName());

    }


    @Test
    public void sillyTestForCodeCoverage() {

        KeyValueIterable<String, Employee> entries = store.loadAll();

        Iterator<Entry<String, Employee>> iterator = entries.iterator();


        try {
            while (iterator.hasNext()) {
                iterator.remove();
            }


        } catch (Exception ex) {

        }
    }


    @Test
    public void testSearch() {
        for (int index = 0; index < 100; index++) {

            store.put("key." + index, new Employee("Rick" + index, "Hightower"));
        }

        KeyValueIterable<String, Employee> entries = store.search("key.50");

        int count = 0;

        for (Entry<String, Employee> entry : entries) {
            puts(entry.key(), entry.value());
            count++;
        }


        ok = (count > 20 && count < 60) || die(count);
        entries.close();
    }


    @Test
    public void testIteration() {

        for (int index = 0; index < 100; index++) {

            store.put("key." + index, new Employee("Rick" + index, "Hightower"));
        }

        KeyValueIterable<String, Employee> entries = store.loadAll();

        int count = 0;

        for (Entry<String, Employee> entry : entries) {
            puts(entry.key(), entry.value());
            count++;
        }

        ok = (count == 100) || die(count);

        entries.close();

    }


    @Test
    public void testBulkRemove() {


        Map<String, Employee> map = Maps.map(

                "123", new Employee("Rick", "Hightower"),
                "456", new Employee("Paul", "Tabor"),
                "789", new Employee("Jason", "Daniel")

        );


        store.putAll(map);


        Employee employee;


        employee = store.get("789");
        Str.equalsOrDie("Jason", employee.getFirstName());
        Str.equalsOrDie("Daniel", employee.getLastName());


        employee = store.get("456");
        Str.equalsOrDie("Paul", employee.getFirstName());
        Str.equalsOrDie("Tabor", employee.getLastName());

        employee = store.get("123");
        Str.equalsOrDie("Rick", employee.getFirstName());
        Str.equalsOrDie("Hightower", employee.getLastName());


        store.removeAll(map.keySet());


        employee = store.get("123");

        ok = employee == null || die();

        employee = store.get("456");


        ok = employee == null || die();


    }

}
