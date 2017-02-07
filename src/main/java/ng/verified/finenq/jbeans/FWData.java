package ng.verified.finenq.jbeans;

public class FWData {
	
	private String responsemessage ;
	private String responsecode ;
	private String uniquereference ;
	private String accountname ;
	private String accountnumber ;
	private String phonenumber ;
	private String internalreference ;
	
	public FWData() {
		// TODO Auto-generated constructor stub
	}
	
	public FWData(String responsemessage, 
			String uniquereference, String accountname, 
			String accountnumber) {
		// TODO Auto-generated constructor stub
		
		this.responsemessage = responsemessage;
		this.uniquereference = uniquereference;
		this.accountname = accountname;
		this.accountnumber = accountnumber;
	}
	
	public FWData(String uniquereference, 
			String accountnumber) {
		// TODO Auto-generated constructor stub
		
		this.uniquereference = uniquereference;
		this.accountnumber = accountnumber;
	}
	
	public String getResponsemessage() {
		return responsemessage;
	}
	
	public void setResponsemessage(String responsemessage) {
		this.responsemessage = responsemessage;
	}

	public String getResponsecode() {
		return responsecode;
	}

	public void setResponsecode(String responsecode) {
		this.responsecode = responsecode;
	}

	public String getUniquereference() {
		return uniquereference;
	}

	public void setUniquereference(String uniquereference) {
		this.uniquereference = uniquereference;
	}

	public String getAccountname() {
		return accountname;
	}

	public void setAccountname(String accountname) {
		this.accountname = accountname;
	}

	public String getAccountnumber() {
		return accountnumber;
	}

	public void setAccountnumber(String accountnumber) {
		this.accountnumber = accountnumber;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public String getInternalreference() {
		return internalreference;
	}

	public void setInternalreference(String internalreference) {
		this.internalreference = internalreference;
	}
	

}
