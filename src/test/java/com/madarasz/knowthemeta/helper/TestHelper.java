package com.madarasz.knowthemeta.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;

public class TestHelper {

    public String getTestResource(String fileName) {
        File file = new File("src/test/resources/" + fileName);
        String result = "";
        try {
            result = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getQueryStringResult(Driver driver, String queryString) {
        try (Session session = driver.session()) {
            return session.readTransaction(new TransactionWork<String>() {
                @Override
                public String execute( Transaction tx ) {
                    Result result = tx.run(queryString);
                    if (!result.hasNext()) return "error: no match";
                    return result.single().get(0).asString();
                }
            } );
        }
    }

    public JsonElement getJsonFromTestResource(String fileName) {
        String resultString = getTestResource(fileName);
        return JsonParser.parseString(resultString);
    }
}