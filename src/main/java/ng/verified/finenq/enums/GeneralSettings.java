package ng.verified.finenq.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum GeneralSettings {
	
	FLWV_TMERCHANTID("FlutterWave Test MerchantID", "tk_JudwNKR08h", "Account merchant ID for FlutterWave authentication on staging"), 
	FLWV_ACCTENQ_TEDP("FlutterWave AccountEnquiry Test Endpoint", "http://staging1flutterwave.co:8080/pwc/rest", "Web service endoint for test account enquiry invocations"), 
	FLWV_TAPIKEY("FlutterWave Test API KEY", "tk_Cxq8n5Lp3zOXGBEwskdD", "Required key for FlutterWave API test implementation and invocations");
	
	private String name ;
	private String description ;
	private String value ;
	
	/**
	 * Instantiates a new general settings.
	 *
	 * @param name the name
	 * @param value the value
	 * @param description the description
	 */
	private GeneralSettings(String name, 
			String value, String description){
		
		this.setDescription(description);
		this.setName(name);
		this.setValue(value);
	}
	
	/**
	 * From name.
	 *
	 * @param name the name
	 * @return the general settings
	 */
	public static GeneralSettings fromName(String name){
		if (name != null && !name.isEmpty())
			for (GeneralSettings generalSettings : GeneralSettings.values()){
				if (generalSettings.getName().equalsIgnoreCase(name))
					return generalSettings;
			}
		
		return null;
	}
	
	/**
	 * Literals.
	 *
	 * @return the list
	 */
	public static List<String> literals(){
		List<String> literals = new ArrayList<String>();

		for (GeneralSettings generalSettings : GeneralSettings.values()){
			literals.add(generalSettings.getName());
		}
		
		Collections.sort(literals, new Comparator<String>() {
			public int compare(String a, String b){
				return a.compareToIgnoreCase(b);
			}
		});
		
		return literals;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}