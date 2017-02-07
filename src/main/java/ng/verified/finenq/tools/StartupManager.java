package ng.verified.finenq.tools;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import ng.verified.finenq.jbeans.AcctEnqResp;
import ng.verified.finenq.ws.client.FWAccountEnquiryImpl;

@Startup
@Singleton
public class StartupManager {
	
	private Logger log = Logger.getLogger(getClass());
	
	@Inject
	private FWAccountEnquiryImpl fwaccountEnquiryImpl ;
	
	@PostConstruct
	public void start(){
		
		log.info("STARTING UP FINENQ SERVICE");
		AcctEnqResp acctEnqResp = fwaccountEnquiryImpl.doAccountEnquiry("044", "0690000031");
		log.info("Request status:" + acctEnqResp.getStatus() + " Account aname:" + acctEnqResp.getData().getAccountname() + " PhoneNo:" + acctEnqResp.getData().getPhonenumber() 
				+ " RespCode:" + acctEnqResp.getData().getResponsecode() + " RespMessage:" + acctEnqResp.getData().getResponsemessage() 
				+ " UniqueRef:" + acctEnqResp.getData().getUniquereference());
	}

}
