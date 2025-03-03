package com.zf.customchat.mongodb.test;


import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test1 {

    public static void main(String[] args){
//        ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017");
//        MongoClient mongoClient = MongoClients.create(connectionString);
        // Replace the placeholder with your Atlas connection string
        String uri = "mongodb://localhost:27017";
        // Construct a ServerApi instance using the ServerApi.builder() method
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .serverApi(serverApi)
                .build();
        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            MongoDatabase database = mongoClient.getDatabase("myNewDb");
            MongoCollection<Document> collection = database.getCollection("paint_order");

            Document doc1 = new Document("color", "red").append("qty", 5).append("_id", 3);
            Document doc2 = new Document("color", "purple").append("qty", 10).append("_id", 4);
            Document doc3 = new Document("color", "yellow").append("qty", 3).append("_id", 3);
            Document doc4 = new Document("color", "blue").append("qty", 8).append("_id", 6);
            List<Document> documents = Arrays.asList(doc1, doc2, doc3, doc4);
            List<Integer> insertedIds = new ArrayList<>();

            // Inserts sample documents and prints their "_id" values
            try {
                InsertManyResult result = collection.insertMany(documents);
                result.getInsertedIds().values()
                        .forEach(doc -> insertedIds.add(doc.asInt32().getValue()));
                System.out.println("Inserted documents with the following ids: " + insertedIds);

            // Prints a message if any exceptions occur during the operation and the "_id" values of inserted documents
            } catch(MongoBulkWriteException exception) {
                exception.getWriteResult().getInserts()
                        .forEach(doc -> insertedIds.add(doc.getId().asInt32().getValue()));
                System.out.println("A MongoBulkWriteException occurred, but there are " +
                        "successfully processed documents with the following ids: " + insertedIds);
            }
        }

//        Document canvas = new Document("item", "canvas")
//                .append("qty", 100)
//                .append("tags", singletonList("cotton"));
//        Document size = new Document("h", 28)
//                .append("2", 35.5)
//                .append("uom", "cm");
//        canvas.put("size", size);
//        collection.insertOne(canvas);

    }
}
