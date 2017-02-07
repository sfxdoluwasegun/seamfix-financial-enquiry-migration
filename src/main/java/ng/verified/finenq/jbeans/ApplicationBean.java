package ng.verified.finenq.jbeans;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;

import ng.verified.finenq.enums.GeneralSettings;
import ng.verified.finenq.tools.QueryManager;
import ng.verified.jpa.Settings;
import ng.verified.jpa.enums.SettingsType;

@Singleton
public class ApplicationBean {

	private String fw_key ;
	private String fw_merchantid ;
	private String acct_enq_edp ;
	
	@Inject
	private QueryManager queryManager;

	@PostConstruct
	public void init(){

		Settings settings = null;

		for (GeneralSettings generalSettings : GeneralSettings.values()){
			if (generalSettings.getName().equalsIgnoreCase(GeneralSettings.FLWV_TAPIKEY.getName())){
				settings = queryManager.createSettings(GeneralSettings.FLWV_TAPIKEY.getName(), 
						GeneralSettings.FLWV_TAPIKEY.getValue(), GeneralSettings.FLWV_TAPIKEY.getDescription(), SettingsType.GENERAL);
				this.fw_key = settings.getValue();
			}else if (generalSettings.getName().equalsIgnoreCase(GeneralSettings.FLWV_TMERCHANTID.getName())){
				settings = queryManager.createSettings(GeneralSettings.FLWV_TMERCHANTID.getName(), 
						GeneralSettings.FLWV_TMERCHANTID.getValue(), GeneralSettings.FLWV_TMERCHANTID.getDescription(), SettingsType.GENERAL);
				this.fw_merchantid = settings.getValue();
			}else if (generalSettings.getName().equalsIgnoreCase(GeneralSettings.FLWV_ACCTENQ_TEDP.getName())){
				settings = queryManager.createSettings(GeneralSettings.FLWV_ACCTENQ_TEDP.getName(), 
						GeneralSettings.FLWV_ACCTENQ_TEDP.getValue(), GeneralSettings.FLWV_ACCTENQ_TEDP.getDescription(), SettingsType.GENERAL);
				this.acct_enq_edp = settings.getValue();
			}
		}
	}
	
	public String getFw_key() {
		return fw_key;
	}

	public void setFw_key(String fw_key) {
		this.fw_key = fw_key;
	}

	public String getFw_merchantid() {
		return fw_merchantid;
	}

	public void setFw_merchantid(String fw_merchantid) {
		this.fw_merchantid = fw_merchantid;
	}

	public String getAcct_enq_edp() {
		return acct_enq_edp;
	}

	public void setAcct_enq_edp(String acct_enq_edp) {
		this.acct_enq_edp = acct_enq_edp;
	}
	
}
