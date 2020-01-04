package E01;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

import java.util.HashMap;
import java.util.Map;

public class Demo {
    private Cluster builder;
    private Session session;

    public Demo(String contactPoint, String keyspace) {
        builder = new Cluster.Builder().addContactPoint(contactPoint).build();
        session = builder.connect(keyspace);
    }

    private void execute(String s) {
        session.execute(s);
        System.out.println(s);
    }

    private void create_table(String table_name, Map<String, String> items, String primaryKey) {
        StringBuilder stringBuilder = new StringBuilder("CREATE TABLE ")
                .append("IF NOT EXISTS ")
                .append(table_name)
                .append("(");
        for (String k : items.keySet()) {
            stringBuilder.append(k).append(" ").append(items.get(k)).append(",");
        }
        stringBuilder.append("Primary Key (").append(primaryKey).append(")").append(")");
        System.out.println(stringBuilder.toString());
        session.execute(stringBuilder.toString());
        System.out.println("Table " + table_name + " created");
    }

    public static void main(String[] args) {
        Demo c = new Demo("127.0.0.1", "cbd_3");

        c.execute("use cbd_3");
        Map<String, String> map = new HashMap<>();
        map.put("id", "int");
        map.put("name", "text");
        map.put("city", "text");
        map.put("sal", "varint");
        map.put("phone", "varint");

        c.create_table("emp", map, "id");

        String query1 = "INSERT INTO emp (id, name, city, phone,  sal)"
                + " VALUES(1,'ram', 'Hyderabad', 9848022338, 50000);";
        String query2 = "INSERT INTO emp (id, name, city," +
                "phone, sal)" + " VALUES(2, 'robin', 'Hyderabad', 9848022339, 40000);";
        String query3 = "INSERT INTO emp (id, name, city, phone, sal)"
                + " VALUES(3,'rahman', 'Chennai', 9848022330, 45000);";


        c.execute(query1);
        c.execute(query2);
        c.execute(query3);
    }
}
