package E03;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class SimplePost {

    private Jedis jedis;
    private static String USERS = "users"; // Key set for users' name

    private SimplePost() {
        this.jedis = new Jedis("localhost");
    }

    private void saveUser(String username) {
        jedis.sadd(USERS, username);
    }

    private Set<String> getUser() {
        return jedis.smembers(USERS);
    }

    private Set<String> getAllKeys() {
        return jedis.keys("*");
    }

    private void saveInList(String value) {
        jedis.rpush("player", value);
    }

    private List<String> getPlayerFromList() {
        return jedis.lrange("player", 0, -1);
    }

    private void delFromList() {
        jedis.del("player");
    }

    private void saveInHashMap(Map<String, String> map) {
        jedis.hmset("clubs", map);
    }

    private Map<String,String> getFromHashMap() {
        return jedis.hgetAll("clubs");
    }

    private void delFromHashMap() {
        jedis.del("clubs");
    }

    public static void main(String[] args) {
        SimplePost board = new SimplePost();
        // set some users
        String[] users = {"Ana", "Pedro", "Maria", "Luis"};
        for (String user : users)
            board.saveUser(user);
        board.getAllKeys().forEach(System.out::println);
        board.getUser().forEach(System.out::println);

        // list
        String[] players = {"Ronaldo", "Messi", "Neymar", "Jonas"};
        for (String player : players) {
            board.saveInList(player);
        }
        board.getPlayerFromList().forEach(System.out::println);
        // clean database to avoid repetitions
        //board.delFromList();

        // hashmap
        Map<String, String> map = new HashMap<>();
        map.put("Ronaldo", "Juventus");
        map.put("Messi", "Barcelona");
        map.put("Neymar", "PSG");
        map.put("Jonas", "Benfica");
        board.saveInHashMap(map);
        System.out.println(board.getFromHashMap());
        //clean database
        //board.delFromHashMap();
    }
}



