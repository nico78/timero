package localdb;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.*;
import java.io.File;
import java.util.Date;
import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;

public class JobHibernateTest {

	private static DataManager dataManager;

	@BeforeClass
	public static void init() {
		
		dataManager = new HibernateDataManager();
		dataManager.init();
		Job job1 = new Job("221736", "SippCentre ViewModel issue", "FIG");

		Job job2 = new Job("221654", "I11 AJBELL Web trading down issue", "FIG");
		
		Job job3 = new Job("ADM","Admin","LOC");
		
		Task dev = new Task(job1, "Development");
		Task releaseNotes = new Task(job1, "Release notes");
		
		ActivityRecord actRecord = new ActivityRecord(releaseNotes, now());
		actRecord.setStartTime(now());
		actRecord.setEndTime(now());

		dataManager.save(job1, job2, job3, dev, releaseNotes,actRecord);

	}

	private static Date now() {
		return new Date();
	}

	@Test
	public void jobRetrievableByReference() {
		Job chkJob = dataManager.getJobByReference("221736");
		assertEquals("FIG", chkJob.getSource());
		assertEquals("SippCentre ViewModel issue", chkJob.getDescription());
	}

	@Test
	public void jobsRetrievableByDescriptionFilter(){
		List<Job> filtered = dataManager.getJobs("issue");
		assertThat(filtered, containsInAnyOrder(hasReference("221736"),hasReference("221654") ));
	}

	@Test
	public void jobsRetrievableByReferenceFilter(){
		List<Job> filtered = dataManager.getJobs("21");
		assertThat(filtered, containsInAnyOrder(hasReference("221736"),hasReference("221654") ));
	}
	
	@Test
	public void taskRetrievable(){
		Task task = dataManager.getTasksByDescription("Development");
//		Job ownerJob = task.getJob();
//		assertThat(ownerJob, hasReference("221736"));
	}
	
	
	
	private Matcher<Object> hasReference(String ref) {
		return hasProperty("reference",equalTo(ref));
	}
	
	
	
	
}