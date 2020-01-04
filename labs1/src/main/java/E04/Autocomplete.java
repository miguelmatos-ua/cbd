package E04;

import redis.clients.jedis.Jedis;

import java.io.*;
import java.util.*;

public class Autocomplete {
    private Jedis jedis = new Jedis("localhost");

    private void saveNames(String names) {
        jedis.rpush("names", names);
    }

    private void saveNamesPop(String name, int pop) {
        jedis.zadd("namespop", pop, name);
    }

    private List<String> values(String name) {
        List<String> lst = new LinkedList<>();
        int max = jedis.lrange("names", 0, -1).size();
        for (int i = 0; i < max; i++) {
            for(String s : jedis.lrange("names", i,i)) { // only one value
                if(s.startsWith(name))
                    lst.add(s);
            }
        }
        return lst;
    }

    private List<String> valuesPop(String name) {
        List<String> lst = new LinkedList<>();
        int max = jedis.zrange("namespop", 0, -1).size();
        for (int i = 0; i < max; i++) {
            for(String s : jedis.zrange("namespop", i,i)) { // only one value
                if(s.startsWith(name))
                    lst.add(s);
            }
        }
        return lst;
    }

    private static void exA() {
        Autocomplete autocomplete = new Autocomplete();

        try {
            BufferedReader br = new BufferedReader(new FileReader("female-names.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                autocomplete.saveNames(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Search for: ");
        String line = sc.nextLine();
        for (String str : autocomplete.values(line)) {
            System.out.println(str);
        }
    }

    private static void exB() {
        Autocomplete autocomplete = new Autocomplete();

        try {
            BufferedReader br = new BufferedReader(new FileReader("nomes-registados-2018.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                String name = line.split(",")[0];
                int pop = Integer.parseInt(line.split(",")[2]);
                autocomplete.saveNamesPop(name, pop);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Search for: ");
        String line = sc.nextLine();
        for (String str : autocomplete.valuesPop(line)) {
            System.out.println(str);
        }
    }

    public static void main(String[] args) {
        //exA();
        exB();
    }
}
