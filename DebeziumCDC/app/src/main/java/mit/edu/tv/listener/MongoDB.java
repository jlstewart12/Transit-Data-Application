package mit.edu.tv.listener;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoDB {

    public void  testConnection() {
        String connectionString = "mongodb://some-mongo:27017";
        try {
	    MongoClient mongoClient = MongoClients.create(connectionString); 
            List<Document> databases = mongoClient.listDatabases().into(new ArrayList<>());
            databases.forEach(db -> System.out.println(db.toJson()));
        } catch(Exception e)
	    {
	    }

    }

    public void insertRecord(String record)
    {
        String connectionString = "mongodb://some-mongo:27017";
	try {
        // Add code here
	} catch (Exception e)
	{
	}
    }
}
