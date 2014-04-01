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
import com.atlassian.jira.rest.client.domain.Transition;
import com.atlassian.jira.rest.client.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

import localdb.DataManager;
import localdb.HibernateDataManager;
import localdb.Job;

public class JiraTransitioner {

	private URI jiraServerUri;
	private JiraRestClient restClient;
	public JiraTransitioner() throws URISyntaxException {
		jiraServerUri = new URI("https://jira.jhc.co.uk");
		AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		restClient = factory.createWithBasicHttpAuthentication(
				jiraServerUri, "infanten", "SagaN0ren");
	}

	public static void main(String[] args) throws ClassNotFoundException,
			URISyntaxException, InterruptedException, ExecutionException {
		new JiraTransitioner().transition(args[0], args[1]);
	}

	public void transition(String issueKey, String newStatus) throws ClassNotFoundException, URISyntaxException,
			InterruptedException, ExecutionException {
		IssueRestClient issueClient = restClient.getIssueClient();
		Issue issue = issueClient.getIssue(issueKey).get();
		final Iterable<Transition> transitions = issueClient.getTransitions(issue.getTransitionsUri()).claim();
		Transition transition = getTransitionByName(transitions, newStatus);
		if(transition==null)
			throw new IllegalArgumentException("No such transition: " +newStatus);
		System.out.println("Tranisitioning "+issueKey + " to '"+ newStatus + "'");
		issueClient.transition(issue, new TransitionInput(transition.getId())).claim();
		System.out.println("done");
		//todo - close client?
		System.exit(0);
		
	}
	private static Transition getTransitionByName(Iterable<Transition> transitions, String transitionName) {
		for (Transition transition : transitions) {
			if (transition.getName().equals(transitionName)) {
				return transition;
			}
		}
		return null;
	}
}
