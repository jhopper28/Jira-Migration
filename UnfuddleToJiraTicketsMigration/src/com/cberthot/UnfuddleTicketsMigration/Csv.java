package com.cberthot.UnfuddleTicketsMigration;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.regex.*;

public class Csv {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat
			.forPattern("yyyyMMdd");

	public static String lookupUser(String id) {
		String person = UnfuddleToJira.people.get(id);
		/**
		 * Here you can transform a person's username if it changed between
		 * Unfuddle and JIRA. Eg: <tt> 
		 * if ("gabe".equals(person)) {
		 *     person = "gabenell";
		 * }
		 * </tt>
		 */
		return person;
	}

	public String lookupMilestone(String id) {
		return UnfuddleToJira.milestones.get(id);
	}

	public String lookupFieldValues(String id) {
		return UnfuddleToJira.fieldValues.get(id);
	}

	public static String lookupProject(String id) {
		return UnfuddleToJira.projectList.get(id);
	}

	public String lookupSeverity(String id) {
		return UnfuddleToJira.severities.get(id);
	}

	public String lookupComponent(String id) {
		return UnfuddleToJira.components.get(id);
	}

	public String lookupVersion(String id) {
		return UnfuddleToJira.versions.get(id);
	}

	
	public static String linkToIssue(String input, String projectId) {
		Pattern p = Pattern.compile("#[0-9]+");
		Matcher m = p.matcher(input);
		while (m.find()) {
			String id = input.substring(m.start() + 1, m.end());
			input = input.replaceFirst("#[0-9]+", projectId + "-" + id);
			m = p.matcher(input);
		}
		return input;
	}

	public static String prepareForCsv(String input) {
		if (input == null)
			return "";
		return "\"" + input.replace("\"", "\"\"") + "\"";
	}

	public String prepareComment(String date, String body, String author) {
		return "\"" + convertDate(date) + ";" + lookupUser(author) + ";"
				+ body.replace("\"", "\"\"") + "\"";
	}

	public static String convertDate(String input) {
		return DATE_FORMATTER.print(new DateTime(input));
	}

	public StringBuilder writeCsvHeader() {
		StringBuilder builder = new StringBuilder(256);
		builder.append("Id, ");
		builder.append("Number, ");
		builder.append("Summary, ");
		builder.append("ProjectId, ");
		builder.append("Status, ");
		builder.append("Priority, ");
		builder.append("Severity, ");
		builder.append("Component, ");
		builder.append("Assignee, ");
		builder.append("Reporter, ");
		builder.append("Resolution, ");
		builder.append("CreateTime, ");
		builder.append("DueDate, ");
		builder.append("HoursEstimateCurrent, ");
		builder.append("HoursEstimateInitial, ");
		builder.append("ResolveTime, ");
		builder.append("Milestone, ");
		builder.append("Field1, ");
		builder.append("Field2, ");
		builder.append("Field3, ");
		builder.append("Version, ");
		builder.append("Description, ");
		builder.append("resolutionDescription");
		for (int k = 1; k < Comment.maxComments + 1; k++) {
			builder.append(", Comment");
		}
		for (int k = 1; k < UnfuddleToJira.nbrAtt + 1; k++) {
			builder.append(", Attachment");
		}
		
		return builder;
	}

	public StringBuilder writeCsvRow(Ticket ticket, String attachmentURL) {
		StringBuilder builder = new StringBuilder(256);

		builder.append(prepareForCsv(ticket.id)).append(", ");
		builder.append(prepareForCsv(ticket.number)).append(", ");
		builder.append(prepareForCsv(ticket.summary)).append(", ");
		builder.append(prepareForCsv(lookupProject(ticket.projectId))).append(
				", ");
		builder.append(prepareForCsv(ticket.status)).append(", ");
		builder.append(prepareForCsv(ticket.priority)).append(", ");
		builder.append(prepareForCsv(lookupSeverity(ticket.severityId)))
				.append(", ");
		builder.append(prepareForCsv(lookupComponent(ticket.componentId)))
				.append(", ");
		builder.append(prepareForCsv(lookupUser(ticket.assigneeId))).append(
				", ");
		builder.append(prepareForCsv(lookupUser(ticket.reporterId))).append(
				", ");
		builder.append(prepareForCsv(ticket.resolution)).append(", ");
		builder.append(prepareForCsv(convertDate(ticket.createdTime))).append(
				", ");
		if (ticket.dueDate.equals(""))
			builder.append(", ");
		else
			builder.append(prepareForCsv(convertDate(ticket.dueDate))).append(
					", ");
		builder.append(prepareForCsv(ticket.hoursEstimateCurrent)).append(", ");
		builder.append(prepareForCsv(ticket.hoursEstimateInitial)).append(", ");
		String resolveTime = ticket.resolution != null ? convertDate(ticket.lastUpdateTime)
				: null;
		builder.append(prepareForCsv(resolveTime)).append(", ");
		builder.append(prepareForCsv(lookupMilestone(ticket.milestoneId)))
				.append(", ");
		if(ticket.field1 ==null)
			builder.append("Task").append(", ") ;
		else
			builder.append(prepareForCsv(lookupFieldValues(ticket.field1))).append(
				", ");
		builder.append(prepareForCsv(lookupFieldValues(ticket.field2)))
				.append(", ");
		builder.append(prepareForCsv(lookupFieldValues(ticket.field3)))
				.append(", ");
		builder.append(prepareForCsv(lookupVersion(ticket.versionId))).append(
				", ");
		builder.append(prepareForCsv(linkToIssue(ticket.description, ticket.projectId)));

		// JIRA doesn't have the notion of a resolution description
		if (ticket.resolutionDescription != null) {
			builder.append(",").append(
					prepareForCsv(linkToIssue(ticket.resolutionDescription,
							ticket.projectId)));
		}
		int nbr;
		// comments
		if (ticket.comments == null)
			nbr = 0;
		else
			nbr = ticket.comments.size();
		for (int k = 0; k < nbr; k++)
			builder.append(", ")
					.append(prepareComment(
							ticket.comments.get(k).createdTime,
							linkToIssue(ticket.comments.get(k).body, ticket.projectId),
							ticket.comments.get(k).authorId));

		for (int k = 0; k < Comment.maxComments - nbr; k++)
			builder.append(", ");
		// tickets attachments
		if (ticket.attachments == null)
			nbr = 0;
		else
			nbr = ticket.attachments.size();
		for (int k = 0; k < nbr; k++)
			builder.append(", ").append(
					prepareForCsv(attachmentURL + ticket.attachments.get(k).fullName));
		for (int k = 0; k < UnfuddleToJira.nbrAtt - nbr; k++)
			builder.append(", ");

		return builder;
	}

	public StringBuilder writeCsvEmptyRow() {
		StringBuilder builder = new StringBuilder(256);
		builder.append(",, To be deleted,");
		for (int i = 0; i < 20; i++) {
			builder.append(",");
		}
		for (int k = 1; k < Comment.maxComments + 1; k++) {
			builder.append(", ");
		}
		for (int k = 1; k < UnfuddleToJira.nbrAtt + 1; k++) {
			builder.append(", ");
		}

		return builder;
	}
}
