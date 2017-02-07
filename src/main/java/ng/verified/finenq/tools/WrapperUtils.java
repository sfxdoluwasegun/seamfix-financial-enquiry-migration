package ng.verified.finenq.tools;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.validator.routines.EmailValidator;

@Stateless
public class WrapperUtils {
	
	@Inject
	private QueryManager queryManager ;
	
	@Inject
	private DocumentManager documentManager;
	
	/**
	 * Validate request parameters.
	 * 
	 * @param params
	 * @return true if validation passes
	 */
	public boolean validateRequestParams(Object...params) {
		// TODO Auto-generated method stub

		for (Object param : params){
			if (param == null)
				return false;
		}

		return true;
	}
	
	/**
	 * Confirm email address complies with valid email expressions.
	 * 
	 * @param email
	 * @return true if valid
	 */
	public boolean validateRequestEmail(String email) {
		// TODO Auto-generated method stub

		return EmailValidator.getInstance().isValid(email);
	}

	/**
	 * Confirm user id is a non-zero value and belongs to a persisted client.
	 *  
	 * @param userid
	 * @return true if valid
	 */
	public boolean validateUserid(Long userid) {
		// TODO Auto-generated method stub

		if (userid == 0) return false;

		long id = queryManager.getClientByUserid(userid);
		if (id == 0) return false;

		return true;
	}
	
	/**
	 * Generate a unique referenceNo for a Transaction document.
	 * 
	 * @return referenceNo
	 */
	public String generateTransactionRef() {
		// TODO Auto-generated method stub

		String referenceNo = "VER|ACCTENQ|" + Timestamp.valueOf(LocalDateTime.now()).getTime();

		boolean exists = documentManager.getInvocationWithTransactionReference(referenceNo);

		while(exists){
			referenceNo = "VER|ACCTENQ|" + Timestamp.valueOf(LocalDateTime.now()).getTime();
			exists = documentManager.getInvocationWithTransactionReference(referenceNo);
		}

		return referenceNo;
	}

	/**
	 * Retrieve list of configured APIs for client and check requested API is in list.
	 * 
	 * @param key
	 * @param userid
	 * @return true if client has access for API invocations
	 */
	public boolean confirmClientAPIAccess(String key, 
			long userid) {
		// TODO Auto-generated method stub
		
		Set<String> apiKeys = queryManager.getAPIListByClient(userid);

		if (apiKeys != null && !apiKeys.isEmpty())
			for (String aKey : apiKeys){
				if (aKey.equalsIgnoreCase(key))
					return true;
			}

		return false;
	}

	/**
	 * Retrieve Client wallet balance and compare with service charge.
	 * 
	 * @param key
	 * @param userid
	 * @return true if client wallet balance isEqual to or greater than service charge
	 */
	public boolean confirmClientCredit(String key, long userid) {
		// TODO Auto-generated method stub
		
		BigDecimal balance = queryManager.getClientWalletBalance(userid);
		BigDecimal charge = queryManager.getClientChargeForAPIRequest(userid, key);

		if (charge.compareTo(balance) > 0)
			return false;

		return true;
	}

	public void sendEmailNotification(long userid, String key, String accountnumber, String accountname) {
		// TODO Auto-generated method stub
		
	}

}
