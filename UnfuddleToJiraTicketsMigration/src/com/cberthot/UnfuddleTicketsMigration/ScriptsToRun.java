package com.cberthot.UnfuddleTicketsMigration;

public class ScriptsToRun {

	public String extension;

	public ScriptsToRun(String extension) {
		this.extension = extension;
	}

	public StringBuilder writeHeader(String extension) {
		StringBuilder builder = new StringBuilder(256);
		if (extension.equals("bat"))
			builder.append("@echo off");
		else if (extension.equals("sh"))
			builder.append("#!/bin/bash");
		else
			System.err.println("Wrong extension argument!");
		return builder;
	}

	public StringBuilder deleteIssue(String projectId, int issueId) {
		StringBuilder builder = new StringBuilder(256);
		builder.append("--action deleteIssue --issue \"");
		builder.append(projectId);
		builder.append("-");
		builder.append(issueId);
		builder.append("\"");
		return builder;
	}

	public StringBuilder writeActionsToRun(Ticket ticket, String projectId) {
		StringBuilder builder = new StringBuilder(256);
		// associated ticket
		for (int i = 0; i < ticket.associatedTickets.size(); i++) {
			String relationship = ticket.associatedTickets.get(i).relationship;

			int firstTick = 0;
			int secondTick = 0;
			int k = Integer.parseInt(ticket.associatedTickets.get(i).number);

			if (Integer.parseInt(ticket.number) < k) {

				if (relationship.equals("child")) {
					firstTick = k;
					secondTick = Integer.parseInt(ticket.number);
				} else {
					firstTick = Integer.parseInt(ticket.number);
					secondTick = k;
				}

				builder.append("--action linkIssue --issue \"");
				builder.append(projectId);
				builder.append("-");
				builder.append(firstTick);
				builder.append("\"");
				builder.append(" --toIssue \"");
				builder.append(projectId);
				builder.append("-");
				builder.append(secondTick);
				builder.append("\"");
				builder.append(" --link \"");

				if (relationship.equals("related"))
					builder.append("Relates\"");
				if (relationship.equals("sibling"))
					builder.append("Relates\" ");
				if ((relationship.equals("parent"))
						|| (relationship.equals("child")))
					builder.append("Blocks\" ");
				if (relationship.equals("duplicate"))
					builder.append("Duplicate\"");

				builder.append("\n");
			}
		}

		// subscription
		for (int i = 0; i < ticket.watchers.size(); i++) {
			if (ticket.watchers.get(i).username != null) {

				builder.append("--action addWatchers --issue \"");
				builder.append(projectId);
				builder.append("-");
				builder.append(ticket.number);
				builder.append("\" --userId \"");
				builder.append(ticket.watchers.get(i).username);
				builder.append("\"");
				if (i + 1 < ticket.watchers.size())
					builder.append("\n");
			}
		}
		return builder;
	}

	public StringBuilder writeTimeEntries(Ticket ticket, String projectId) {
		StringBuilder builder = new StringBuilder(256);

		// timeEntries
		String status = ticket.status;
		String issue = projectId + "-" + ticket.number;
		String call = "";
		if (extension.equals("bat"))
			call = "CALL jira";
		else if (extension.equals("sh"))
			call = "./jira";
		else
			System.err.println("Wrong extension argument!");

		if (ticket.timeEntries.size() > 0) {
			if (status.equals("closed"))
				builder.append(call + "." + extension
						+ " --action progressIssue --issue \"" + issue
						+ "\" --step \"3\"\n");
			for (int i = 0; i < ticket.timeEntries.size(); i++) {

				builder.append(call + "2." + extension
						+ " --action addWork --issue \"");
				builder.append(issue);
				builder.append("\" --timeSpent \"");
				builder.append(ticket.timeEntries.get(i).hours);
				builder.append("h\" --date \"");
				String date = ticket.timeEntries.get(i).date;
				String newDate = "";
				newDate += date.substring(8, 10);
				newDate += "/";
				newDate += date.substring(5, 7);
				newDate += "/";
				newDate += date.substring(2, 4);
				builder.append(newDate);
				builder.append("\" --user ");
				builder.append(Csv.lookupUser(ticket.timeEntries.get(i).personId));
				builder.append(" --comment \"");
				builder.append(ticket.timeEntries.get(i).description);
				builder.append("\" --autoAdjust");
				builder.append("\n");

			}
			if (status.equals("closed"))
				builder.append(call + "." + extension
						+ " --action progressIssue --issue \"" + issue
						+ "\" --step \"2\"");
		}

		return builder;
	}

	// attachments
	public String writeRenameAttachments(Ticket ticket, String extension) {

		StringBuilder builder = new StringBuilder(256);

		String command = "";
		if (extension.equals("bat"))
			command = "ren ";
		else if (extension.equals("sh"))
			command = "mv ";
		else
			System.err.println("Wrong extension argument!");

		for (int i = 0; i < ticket.attachments.size(); i++) {
			builder.append(command);
			builder.append(ticket.attachments.get(i).id);
			builder.append(" ");
			builder.append(ticket.attachments.get(i).fullName);
			if (i + 1 < ticket.attachments.size())
				builder.append("\n");
		}

		return builder.toString();

	}

}
