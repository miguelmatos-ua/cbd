package com.company;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Main {

    public static void main(String[] args) {
        Neo4j driver = new Neo4j("bolt://localhost:7687");
        driver.insertToDatabase("datasets.csv");
        PrintStream printStream = null;
        try {
            printStream = new PrintStream("CBD_L44c_output.txt");
            printStream.println(driver.query("MATCH (n:Dataset)-[:IN_CLOUD]->(c:Cloud) WHERE c.cloud=\"GitHub\" RETURN n"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
