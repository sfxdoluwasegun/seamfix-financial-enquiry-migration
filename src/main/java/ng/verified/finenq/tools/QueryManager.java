package ng.verified.finenq.tools;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;

import org.jboss.logging.Logger;

import ng.verified.jpa.Client;
import ng.verified.jpa.Client_;
import ng.verified.jpa.Overrides;
import ng.verified.jpa.Overrides_;
import ng.verified.jpa.Settings;
import ng.verified.jpa.Settings_;
import ng.verified.jpa.Wallet;
import ng.verified.jpa.Wallet_;
import ng.verified.jpa.Wrapper;
import ng.verified.jpa.Wrapper_;
import ng.verified.jpa.enums.SettingsType;

@Stateless
public class QueryManager {
	
	private Logger log = Logger.getLogger(getClass());

	private CriteriaBuilder criteriaBuilder ;

	@PersistenceContext
	private EntityManager entityManager ;

	@PostConstruct
	public void init(){
		criteriaBuilder = entityManager.getCriteriaBuilder();
	}
	
	/**
	 * Get standard charge for Wrapper invocation by Wrapper API key.
	 * 
	 * @param api_key
	 * @return charge. Returns zero(0) if operation is unsuccessful
	 */
	public BigDecimal getStandardAPIWrapperCharge(String api_key) {
		// TODO Auto-generated method stub
	
		CriteriaQuery<BigDecimal> criteriaQuery = criteriaBuilder.createQuery(BigDecimal.class);
		Root<Wrapper> root = criteriaQuery.from(Wrapper.class);
		
		criteriaQuery.select(root.get(Wrapper_.charge));
		criteriaQuery.where(criteriaBuilder.equal(root.get(Wrapper_.key), api_key));
		
		try {
			return entityManager.createQuery(criteriaQuery).getSingleResult();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.warn("No Wrapper exists with Key:" + api_key);
		}
		
		return BigDecimal.ZERO;
	}
	
	/**
	 * Get preferenceCharge by Client clienId and Wrapper key properties.
	 * 
	 * @param userid
	 * @param api_key
	 * @return preferenceCharge. Returns zero(0) if operation is unsuccessful
	 */
	public BigDecimal getClientPreferenceChargeForAPIRequest(long userid, 
			String api_key) {
		// TODO Auto-generated method stub
		
		CriteriaQuery<BigDecimal> criteriaQuery = criteriaBuilder.createQuery(BigDecimal.class);
		Root<Overrides> root = criteriaQuery.from(Overrides.class);
		
		Join<Overrides, Client> client = root.join(Overrides_.client);
		Join<Overrides, Wrapper> wrapper = root.join(Overrides_.wrapper);
		
		criteriaQuery.select(root.get(Overrides_.value));
		criteriaQuery.where(criteriaBuilder.and(
				criteriaBuilder.equal(client.get(Client_.clientId), userid), 
				criteriaBuilder.equal(wrapper.get(Wrapper_.key), api_key))
			);
		
		try {
			return entityManager.createQuery(criteriaQuery).getSingleResult();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.warn("No Override exists for clientid:" + userid + " and ApiKey:" + api_key);
		}
	
		return BigDecimal.ZERO;
	}
	
	/**
	 * Get Client balance in Wallet.
	 * 
	 * @param userid
	 * @return balance. Returns zero(0) if operation is unsuccessful
	 */
	public BigDecimal getClientWalletBalance(long userid) {
		// TODO Auto-generated method stub
		
		CriteriaQuery<BigDecimal> criteriaQuery = criteriaBuilder.createQuery(BigDecimal.class);
		Root<Client> root = criteriaQuery.from(Client.class);
		
		Join<Client, Wallet> wallet = root.join(Client_.wallet);
		
		criteriaQuery.select(wallet.get(Wallet_.balance));
		criteriaQuery.where(criteriaBuilder.equal(root.get(Client_.clientId), userid));
		
		try {
			return entityManager.createQuery(criteriaQuery).getSingleResult();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.warn("No Client exists with clientid:" + userid);
		}
		
		return BigDecimal.ZERO;
	}
	
	/**
	 * Get list of allowed APIs for Client by Client user id.
	 * 
	 * @param userid
	 * @return list. Returns null if operation is unsuccessful
	 */
	public Set<String> getAPIListByClient(long userid) {
		// TODO Auto-generated method stub
		
		CriteriaQuery<Wrapper> criteriaQuery = criteriaBuilder.createQuery(Wrapper.class);
		Root<Client> root = criteriaQuery.from(Client.class);
		
		SetJoin<Client, Wrapper> wrapps = root.join(Client_.wrappers);
		
		criteriaQuery.select(wrapps);
		criteriaQuery.where(criteriaBuilder.equal(root.get(Client_.clientId), userid));
		
		try {
			List<Wrapper> wrappers = entityManager.createQuery(criteriaQuery).getResultList();
			Set<String> keys = new HashSet<>();
			
			for (Wrapper wrapper : wrappers){
				keys.add(wrapper.getKey());
			}
			
			return keys;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.warn("No Client exists with clientId:" + userid);
		}
	
		return null;
	}
	
	/**
	 * Get primaryKey for Client by clientId property.
	 * 
	 * @param userid
	 * @return long. Returns zero(0) if operation is unsuccessful
	 */
	public long getClientByUserid(Long userid) {
		// TODO Auto-generated method stub
		
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Client> root = criteriaQuery.from(Client.class);
		
		criteriaQuery.select(root.get(Client_.pk));
		criteriaQuery.where(criteriaBuilder.equal(root.get(Client_.clientId), userid));
		
		try {
			return entityManager.createQuery(criteriaQuery).getSingleResult();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.warn("");
		}
	
		return 0;
	}
	
	/**
	 * Fetch {@link Settings} by name property.
	 * 
	 * @param name
	 * @return
	 */
	public Settings getSettingsByName(String name){

		CriteriaQuery<Settings> criteriaQuery = criteriaBuilder.createQuery(Settings.class);
		Root<Settings> root = criteriaQuery.from(Settings.class);

		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.get(Settings_.name), name));

		try {
			return entityManager.createQuery(criteriaQuery).getSingleResult();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("No Setting instance found with name:" + name);
		}

		return null;
	}
	
	/**
	 * Compute charge to applied to Client for Wrapper invocation.
	 * 
	 * @param userid
	 * @param api_key
	 * @return charge
	 */
	public BigDecimal getClientChargeForAPIRequest(long userid, String api_key) {
		// TODO Auto-generated method stub

		BigDecimal preferenceCharge = getClientPreferenceChargeForAPIRequest(userid, api_key);

		if (preferenceCharge.compareTo(BigDecimal.ZERO) == 0)
			preferenceCharge = getStandardAPIWrapperCharge(api_key);

		return preferenceCharge;
	}
	
	/**
	 * Subtract Wrapper invocation charge from Clients current balance in Wallet.
	 * 
	 * @param userid
	 * @param charge
	 */
	public void DebitClientWalletWithCharge(long userid, 
			BigDecimal charge) {
		// TODO Auto-generated method stub
		
		CriteriaQuery<Wallet> criteriaQuery = criteriaBuilder.createQuery(Wallet.class);
		Root<Client> root = criteriaQuery.from(Client.class);
		
		Join<Client, Wallet> wallet = root.join(Client_.wallet);
		
		criteriaQuery.select(wallet);
		criteriaQuery.where(criteriaBuilder.equal(root.get(Client_.clientId), userid));
		
		Wallet aWallet;
		
		try {
			aWallet = entityManager.createQuery(criteriaQuery).getSingleResult();
			
			BigDecimal previousBalance = aWallet.getBalance();
			BigDecimal balance = aWallet.getBalance().subtract(charge);
			
			aWallet.setBalance(balance);
			aWallet.setLastModified(Timestamp.valueOf(LocalDateTime.now()));
			aWallet.setPreviousBalance(previousBalance);
			
			update(aWallet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.warn("No Client found with clientid:" + userid);
		}
	}
	
	/**
	 * Create a new Setting instance or return existing.
	 * 
	 * @param name
	 * @param value
	 * @param description
	 * @param settingType
	 * @return {@link Settings}
	 */
	public Settings createSettings(String name, 
			String value, String description, SettingsType settingsType){

		Settings settings = getSettingsByName(name);

		if (settings != null)
			return settings;

		settings = new Settings();
		settings.setDescription(description);
		settings.setName(name);
		settings.setSettingsType(settingsType);
		settings.setValue(value);

		return (Settings) create(settings);
	}
	
	/**
	 * Persist entity and add entity instance to {@link EntityManager}.
	 * 
	 * @param entity
	 * @return persisted entity instance
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public <T> Object create(T entity){

		entityManager.persist(entity);

		try {
			return entity;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}
		return null;
	}

	/**
	 * Merge the state of the given entity into the current {@link PersistenceContext}.
	 * 
	 * @param entity
	 * @return the managed instance that the state was merged to
	 */
	public <T> Object update(T entity){

		entityManager.merge(entity);
		try {
			return entity;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}

		return null;
	}

	/**
	 * Merge the state of the given entity into the current {@link PersistenceContext}.
	 * 
	 * @param entity
	 * @return the managed instance that the state was merged to
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public <T> Object updateWithNewTransaction(T entity){

		entityManager.merge(entity);
		try {
			return entity;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}

		return null;
	}

	/**
	 * Merge the state of the given entity with instance from static persistence.
	 * 
	 * @param entity
	 * @return refreshed instance
	 */
	public <T> Object refresh(T entity){

		entityManager.refresh(entity);
		try {
			return entity;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}

		return null;
	}

	/**
	 * Remove the entity instance.
	 * 
	 * @param entity
	 */
	public <T> void delete(T entity){
		try {
			entityManager.remove(entity);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("", e);
		}
	}

}