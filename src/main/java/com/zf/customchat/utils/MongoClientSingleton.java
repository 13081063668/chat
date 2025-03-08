package com.zf.customchat.utils;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoClientSingleton {
    private volatile static MongoClient INSTANCE;

    private MongoClientSingleton() {}

    public static MongoClient getInstance() {
        if (INSTANCE == null) {
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
            INSTANCE = MongoClients.create(settings);
        }
        return INSTANCE;
    }
}
