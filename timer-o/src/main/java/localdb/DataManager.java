package localdb;

import java.util.Date;
import java.util.List;

public interface DataManager {

	public abstract void init();

	public abstract void save(Object obj);

	public abstract void save(Object... objs);

	public abstract Job getJobByReference(String reference);

	public abstract List<Job> getJobs(String filter);

	public abstract Task getTasksByDescription(String filter);

	public abstract List<Job> getAllJobs();

	public abstract List<Task> getSuggestedTasksForJob(Job job);

	public abstract void close();

	public abstract List runQuery(String queryString);

	public abstract List<ActivityRecord> activity(Date fromDate, Date toDate);

	public abstract void deleteActivity(ActivityRecord activity);

	void refresh(Object... objs);

}