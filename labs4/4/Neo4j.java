package com.company;

import org.neo4j.driver.*;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Neo4j implements AutoCloseable {
    private final Driver driver;
    private PrintStream printStream;

    public Neo4j(String uri) {
        this.driver = GraphDatabase.driver(uri);
        try {
            printStream = new PrintStream("CBD_L44c_output.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void insertToDatabase(String file) {
        try (Session session = driver.session()) {
            String insert = session.writeTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction transaction) {
                    Result r = transaction.run("LOAD CSV WITH HEADERS FROM \"file:///" + file + "\" AS row " +
                            "MERGE (d:Dataset {name:row.datasetName, about:row.about, link:row.link, date:row.vintage}) " +
                            "MERGE (a:Category {name:row.category_name})" +
                            "MERGE (c:Cloud {cloud:row.cloud}) " +
                            "MERGE (d)-[:IN_CLOUD]->(c)" +
                            "MERGE (d)-[:CATEGORY]->(a)");
                    return r.toString();
                }
            });
        }
    }

    public String query(String query) {
        try (Session session = driver.session()) {
            return session.writeTransaction(transaction -> transaction.run(query).toString());
        }
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }
}
