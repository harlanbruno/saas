package br.org.jira;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang.StringUtils;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.IssueRestClient.Expandos;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicVotes;
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup;
import com.atlassian.jira.rest.client.api.domain.ChangelogItem;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

import br.org.jira.business.CSVWriter;
import br.org.jira.domain.Status;
import br.org.jira.domain.StatusEn;

// @SpringBootApplication
public class JiraApplication {

	private String username;
	private String password;
	private String jiraUrl;
	private static JiraRestClient restClient;

	private JiraApplication(String username, String password, String jiraUrl) {
		this.username = username;
		this.password = password;
		this.jiraUrl = jiraUrl;
		restClient = getJiraRestClient();
	}

	public static void main(String[] args) throws IOException {
		// SpringApplication.run(BalanceteApplication.class, args);

		JiraApplication myJiraClient = new JiraApplication("xb204243", "Onvf!0404", "http://jira.produbanbr.corp");

		String project = "PPJWPBR";
		
		String jql = "project = " + project + " AND issuetype in standardIssueTypes() AND issueType != Epic"; // AND id = PPJWPBR-881";
		int maxPerQuery = 100;
		int startIndex = 0;
		List<br.org.jira.domain.Issue> issues = new ArrayList<>();

		try {
			SearchRestClient searchRestClient = restClient.getSearchClient();
			IssueRestClient issueRestClient = restClient.getIssueClient();
			
			while (true) {
				
				Promise<SearchResult> searchResult = searchRestClient.searchJql(jql, maxPerQuery, startIndex, null);
				SearchResult results = searchResult.claim();

				Expandos[] expandArr = new Expandos[] { Expandos.CHANGELOG };
				List<Expandos> expand = Arrays.asList(expandArr);

				for (Issue issue : results.getIssues()) {
					
					final Issue _issue = issueRestClient.getIssue(issue.getKey(), expand).claim();
					IssueType type = _issue.getIssueType();

					if (!type.isSubtask()) {
						
						// TODO: all
						br.org.jira.domain.Issue newIssue = new br.org.jira.domain.Issue();
						newIssue.setId(_issue.getKey());
						newIssue.setCreated(_issue.getCreationDate().toDate());
						newIssue.setDescription(_issue.getSummary());
						newIssue.setType(_issue.getIssueType().getName());
						newIssue.addStatus(new Status(_issue.getCreationDate().toDate(), StatusEn.TEAM_BACKLOG));
						
						String team = project;
						
						if(_issue.getSummary().indexOf("[") != -1 && _issue.getSummary().indexOf("]") != -1) {
							team = _issue.getSummary().substring(_issue.getSummary().indexOf("[")+1, _issue.getSummary().indexOf("]"));
						}
						
						newIssue.setTeam(team);
						
						System.out.println("----------------------------");
						System.out.println(_issue.getKey() + " - Type: " + type.getName());
						System.out.println("----------------------------");

						if (_issue.getChangelog() != null) {
							
							List<Status> status = new ArrayList<>();

							for (ChangelogGroup changelogGroup : _issue.getChangelog()) {
								for (ChangelogItem changelogItem : changelogGroup.getItems()) {
									if (StringUtils.equalsIgnoreCase(changelogItem.getField(), "status")) {

										LocalDateTime date = new java.sql.Timestamp(
												changelogGroup.getCreated().getMillis()).toLocalDateTime();
										DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
										String formatDateTime = date.format(formatter);
										System.out.println("Date: " + formatDateTime + " from '"
										// + changelogItem.getField() + "' of type '"
										// + changelogItem.getFieldType() + "' from value '"
										// + changelogItem.getFrom() + "' and string '"
												+ changelogItem.getFromString() + "' to '"
												// + changelogItem.getTo() + "' and string '"
												+ changelogItem.getToString() + "'");

										status.add(new Status(changelogGroup.getCreated().toDate(), StatusEn.getByDescription(changelogItem.getToString().toUpperCase())));
									}
								}
							}
							newIssue.addStatus(status);
						}
						
						issues.add(newIssue);
						System.out.println("----------------------------");
					}
				}

				if (startIndex >= results.getTotal()) {
					break;
				}

				startIndex += maxPerQuery;

				System.out.println("Fetching from Index: " + startIndex);
			}

		} finally {
			restClient.close();
		}
        
		try {
			
			CSVWriter writer = new CSVWriter();
			writer.write(issues);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String createIssue(String projectKey, Long issueType, String issueSummary) {

		IssueRestClient issueClient = restClient.getIssueClient();
		IssueInput newIssue = new IssueInputBuilder(projectKey, issueType, issueSummary).build();

		return issueClient.createIssue(newIssue).claim().getKey();
	}

	private Issue getIssue(String issueKey) {
		return restClient.getIssueClient().getIssue(issueKey).claim();
	}

	private void voteForAnIssue(Issue issue) {
		restClient.getIssueClient().vote(issue.getVotesUri()).claim();
	}

	private int getTotalVotesCount(String issueKey) {
		BasicVotes votes = getIssue(issueKey).getVotes();
		return votes == null ? 0 : votes.getVotes();
	}

	private void addComment(Issue issue, String commentBody) {
		restClient.getIssueClient().addComment(issue.getCommentsUri(), Comment.valueOf(commentBody));
	}

	private List<Comment> getAllComments(String issueKey) {
		return StreamSupport.stream(getIssue(issueKey).getComments().spliterator(), false).collect(Collectors.toList());
	}

	private void updateIssueDescription(String issueKey, String newDescription) {
		IssueInput input = new IssueInputBuilder().setDescription(newDescription).build();
		restClient.getIssueClient().updateIssue(issueKey, input).claim();
	}

	private void deleteIssue(String issueKey, boolean deleteSubtasks) {
		restClient.getIssueClient().deleteIssue(issueKey, deleteSubtasks).claim();
	}

	private JiraRestClient getJiraRestClient() {
		return new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(getJiraUri(), this.username,
				this.password);
	}

	private URI getJiraUri() {
		return URI.create(this.jiraUrl);
	}
}
