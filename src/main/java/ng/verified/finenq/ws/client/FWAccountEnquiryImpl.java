package ng.verified.finenq.ws.client;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

import ng.verified.finenq.jbeans.AcctEnqReq;
import ng.verified.finenq.jbeans.AcctEnqResp;
import ng.verified.finenq.jbeans.ApplicationBean;
import ng.verified.finenq.tools.HashUtil;

public class FWAccountEnquiryImpl {
	
	private Logger log = Logger.getLogger(getClass());
	
	@Inject
	private HashUtil hashUtil ;
	
	@Inject 
	private ApplicationBean appBean ;
	
	/**
	 * Account names can be resolved by calling the ‘Resolve Account’ API
	 * 
	 * @see https://www.flutterwave.com/documentation/account-enquiry/
	 * 
	 * @param destbankcode
	 * @param recipientaccount
	 * @return {@link AcctEnqResp} representing JSON formatted bean. Returns null if operation is unsuccessful
	 */
	public AcctEnqResp doAccountEnquiry(String destbankcode, 
			String recipientaccount){
		
		destbankcode = encryptionAlgorithmForRequestParams(destbankcode);
		recipientaccount = encryptionAlgorithmForRequestParams(recipientaccount);
		
		if (!validateEncryptedParams(destbankcode, recipientaccount)) return null;
		
		Client client = null;
		AcctEnqResp acctEnqResp = null;
		AcctEnqReq accEnqReq = new AcctEnqReq(destbankcode, recipientaccount, appBean.getFw_merchantid());
		
		try {
			client = ClientBuilder.newClient();
			acctEnqResp = client.target(appBean.getAcct_enq_edp()).path("/pay/resolveaccount")
					.request(MediaType.APPLICATION_JSON)
					.post(Entity.json(accEnqReq), AcctEnqResp.class);
			
			return acctEnqResp;
		} catch (WebApplicationException e) {
			log.error("", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}finally{
			if (client != null)
				client.close();
		}
		
		return null;
	}

	/**
	 * Confirm encrypted parameters is valid string.
	 * 
	 * @param params
	 * @return true if valid.
	 */
	private boolean validateEncryptedParams(String...params) {
		// TODO Auto-generated method stub
		
		for (String param : params){
			if (param == null || param.isEmpty())
				return false;
		}
		
		return true;
	}

	/**
	 * Encrypt request parameter using recommended encryption algorithm.
	 * 
	 * @param param
	 * @return encrypted string. Returns null if operation is unsuccessful
	 */
	private String encryptionAlgorithmForRequestParams(String param) {
		// TODO Auto-generated method stub
		
		if (param == null) return null;
		
		try {
			return hashUtil.harden(param);
		} catch (InvalidKeyException | NoSuchAlgorithmException | UnsupportedEncodingException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}
		
		return null;
	}

}