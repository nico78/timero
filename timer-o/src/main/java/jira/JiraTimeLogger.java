package jira;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.IssueRestClient.Expandos;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.SearchRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.Subtask;
import com.atlassian.jira.rest.client.domain.Transition;
import com.atlassian.jira.rest.client.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.domain.input.WorklogInput;
import com.atlassian.jira.rest.client.domain.input.WorklogInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.google.common.base.Joiner;

import core.DateUtil;

import localdb.ActivityRecord;
import localdb.DataManager;
import localdb.HibernateDataManager;
import localdb.Job;

public class JiraTimeLogger {
	private static final String JIRA = "JIRA";
	DataManager dataManager;

	private URI jiraServerUri;
	private JiraRestClient restClient;
	private boolean preview;
	public JiraTimeLogger(boolean preview) throws URISyntaxException {
		this.preview = preview;
		jiraServerUri = new URI("https://jira.jhc.co.uk");
		AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		restClient = factory.createWithBasicHttpAuthentication(
				jiraServerUri, "infanten", "SagaN0ren");
		dataManager = new HibernateDataManager();
		dataManager.init();
	}

	public static void main(String[] args) throws ClassNotFoundException,
			URISyntaxException, InterruptedException, ExecutionException {
		boolean preview = args.length==1 && (args[0].equals("preview"));
		new JiraTimeLogger(preview).logTime(DateUtil.today(), DateUtil.tomorrow());
	}

	private static class ActivitySummary {
		private Job job;
		private int durationMins;
		private Date earliestStartDate;
		private Set<String> taskDescs = new HashSet<>();
		
		public ActivitySummary(ActivityRecord activity) {
			this.job = activity.getTask().getJob();
			this.durationMins = activity.getDurationSecs() / 60;
			this.earliestStartDate = activity.getStartTime();
			taskDescs.add(activity.getTask().getTaskDescription());
		}
		
		public Job getJob() {
			return job;
		}
		
		public int getDurationMins() {
			return durationMins;
		}
		
		public Date getEarliestStartDate() {
			return earliestStartDate;
		}
		
		public void addActivity(ActivityRecord activity){
			if(activity.getStartTime().before(this.earliestStartDate))
				this.earliestStartDate = new Date(activity.getStartTime().getTime());
			
			this.durationMins+=activity.getDurationSecs()/60;
			taskDescs.add(activity.getTask().getTaskDescription());
		}
		public String getTasks(){
			return Joiner.on(", ").join(taskDescs);
		}

		@Override
		public String toString() {
			return "ActivitySummary [job=" + job + ", durationMins="
					+ durationMins + ", earliestStartDate=" + earliestStartDate
					+  ", getTasks()=" + getTasks()
					+ "]";
		}
		
		
		
	}
	private List<ActivitySummary> getSummaryActivitites(Date fromDate, Date toDate){
		List<ActivityRecord> activities = dataManager.activity(fromDate, toDate);
		Map<String,ActivitySummary> summaries = new HashMap<>();
		for(ActivityRecord activity:activities){
			Job parentJob = activity.getTask().getJob();
			if(parentJob.getSource().equals(JIRA)){
				String reference = parentJob.getReference();
				ActivitySummary summary = summaries.get(reference);
				if(summary!=null){
					summary.addActivity(activity);
				}
				else {
					summary = new ActivitySummary(activity);
					summaries.put(reference, summary);
				}
			}
		}
		List<ActivitySummary> summaryList= new ArrayList<>();
		summaryList.addAll(summaries.values());
		return summaryList;
	}
	
	public void logTime(Date fromDate, Date toDate) throws ClassNotFoundException, URISyntaxException,
			InterruptedException, ExecutionException {

		List<ActivitySummary> summaries = getSummaryActivitites(fromDate, toDate);

		
		IssueRestClient issueClient = restClient.getIssueClient();
		 for(ActivitySummary summary:summaries){
			 System.out.println(summary);
			String issueKey = summary.getJob().getReference();
			Issue issue = issueClient.getIssue(issueKey).get();
			WorklogInput worklogInput = workLogFor(issue)
					.setAdjustEstimateLeave()
					.setMinutesSpent(summary.getDurationMins())
					.setStartDate(new DateTime(summary.getEarliestStartDate()))
					.setComment(summary.getTasks())										
					.build();
			if(preview)
				System.out.println("PREVIEW ONLY");
			else {
				System.out.println("Logging: " + worklogInput);
				issueClient.addWorklog(issue.getWorklogUri(), worklogInput).claim();
			}
		}
		
		
		System.out.println("done");
		//todo - close client?
		
		
		System.exit(0);
		
	}
	

	private WorklogInputBuilder workLogFor(Issue issue) {
		return new WorklogInputBuilder(issue.getSelf());
	}
}
