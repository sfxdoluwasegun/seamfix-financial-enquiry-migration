package ng.verified.finenq.tools;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.jboss.logging.Logger;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Projections;

import ng.verified.finenq.enums.documents.AppDocuments;
import ng.verified.finenq.enums.documents.BankAccounts;
import ng.verified.finenq.enums.documents.ClientInvocations;
import ng.verified.finenq.enums.documents.Identity;
import ng.verified.finenq.enums.documents.TransactionLog;
import ng.verified.finenq.enums.documents.Transactions;

@Stateless
public class DocumentManager {

	private Logger log = Logger.getLogger(getClass());

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

	/**
	 * Create new Identity document. 
	 * This document contains data used for identification of a unique individual.
	 * 
	 * @param email
	 * @param accountnumber
	 */
	@Asynchronous
	public void createIdentity(String email, String accountnumber) {
		// TODO Auto-generated method stub

		MongoCollection<Document> mongoCollection = mongoManager.getCollectionConnection(AppDocuments.identity_db.name());

		Document document = null;
		Bson filter = new Document(Identity.email.name(), email);

		try {
			document = mongoCollection.find(filter).first();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			log.error("", e1);
		}

		if (document == null)
			persistNewIdentityDocument(mongoCollection, accountnumber, email);
		else
			updateExistingIdentityDocument(accountnumber, document, filter, mongoCollection);
	}

	/**
	 * Persist new Identity document with embedded BankAccount document.
	 * 
	 * @param mongoCollection
	 * @param accountnumber
	 * @param email
	 */
	private void persistNewIdentityDocument(MongoCollection<Document> mongoCollection, 
			String accountnumber, String email) {
		// TODO Auto-generated method stub

		List<Document> bankaccounts = new LinkedList<>();

		Document bankAccount = new Document(BankAccounts.accountnumber.name(), accountnumber);
		bankaccounts.add(bankAccount);

		Document document = new Document(Identity.email.name(), email)
				.append(Identity.bankaccts.name(), bankaccounts);

		try {
			mongoCollection.insertOne(document);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}
	}

	/**
	 * Fetch embedded BankAccount document list from Identity document.
	 * If embedded document list doesn't exist, create new embedded document for accountNumber and append in list to Identity document.
	 * Confirm existence of BankAccount document for accountNumber argument within embedded list.
	 * If embedded document for accountNumber doesn't exist within list, create new embedded document and add to list.
	 * 
	 * @param accountnumber
	 * @param document
	 * @param filter
	 * @param mongoCollection
	 */
	private void updateExistingIdentityDocument(String accountnumber, 
			Document document, Bson filter, 
			MongoCollection<Document> mongoCollection){

		@SuppressWarnings("unchecked") List<Document> bankaccounts = (List<Document>) document.get(Identity.bankaccts.name());
		if (bankaccounts == null || bankaccounts.isEmpty()){
			bankaccounts = new LinkedList<>();

			Document bankAccount = new Document(BankAccounts.accountnumber.name(), accountnumber);
			bankaccounts.add(bankAccount);
		}else{
			boolean exists = false;

			for (Document bankAccount : bankaccounts){
				String account = bankAccount.getString(BankAccounts.accountnumber.name());
				if (account != null 
						&& !account.isEmpty() 
						&& account.equalsIgnoreCase(accountnumber)){
					exists = true;
					break;
				}
			}

			if (!exists){
				Document bankAccount = new Document(BankAccounts.accountnumber.name(), accountnumber);
				bankaccounts.add(bankAccount);
				document.replace(Identity.bankaccts.name(), bankaccounts);

				mongoCollection.replaceOne(filter, document);
			}
		}
	}

	/**
	 * Create Transaction and TransactionLog documents. 
	 * The Transaction document represents a single invocation on our wrapper service by a client.
	 * The TransactionLog document contains a breakdown of details for a Transaction document.
	 * 
	 * @param userid
	 * @param key
	 * @param accountnumber
	 * @param email
	 * @param bankcode
	 * @param referenceNo
	 * @return referenceNo. If operation fails an empty string is returned
	 */
	@Asynchronous
	public Future<String> createTransactionAndLog(long userid, String key, String accountnumber, String email,
			String bankcode, String referenceNo) {
		// TODO Auto-generated method stub

		MongoCollection<Document> mongoCollection = mongoManager.getCollectionConnection(AppDocuments.invocations_db.name());

		String refNo = createTransaction(mongoCollection, userid, key, referenceNo);
		if (refNo == null)
			return new AsyncResult<String>("");

		createTransactionLog(accountnumber, email, referenceNo, "", userid);

		return new AsyncResult<String>(referenceNo);
	}

	/**
	 * Create new TransactionLog document. 
	 * Log should contain embedded document containing fields received from wrapper request.
	 * 
	 * @param accountnumber
	 * @param email
	 * @param referenceNo
	 * @param flag
	 * @param userid
	 */
	private void createTransactionLog(String accountnumber, 
			String email, String referenceNo, String flag, 
			long userid) {
		// TODO Auto-generated method stub

		MongoCollection<Document> mongoCollection = mongoManager.getCollectionConnection(AppDocuments.transaction_log_db.name());

		Bson request = new Document("accountnumber", accountnumber)
				.append("email", email);

		Document document = new Document(TransactionLog.request.name(), request)
				.append(TransactionLog.timestamp.name(), LocalDateTime.now())
				.append(TransactionLog.transaction_ref.name(), referenceNo)
				.append(TransactionLog.redundant_flag.name(), flag);

		try {
			mongoCollection.insertOne(document);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}
	}

	/**
	 * Create new Transaction document.
	 * Document should clearly indicate serviced status and wrapper which is being invoked.
	 * 
	 * @param mongoCollection
	 * @param userid
	 * @param api_key
	 * @param referenceNo
	 * @return referenceNo. If operation fails a null is returned
	 */
	public String createTransaction(MongoCollection<Document> mongoCollection, 
			long userid, String api_key, String referenceNo){
		// TODO Auto-generated method stub

		Document invocation = mongoCollection.find(new Document(ClientInvocations.userid.name(), userid)).first();

		if (invocation == null)
			return null;

		@SuppressWarnings("unchecked") List<Document> transactions = (List<Document>) invocation.get(ClientInvocations.transactions.name());

		if (transactions == null)
			transactions = new ArrayList<>();

		Document transaction = new Document(Transactions.txn_time.name(), LocalDateTime.now())
				.append(Transactions.api_key.name(), api_key)
				.append(Transactions.is_serviced.name(), false)
				.append(Transactions.reference_no.name(), referenceNo);

		transactions.add(transaction);

		Bson modification = new Document(ClientInvocations.transactions.name(), transactions);

		try {
			mongoManager.updateDocument(mongoCollection, invocation, modification);
			return referenceNo;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}

		return null;
	}

	/**
	 * Update Identity document with accountName parameter post successful FlutterWave invocation.
	 * 
	 * @param email
	 * @param accountnumber
	 * @param accountname
	 */
	@Asynchronous
	public void updateIdentity(String email, 
			String accountnumber, String accountname) {
		// TODO Auto-generated method stub

		MongoCollection<Document> mongoCollection = mongoManager.getCollectionConnection(AppDocuments.identity_db.name());

		Document document = null;
		Bson filter = new Document(Identity.email.name(), email);

		try {
			document = mongoCollection.find(filter).first();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			log.error("", e1);
		}
		
		if (document == null)
			return;
		
		@SuppressWarnings("unchecked") List<Document> bankaccounts = (List<Document>) document.get(Identity.bankaccts.name());
		if (bankaccounts != null && !bankaccounts.isEmpty())
			for (Document bankacct : bankaccounts){
				if (bankacct.getString(BankAccounts.accountnumber).equalsIgnoreCase(accountnumber) 
						&& (bankacct.getString(BankAccounts.accountname.name()) == null || bankacct.getString(BankAccounts.accountname.name()).isEmpty())){
					bankacct.append(BankAccounts.accountname.name(), accountname);
					
					mongoCollection.replaceOne(filter, document);
					break;
				}
			}
	}

	/**
	 * Update value of is_serviced property of document after attempting processing of Wrapper invocation.
	 * 
	 * @param userid
	 * @param referenceNo
	 * @param serviced
	 */
	public void updateTransaction(long userid, 
			String referenceNo, boolean serviced) {
		// TODO Auto-generated method stub

		MongoCollection<Document> mongoCollection = mongoManager.getCollectionConnection(AppDocuments.invocations_db.name());

		Document filter = new Document(ClientInvocations.userid.name(), userid);

		try {
			Document document = mongoCollection.find(filter).first();
			if (document != null){
				@SuppressWarnings("unchecked") List<Document> documents = (List<Document>) document.get(ClientInvocations.transactions.name());
				if (documents != null && !documents.isEmpty())
					for (Document transaction : documents){
						if (transaction.getString(Transactions.reference_no).equalsIgnoreCase(referenceNo)){
							transaction.replace(Transactions.is_serviced.name(), serviced);

							mongoCollection.replaceOne(filter, document);
							break;
						}
					}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}
	}

	/**
	 * Add Wrapper response parameters to TransactionLog document.
	 * 
	 * @param referenceNo
	 * @param requestStatus
	 * @param validity
	 */
	public void updateTransactionLog(String referenceNo, 
			String responsemessage, String accountname) {
		// TODO Auto-generated method stub

		MongoCollection<Document> mongoCollection = mongoManager.getCollectionConnection(AppDocuments.transaction_log_db.name());

		Document filter = new Document(TransactionLog.transaction_ref.name(), referenceNo);

		try {
			Document document = mongoCollection.find(filter).first();
			if (document == null)
				return;

			Document response = new Document("responsemessage", responsemessage)
					.append("accountname", accountname);

			Bson modification = new Document(TransactionLog.response.name(), response);

			mongoManager.updateDocument(mongoCollection, filter, modification);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}
	}

	/**
	 * Update transactionLog document with error response.
	 * 
	 * @param uniquereference
	 * @param reasonPhrase
	 * @param statusCode
	 */
	public void updateTransactionLog(String uniquereference, 
			String reasonPhrase, int statusCode) {
		// TODO Auto-generated method stub

		MongoCollection<Document> mongoCollection = mongoManager.getCollectionConnection(AppDocuments.transaction_log_db.name());

		Document filter = new Document(TransactionLog.transaction_ref.name(), uniquereference);

		try {
			Document document = mongoCollection.find(filter).first();
			if (document == null)
				return;

			Document response = new Document("responsemessage", reasonPhrase)
					.append("errorcode", statusCode);

			Bson modification = new Document(TransactionLog.response.name(), response);

			mongoManager.updateDocument(mongoCollection, filter, modification);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}
	}

}