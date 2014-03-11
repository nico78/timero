package localdb;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateDataManager implements DataManager {

	private SessionFactory sessionFactory;
	
	/* (non-Javadoc)
	 * @see localdb.DataManager#init()
	 */
	@Override
	public void init(){
		sessionFactory = new Configuration()
        .configure() // configures settings from hibernate.cfg.xml
        .buildSessionFactory();
	}
	
	
	/* (non-Javadoc)
	 * @see localdb.DataManager#save(java.lang.Object)
	 */
	@Override
	public  void save(Object obj) {
		Session session = openSession();
		session.beginTransaction();
		session.saveOrUpdate(obj);
		session.getTransaction().commit();
		session.close();
	}

	/* (non-Javadoc)
	 * @see localdb.DataManager#save(java.lang.Object)
	 */
	@Override
	public void save(Object... objs){
		Session session = openSession();
		session.beginTransaction();
		for(Object obj:objs){
			session.saveOrUpdate(obj);
		}
		session.getTransaction().commit();
		session.close();
	}
	
	
	
	/* (non-Javadoc)
	 * @see localdb.DataManager#getJobByReference(java.lang.String)
	 */
	@Override
	public  Job getJobByReference(String reference) {
		Session session = openSession();
		Query query = session.createQuery("from Job where reference = :reference");
		query.setParameter("reference", reference);
		Job job = (Job) query.uniqueResult();
		session.close();
		return job;		
	}

	private Session openSession() {
		if(sessionFactory==null)
			throw new IllegalStateException("Session factory not initialized");
		Session session = sessionFactory.openSession();
		return session;
	}
	
	/* (non-Javadoc)
	 * @see localdb.DataManager#getJobs(java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public  List<Job> getJobs(String filter){
		Session session = openSession();
		Query query = session.createQuery("from Job where description like :descFilter or reference like :referenceFilter");
		query.setParameter("descFilter", like(filter));
		query.setParameter("referenceFilter", like(filter));
		List<Job> list = (List<Job>)query.list();
		session.close();
		return list;
	}

	private static String like(String filter) {
		return "%"+filter+"%";
	}

	/* (non-Javadoc)
	 * @see localdb.DataManager#getTasksByDescription(java.lang.String)
	 */
	@Override
	public Task getTasksByDescription(String filter) {//temporary - will get rid of
		Session session = openSession();
		Query query = session.createQuery("from Task where taskDescription like :descFilter");
		query.setParameter("descFilter",like(filter));
		Task uniqueResult = (Task) query.uniqueResult();
	//	session.close();
		return uniqueResult;
	}

	/* (non-Javadoc)
	 * @see localdb.DataManager#getAllJobs()
	 */
	@Override
	public List<Job> getAllJobs() {
		Session session = openSession();
		Query query = session.createQuery("from Job ");
		List<Job> list = (List<Job>) query.list();
		session.close();
		return list;
	}

	
	
}
