package com.mongodb.quickstart;

import com.mongodb.client.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.descending;

public class ReadCDC {

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create(System.getProperty("mongodb.uri"))) {
            MongoDatabase sampleTrainingDB = mongoClient.getDatabase("myDatabase");
            MongoCollection<Document> myCDCCollection = sampleTrainingDB.getCollection("myCollection");

        Document cdcDocument = myCDCCollection.find(new Document("recordId", "CDC")).first();
        System.out.println("CDC Record: " + cdcDocument.toJson());

        }
    }
}