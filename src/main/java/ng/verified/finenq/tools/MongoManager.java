package ng.verified.finenq.tools;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class MongoManager {
	
	private MongoClient mongoClient ;
	
	@PostConstruct
	public void run(){
		
		MongoCredential.createCredential("", "", "".toCharArray());
		mongoClient = new MongoClient();
	}
	
	@PreDestroy
	public void stop(){
		
		mongoClient.close();
	}
	
	/**
	 * Get connection to {@link MongoDatabase}.
	 * 
	 * @return mongoClient
	 */
	public MongoClient getMongoClient(){
		
		return mongoClient;
	}
	
	/**
	 * Get MongoDatabase connection for CAVEVA database to use in querying collections.
	 * 
	 * @param databaseName
	 * @return {@link MongoDatabase}
	 */
	public MongoDatabase getDatabaseConnection(){
		
		return mongoClient.getDatabase("idswitch");
	}
	
	/**
	 * Get MongoDatabase connection for use in querying collections.
	 * 
	 * @param databaseName
	 * @return {@link MongoDatabase}
	 */
	public MongoDatabase getDatabaseConnection(String databaseName){
		
		return mongoClient.getDatabase(databaseName);
	}
	
	/**
	 * Get MongoCOllection connection for use in querying documents.
	 * 
	 * @param mongoDatabase
	 * @param collectionName
	 * @return {@link MongoCollection}
	 */
	public MongoCollection<Document> getCollectionConnection(MongoDatabase mongoDatabase, 
			String collectionName){
		
		return mongoDatabase.getCollection(collectionName);
	}
	
	/**
	 * Get MongoCOllection connection for use in querying documents.
	 * 
	 * @param mongoDatabase
	 * @param collectionName
	 * @return {@link MongoCollection}
	 */
	public MongoCollection<Document> getCollectionConnection(String collectionName){
		
		return getDatabaseConnection().getCollection(collectionName);
	}
	
	/**
	 * Update MongoDocument by filter.
	 * 
	 * @param mongoCollection
	 * @param document - original copy of document to be updated
	 * @param modifications - BSON containing changes to effected in document
	 */
	public void updateDocument(MongoCollection<Document> mongoCollection, 
			Document document, Bson modifications){
		
		Bson filter = new Document("_id", document.getObjectId("_id"));
		Bson update = new Document("$set", modifications);
		
		mongoCollection.updateOne(filter, update);
	}
	
	/**
	 * Add UNIQUE constraint to fields in collection.
	 * 
	 * @param mongoCollection
	 * @param fields
	 */
	public void createUniqueConstraint(MongoCollection<Document> mongoCollection, 
			String...fields){
		
		List<IndexModel> indexes = new LinkedList<>();
		
		for (String field : fields){
			
			IndexModel indexModel = new IndexModel(Indexes.ascending(field), new IndexOptions().unique(true).name(field.toUpperCase() + "_UNIQUE"));
			indexes.add(indexModel);
		}
		
		mongoCollection.createIndexes(indexes);
	}
	
	/**
	 * MongoDB provides text indexes to support text search of string content. 
	 * Text indexes can include any field whose value is a string or an array of string elements. 
	 * A compound index can be created incorporating a text index 
	 * but it’s important to note there can only be one text index on a collection.
	 * @see http://mongodb.github.io/morphia/1.3/guides/indexing/
	 * 
	 * @param mongoCollection
	 * @param fields
	 */
	public void createTextIndex(MongoCollection<Document> mongoCollection, 
			String field){
		
		mongoCollection.createIndex(Indexes.text(field), new IndexOptions().name(field.toUpperCase() + "_TEXT"));
	}

}