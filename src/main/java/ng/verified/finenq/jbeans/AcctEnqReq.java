package ng.verified.finenq.jbeans;

public class AcctEnqReq {
	
	private String destbankcode ;
	private String recipientaccount ;
	private String merchantid ;
	
	public AcctEnqReq() {
		// TODO Auto-generated constructor stub
	}
	
	public AcctEnqReq(String destbankcode, 
			String recipientaccount, String merchantid) {
		// TODO Auto-generated constructor stub
		
		this.destbankcode = destbankcode;
		this.merchantid = merchantid;
		this.recipientaccount = recipientaccount;
	}
	
	public String getDestbankcode() {
		return destbankcode;
	}
	
	public void setDestbankcode(String destbankcode) {
		this.destbankcode = destbankcode;
	}

	public String getRecipientaccount() {
		return recipientaccount;
	}

	public void setRecipientaccount(String recipientaccount) {
		this.recipientaccount = recipientaccount;
	}

	public String getMerchantid() {
		return merchantid;
	}

	public void setMerchantid(String merchantid) {
		this.merchantid = merchantid;
	}

}
