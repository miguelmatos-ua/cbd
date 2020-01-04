package E05;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class Mensagem {
    private Jedis jedis = new Jedis("localhost");
    private JedisPubSub jedisPubSub = new JedisPubSub() {

        @Override
        public void onMessage(String channel, String message) {
            System.out.println("User " + channel + " has sent a message: " + message);
        }

        @Override
        public void onSubscribe(String channel, int subscribedChannels) {
            System.out.println("User is Subscribed to channel : " + channel);
            System.out.println("User is Subscribed to " + subscribedChannels + " no. of channels");
        }

        @Override
        public void onUnsubscribe(String channel, int subscribedChannels) {
            System.out.println("Client is Unsubscribed from channel : " + channel);
            System.out.println("Client is Subscribed to " + subscribedChannels + " no. of channels");
        }

    };

    public void subscribe(String user1, String user2) {
        jedis.subscribe(jedisPubSub, user1, user2);
    }

    public void addUser(String username) {
        jedis.sadd("users", username);
    }

    public void storeMsg(String user, String msg) {
        jedis.publish(user, msg);
        jedis.sadd("users", user, msg);
    }

    public static void main(String[] args) {
        Mensagem m = new Mensagem();

        String[] users = {"userA", "userB", "userC"};

        for(String user : users) {
            m.addUser(user);
        }

        m.subscribe(users[0], users[1]);
    }
}
