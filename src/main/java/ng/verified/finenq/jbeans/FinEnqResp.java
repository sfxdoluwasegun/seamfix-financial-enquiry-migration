package ng.verified.finenq.jbeans;

public class FinEnqResp {
	
	private int code ;
	private String status ;
	
	private FWData description ;
	
	public FinEnqResp() {
		// TODO Auto-generated constructor stub
	}
	
	public FinEnqResp(FWData description, 
			int code, String status) {
		// TODO Auto-generated constructor stub
		
		this.code = code;
		this.description = description;
		this.status = status;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public FWData getDescription() {
		return description;
	}

	public void setDescription(FWData description) {
		this.description = description;
	}

}
