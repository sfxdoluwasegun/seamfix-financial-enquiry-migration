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
		AcctEnqResp acctEnqResp = fwaccountEnquiryImpl.doAccountEnquiry("990", "2007622883");
		log.info("Request status:" + acctEnqResp.getStatus() + " Account aname:" + acctEnqResp.getData().getAccountname());
	}

}
