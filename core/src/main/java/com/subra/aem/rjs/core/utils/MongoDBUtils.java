package com.subra.aem.rjs.core.utils;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.exceptions.RJSCustomException;
import org.subra.commons.helpers.CommonHelper;

import java.io.UnsupportedEncodingException;

public class MongoDBUtils {

    public static final String PROTOCOL = "mongodb://";
    public static final String PROTOCOL_SRV = "mongodb+srv://";
    public static final String SRV_URL = "mongodb.net";
    public static final String RECEIVED_INVALID_MONGO_CLIENT = "Received invalid MongoClient...";
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBUtils.class);

    private MongoDBUtils() {
        throw new UnsupportedOperationException();
    }

    public static MongoClient connectMongoDB(final String url) throws RJSCustomException {
        if (url != null) {
            LOGGER.debug("Connecting to Mongo DB Server with URL:- {}", url);
            MongoClientURI uri = new MongoClientURI(url);
            return new MongoClient(uri);
        } else {
            throw new RJSCustomException(RECEIVED_INVALID_MONGO_CLIENT);
        }
    }

    public static MongoClient createConnection(String host, int port, String username, String password) throws RJSCustomException, UnsupportedEncodingException {
        final String url = createMongoURL(host, port, username, password);
        if (StringUtils.isEmpty(url)) {
            LOGGER.debug("Connecting to Mongo DB Server with URL:- {}", url);
            MongoClientURI uri = new MongoClientURI(url);
            return new MongoClient(uri);
        } else {
            throw new RJSCustomException(RECEIVED_INVALID_MONGO_CLIENT);
        }
    }

    public static void closeConnection(MongoClient client) {
        if (client != null)
            client.close();
    }

    public static MongoDatabase getMongoDB(final MongoClient client, final String dbName) throws RJSCustomException {
        if (client != null) {
            LOGGER.debug("Getting DB '{}' from '{}'", dbName, client.getAddress());
            return client.getDatabase(dbName);
        } else {
            throw new RJSCustomException(RECEIVED_INVALID_MONGO_CLIENT);
        }
    }

    public static MongoCollection<Document> getMongoCollection(final MongoDatabase db, final String collectionName) throws RJSCustomException {
        if (db != null) {
            LOGGER.debug("Getting collection '{}' from '{}' ", collectionName, db.getName());
            return db.getCollection(collectionName);
        } else {
            throw new RJSCustomException("Received invalid MongoDatabase...");
        }
    }

    public static MongoCollection<Document> getMongoCollection(final MongoClient client, final String dbName, final String collectionName) throws RJSCustomException {
        LOGGER.debug("Getting collection '{}' from '{}' ", collectionName, dbName);
        return getMongoDB(client, dbName).getCollection(collectionName);
    }

    public static String createMongoURL(String host, int port, String username, String password) throws UnsupportedEncodingException {
        // mongodb://localhost:27017
        // mongodb+srv://<username>:<password>@subra-6bdt2.mongodb.net/<dbname>?retryWrites=true&w=majority
        StringBuilder urlBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(host)) {
            if (host.contains(SRV_URL)) {
                urlBuilder.append(PROTOCOL_SRV).append(createUnamePwd(username, password)).append(host);
            } else {
                urlBuilder.append(PROTOCOL).append(createUnamePwd(username, password)).append(host).append(":").append(port);
            }
        }
        return urlBuilder.toString();
    }

    private static String createUnamePwd(String username, String password) throws UnsupportedEncodingException {
        String unamePwd = StringUtils.EMPTY;
        if (!StringUtils.isAllBlank(username, password)) {
            password = CommonHelper.encode(password, null);
            unamePwd = username + CommonHelper.COLON + password + CommonHelper.AT;
        }
        return unamePwd;
    }

}
