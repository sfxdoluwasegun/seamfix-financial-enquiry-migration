package ng.verified.finenq.ws.wrapper;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import ng.verified.finenq.jbeans.AcctEnqResp;
import ng.verified.finenq.jbeans.AcctEnqWrapperReq;
import ng.verified.finenq.jbeans.FWData;
import ng.verified.finenq.jbeans.FinEnqResp;
import ng.verified.finenq.tools.DocumentManager;
import ng.verified.finenq.tools.WrapperUtils;
import ng.verified.finenq.ws.client.FWAccountEnquiryImpl;

@Path(value = "/acctenq")
public class AcctEnqWrapper {
	
	private Logger log = Logger.getLogger(getClass());

	@Inject
	private WrapperUtils wrapperUtils ;
	
	@Inject
	private DocumentManager documentManager ;
	
	@Inject
	private FWAccountEnquiryImpl fwaccountEnquiryImpl ;

	/**
	 * Account Enquiry wrapper service for 3rd party clients.
	 * {@link AcctEnqWrapper} represents properties to be received (as JSON) from a web client.
	 * Required properties include the following parameters: account number, bank code, email, API key and client user id.
	 * 
	 * @param acctEnqWrapperReq
	 * @return {@link FinEnqResp}
	 */
	@POST
	@Path(value = "/wrapper")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public FinEnqResp doAccountEnquiryHandshake(AcctEnqWrapperReq acctEnqWrapperReq){

		String uniquereference = "";
		String accountnumber = "";

		if (acctEnqWrapperReq == null) return initializeResponse(Status.BAD_REQUEST, accountnumber, uniquereference);
		
		try {
			String referenceNo = wrapperUtils.generateTransactionRef();
			Future<String> status = documentManager.createTransactionAndLog(acctEnqWrapperReq.getUserid(), acctEnqWrapperReq.getKey(), acctEnqWrapperReq.getAccountnumber(), 
					acctEnqWrapperReq.getEmail(), acctEnqWrapperReq.getBankcode(), referenceNo);
			uniquereference = status.get();
		} catch (InterruptedException | ExecutionException e1) {
			// TODO Auto-generated catch block
			log.error("", e1);
		}

		if (!wrapperUtils.validateRequestParams(acctEnqWrapperReq.getAccountnumber(), acctEnqWrapperReq.getBankcode(), acctEnqWrapperReq.getEmail(), acctEnqWrapperReq.getKey(), acctEnqWrapperReq.getUserid()))
			return initializeResponse(Status.BAD_REQUEST, accountnumber, uniquereference);

		accountnumber = acctEnqWrapperReq.getAccountnumber();

		if (!wrapperUtils.validateRequestEmail(acctEnqWrapperReq.getEmail())) return initializeResponse(Status.BAD_REQUEST, accountnumber, uniquereference);
		if (wrapperUtils.validateUserid(acctEnqWrapperReq.getUserid())) return initializeResponse(Status.UNAUTHORIZED, accountnumber, uniquereference);

		try {
			documentManager.createIdentity(acctEnqWrapperReq.getEmail(), acctEnqWrapperReq.getAccountnumber());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("", e);
			return initializeResponse(Status.INTERNAL_SERVER_ERROR, accountnumber, uniquereference);
		}
		
		if (!wrapperUtils.confirmClientAPIAccess(acctEnqWrapperReq.getKey(), acctEnqWrapperReq.getUserid())) return initializeResponse(Status.UNAUTHORIZED, accountnumber, uniquereference);
		if (!wrapperUtils.confirmClientCredit(acctEnqWrapperReq.getKey(), acctEnqWrapperReq.getUserid())) return initializeResponse(Status.PAYMENT_REQUIRED, accountnumber, uniquereference);
		
		try {
			AcctEnqResp acctEnqResp = fwaccountEnquiryImpl.doAccountEnquiry(acctEnqWrapperReq.getBankcode(), accountnumber);
			if (acctEnqResp == null) return initializeResponse(Status.SERVICE_UNAVAILABLE, accountnumber, uniquereference);
			
			wrapperUtils.sendEmailNotification(acctEnqWrapperReq.getUserid(), acctEnqWrapperReq.getKey(), acctEnqWrapperReq.getAccountnumber(), acctEnqResp.getData().getAccountname());
			
			documentManager.updateIdentity(acctEnqWrapperReq.getEmail(), accountnumber, acctEnqResp.getData().getAccountname());
			wrapperUtils.handlePostRequestOps(acctEnqWrapperReq.getUserid(), uniquereference, acctEnqResp.getData().getResponsemessage(), acctEnqResp.getData().getAccountname(), acctEnqWrapperReq.getKey());
			
			return initializeResponse(Status.OK, new FWData(acctEnqResp.getData().getResponsemessage(), uniquereference, acctEnqResp.getData().getAccountname(), accountnumber));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("", e);
			return initializeResponse(Status.INTERNAL_SERVER_ERROR, accountnumber, uniquereference);
		}
	}

	/**
	 * Construct response POJO from {@link Status} parameters.
	 * 
	 * @param status
	 * @param accountnumber
	 * @param uniquereference
	 * @return {@link FinEnqResp}
	 */
	private FinEnqResp initializeResponse(Status status, 
			String accountnumber, String uniquereference) {
		// TODO Auto-generated method stub

		documentManager.updateTransactionLog(uniquereference, status.getReasonPhrase(), status.getStatusCode());
		return new FinEnqResp(new FWData(uniquereference, accountnumber), status.getStatusCode(), status.getReasonPhrase());
	}

	/**
	 * Construct response POJO from {@link Status} parameters and validity status.
	 * 
	 * @param status
	 * @param fwData
	 * @return {@link FinEnqResp}
	 */
	private FinEnqResp initializeResponse(Status status, 
			FWData fwData) {
		// TODO Auto-generated method stub

		return new FinEnqResp(fwData, status.getStatusCode(), status.getReasonPhrase());
	}

}