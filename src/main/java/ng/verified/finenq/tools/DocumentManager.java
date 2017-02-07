package ng.verified.finenq.tools;

import java.util.Arrays;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.bson.Document;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Projections;

import ng.verified.finenq.enums.documents.AppDocuments;
import ng.verified.finenq.enums.documents.ClientInvocations;
import ng.verified.finenq.enums.documents.Transactions;

@Stateless
public class DocumentManager {
	
	@Inject
	private MongoManager mongoManager ;

	/**
	 * Confirms if transaction reference already exists.
	 * 
	 * @param referenceNo
	 * @return true if exists
	 */
	public boolean getInvocationWithTransactionReference(String referenceNo) {
		// TODO Auto-generated method stub
		
		MongoCollection<Document> mongoCollection = mongoManager.getCollectionConnection(AppDocuments.invocations_db.name());

		AggregateIterable<Document> documents = mongoCollection.aggregate(Arrays.asList(new Document("$unwind", "$" + ClientInvocations.transactions.name()), 
				new Document("$match", new Document(ClientInvocations.transactions.name() + "." + Transactions.reference_no.name(), referenceNo)), 
				new Document("$project", Projections.include(ClientInvocations.transactions.name()))
				));
		
		return documents.iterator().hasNext();
	}

	public void createIdentity(String email, String accountnumber) {
		// TODO Auto-generated method stub
		
	}

	public Future<String> createTransactionAndLog(long userid, String key, String accountnumber, String email,
			String bankcode, String referenceNo) {
		// TODO Auto-generated method stub
		return null;
	}

}
