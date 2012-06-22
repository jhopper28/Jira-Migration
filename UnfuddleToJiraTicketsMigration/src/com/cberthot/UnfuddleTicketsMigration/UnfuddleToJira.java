package com.cberthot.UnfuddleTicketsMigration;

/* Author: Charline Berthot
 * 
 * Code extended from: http://gabenell.blogspot.com/2010/05/migrating-unfuddle-tickets-to-jira.html
 * 
 */

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class UnfuddleToJira {

	private final Document backup;
	private final String extension;

	private int ticketCreated;

	public static Map<String, String> projectList;
	public static Map<String, String> versions;
	public static Map<String, String> milestones;
	public static Map<String, String> people;
	public static Map<String, String> components;
	public static Map<String, String> severities;
	public static Map<String, String> fieldValues;
	public static ArrayList<Page> pages;
	public static ArrayList<String> watchs;
	public static ArrayList<String> projects;
	public static int nbrAtt;

	public UnfuddleToJira(Document backup, String extension) {

		this.backup = backup;
		this.extension = extension;

		UnfuddleToJira.milestones = Others.parseMilestones(backup);
		UnfuddleToJira.versions = Others.parseVersions(backup);
		UnfuddleToJira.people = Others.parsePeople(backup);
		UnfuddleToJira.components = Others.parseComponents(backup);
		UnfuddleToJira.severities = Others.parseSeverities(backup);
		UnfuddleToJira.projectList = Others.parseProjects(backup);
		UnfuddleToJira.pages = Page.parsePages(backup);
		UnfuddleToJira.fieldValues = Others.parseFieldValue(backup);

		UnfuddleToJira.watchs = new ArrayList<String>();
		UnfuddleToJira.nbrAtt = 0;
		UnfuddleToJira.projects = new ArrayList<String>();

	}

	public void writeCsv(String attachmentURL) throws Exception {

		// get all the tickets
		NodeList ticketNodes = backup.getElementsByTagName("ticket");
		List<Ticket> tickets = Ticket.parseTickets(ticketNodes);

		Csv csv = new Csv();
		ScriptsToRun scripts = new ScriptsToRun(extension);

		PrintStream csvOutput = new PrintStream(new FileOutputStream(
				"csvOutput" + projects.get(0) + ".csv"), true, "UTF-8");
		PrintStream runActions = new PrintStream(new FileOutputStream(
				"atlassian/actions" + projects.get(0) + ".txt"), true, "UTF-8");
		PrintStream renameAttachments = new PrintStream(new FileOutputStream(
				"renameAttachments" + projects.get(0) + "." + extension), true,
				"UTF-8");
		PrintStream logTimeEntries = new PrintStream(new FileOutputStream(
				"atlassian/timeEntries" + projects.get(0) + "." + extension),
				true, "UTF-8");

		// write headers
		csvOutput.println(csv.writeCsvHeader().toString());

		logTimeEntries.println(scripts.writeHeader(extension).toString());
		renameAttachments.println(scripts.writeHeader(extension).toString());

		// Write outputs for each ticket in order of ticket number
		Collections.sort(tickets);
		for (String project : projects) {
			System.out.println(project);
			ticketCreated = 0;
			for (Ticket ticket : tickets) {
				// create an empty ticket to make sure the issue number and the
				// ticket number stay the same
				if (ticket.projectId.equals(project)) {
					ticketCreated += 1;
					while (ticketCreated != Integer.parseInt(ticket.number)) {
						csvOutput.println(csv.writeCsvEmptyRow().toString());
						runActions.println(scripts.deleteIssue(
								ticket.projectId.toUpperCase(), ticketCreated));
						ticketCreated += 1;
					}

					if (ticket.status.equals("closed"))
						if (ticket.resolution.equals(""))
							ticket.resolution = "fixed";

					// create the "real" ticket and the actions related to it
					csvOutput.println(csv.writeCsvRow(ticket, attachmentURL)
							.toString());
					if ((ticket.associatedTickets.size() > 0)
							|| (ticket.watchers.size() > 0))
						runActions.println(scripts.writeActionsToRun(ticket,
								ticket.projectId.toUpperCase()).toString());
					if (ticket.attachments.size() > 0)
						renameAttachments.println(scripts
								.writeRenameAttachments(ticket, extension));
					if (ticket.timeEntries.size() > 0)
						logTimeEntries.println(scripts.writeTimeEntries(ticket,
								ticket.projectId.toUpperCase()).toString());
				}
			}
			// create html pages from the notebooks
			Collections.sort(pages);
			for (Page page : pages)
				Page.wikiToHtml(page, project);
		}

		runActions.close();
		renameAttachments.close();
		csvOutput.close();
		logTimeEntries.close();

	}

	public static void main(String[] args) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		if (args.length != 2) {
			System.err
					.println("Wrong arguments number :"
							+ "First argument: Please choose > bat or > sh as argument depending on your OS"
							+ "Second argument: Enter the URL where your attachments are located + /");
			return;
		}

		String backupFile = "backup.xml";
		String extension = args[0];
		String attachmentURL = args[1];
		UnfuddleToJira converter = new UnfuddleToJira(factory
				.newDocumentBuilder().parse(backupFile), extension);

		converter.writeCsv(attachmentURL);

		System.out.println("Done!");
		return;
	}

}