package E04;

import org.bson.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class Demo {
    private static void exA(Driver driver) {
        String dateStr = "2017-01-25 09:28:04.041 UTC";
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ZZZ").parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Example of a restaurant to insert into the db
        Document newRest = new Document("address",
                new Document("building", "500")
                        .append("coord", Arrays.asList(-73.961704, 40.662942))
                        .append("rua", "Flatbush Avenue")
                        .append("zipcode", 11226))
                .append("localidade", "Brooklyn")
                .append("gastronomia", "Hamburgers")
                .append("grades", Arrays.asList(
                        new Document("date", date)
                                .append("grade", "A")
                                .append("score", 8),
                        new Document("date", date)
                                .append("grade", "B")
                                .append("score", 7)))
                .append("nome", "Batista")
                .append("restaurant_id", 30112341);

        // Insert the restaurant
        driver.insert(newRest);
        Document find = driver.find("restaurant_id", 30112341).first();
        assert find != null;
        System.out.println(find.toJson());

        // Edit the document (change the name to "New Name")
        driver.edit(new Document("nome", "New Name").append("restaurant_id", 30112341));
        System.out.println(find.toJson());

        // Remove the restaurant
        driver.remove("restaurant_id", 30112341);
    }

    private static void exB(Driver driver) {
        System.out.println("Creating index for \"localidade\"");
        driver.createIndex("localidade");
        System.out.println("Creating index for \"gastronomia\"");
        driver.createIndex("gastronomia");
        System.out.println("Creating text index for \"nome\"");
        driver.createIndexText("nome");
    }

    private static void exC(Driver driver) {
        System.out.println("Numero de localidades distintas: " + driver.countLocalidades());
        System.out.println("Numero de restaurantes por localidade:");
        Map<String, Integer> restByLocal = driver.countRestByLocalidade();
        for (String key : restByLocal.keySet()) {
            System.out.println("\t-> " + key + " - " + restByLocal.get(key));
        }
        Map<String, Integer> restByLocalByGast = driver.countRestByLocalidadeByGastronomia();
        System.out.println("Numero de restaurantes por localidade e gastronomia:");
        for (String key : restByLocalByGast.keySet()) {
            System.out.println("\t-> " + key + " - " + restByLocalByGast.get(key));
        }
        System.out.println("Nome de restaurantes contendo \"Park\" no nome:");
        driver.getRestWithNameCloserTo("Park").forEach(System.out::println);
    }

    public static void main(String[] args) {
        Driver driver = new Driver("cbd", "rest");
//        exA(driver);
//        exB(driver);
        exC(driver);
    }
}
