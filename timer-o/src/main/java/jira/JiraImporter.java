package jira;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.NullProgressMonitor;

import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.IssueRestClient.Expandos;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.SearchRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.Subtask;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

import localdb.DataManager;
import localdb.HibernateDataManager;
import localdb.Job;

public class JiraImporter {
	private static final String JIRA = "JIRA";
	DataManager dataManager;

	public static void main(String[] args) throws ClassNotFoundException,
			URISyntaxException, InterruptedException, ExecutionException {
		new JiraImporter().run();
	}

	public void run() throws ClassNotFoundException, URISyntaxException,
			InterruptedException, ExecutionException {
		AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		URI jiraServerUri = new URI("https://jira.jhc.co.uk");
		JiraRestClient restClient = factory.createWithBasicHttpAuthentication(
				jiraServerUri, "infanten", "SagaN0ren");
		SearchRestClient searchClient = restClient.getSearchClient();
		IssueRestClient issueClient = restClient.getIssueClient();
		dataManager = new HibernateDataManager();
		dataManager.init();

		Promise<SearchResult> searchJql = searchClient
				.searchJql("project = \"Jarvis FigaroWeb\" AND assignee = infanten AND type = Story and sprint = \"Sprint 1\"");

		SearchResult searchResult = searchJql.claim();
		Iterable<BasicIssue> issues = searchResult.getIssues();

		for (BasicIssue bsissue : issues) {
			System.out.println(bsissue.toString());
			Issue issue = issueClient.getIssue(bsissue.getKey()).get();
			String storyKey = issue.getKey();
			String storySummary = issue.getSummary();
			System.out.println(storyKey + " - " + issue.getSummary());
					
					
			Iterable<Subtask> subtasks = issue.getSubtasks();
			
			for (Subtask subtask : subtasks) {
				String subtaskSummary = subtask.getSummary();
				addJob(subtask.getIssueKey(), 
						JIRA, 
						storyKey + " " + storySummary + " -> "+ subtaskSummary);
			}
			addJob(storyKey,JIRA,"OTHER: " + storySummary);

		}
		System.out.println("done");

	}

	private void addJob(String reference, String source, String description) {
		Job job = dataManager.getJobByReference(reference);
		if (job == null) {
			job = new Job();
			job.setReference(reference);
		}
		job.setSource(source);
		job.setDescription(description);
		dataManager.save(job);
	}
}
