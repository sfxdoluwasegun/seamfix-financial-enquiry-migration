package ng.verified.finenq.tools;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;

import ng.verified.jpa.Settings;
import ng.verified.jpa.Settings_;
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