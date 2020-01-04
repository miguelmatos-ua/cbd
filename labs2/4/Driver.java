package E04;

import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Indexes;
import org.bson.Document;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;

public class Driver {
    private MongoCollection<Document> document;

    public Driver(String db, String col) {
        MongoClient client = new MongoClient();
        MongoDatabase database = client.getDatabase(db);
        document = database.getCollection(col);
    }

    public void insert(Document doc) {
        document.insertOne(doc);
    }

    public void edit(Document doc) {
        document.updateOne(eq("restaurant_id", doc.get("restaurant_id")), new Document("$set", doc));
    }

    public FindIterable<Document> find(String key, Object id) {
        return document.find(eq(key, id));
    }

    public void remove(String key, Object id) {
        document.findOneAndDelete(eq(key, id));
    }

    public void createIndex(String key) {
        document.createIndex(Indexes.ascending(key));
    }

    public void createIndexText(String key) {
        document.createIndex(Indexes.text(key));
    }

    public int countLocalidades() {
        AggregateIterable<Document> output = document.aggregate(Arrays.asList(
                new Document("$group", new Document("_id", "$localidade")),
                new Document("$count", "localidades")
        ));
        int i = 0;
        for (Document d : output) {
            i = (int) d.get("localidades");
        }
        return i;
    }

    public Map<String, Integer> countRestByLocalidade() {
        Map<String, Integer> restByLocal = new HashMap<>();
        AggregateIterable<Document> output = document.aggregate(Collections.singletonList(
                new Document("$group", new Document("_id", "$localidade").append("number_of_rest", new Document("$sum", 1)))
        ));
        for (Document d : output) {
            restByLocal.put((String) d.get("_id"), (int) d.get("number_of_rest"));
        }
        return restByLocal;
    }

    public Map<String, Integer> countRestByLocalidadeByGastronomia() {
        Map<String, Integer> restByLocalByGast = new HashMap<>();
        AggregateIterable<Document> output = document.aggregate(Collections.singletonList(
                new Document("$group", new Document("_id", new Document("local", "$localidade").append("gast", "$gastronomia").append("count", new Document("$sum", 1))))

        ));
        for (Document d : output) {
            restByLocalByGast.put(((Document) d.get("_id")).get("local") + " | " + ((Document) d.get("_id")).get("gast"), (int) ((Document) d.get("_id")).get("count"));
        }
        return restByLocalByGast;
    }

    public List<String> getRestWithNameCloserTo(String name) {
        List<String> ret = new ArrayList<>();
        AggregateIterable<Document> output = document.aggregate(Arrays.asList(
                new Document("$project", new Document("_id", 0).append("nome", 1)),
                new Document("$match", new Document("nome", new Document("$regex", name)))
        ));
        for(Document d : output)
            ret.add((String) d.get("nome"));
        return ret;
    }
}
