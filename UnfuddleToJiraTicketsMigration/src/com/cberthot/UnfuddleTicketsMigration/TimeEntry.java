package com.cberthot.UnfuddleTicketsMigration;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TimeEntry {

	public String createdTime;
	public String date;
	public String description;
	public String hours;
	public String id;
	public String personId;
	public String ticketId;
	public String updatedAt;

	public static ArrayList<TimeEntry> parseTimeEntries(Node ticketsTimeEntries) {
		Element elem = (Element) ticketsTimeEntries;
		ArrayList<TimeEntry> timeEntries = new ArrayList<TimeEntry>();
		NodeList TimeEntriesList = elem.getElementsByTagName("time-entry");

		int nbr = TimeEntriesList.getLength();

		for (int j = 0; j < nbr; j++) {
			Node node = TimeEntriesList.item(j);

			Element nodeElem = (Element) node;
			TimeEntry timeEntry = new TimeEntry();
			NodeList timeEntryElements = nodeElem.getChildNodes();
			for (int k = 0; k < timeEntryElements.getLength(); k++) {
				Node timeEntrySubNode = timeEntryElements.item(k);
				String nodeName = timeEntrySubNode.getNodeName();
				if ("id".equals(nodeName)) {
					timeEntry.id = timeEntrySubNode.getTextContent();
				} else if ("description".equals(nodeName)) {
					timeEntry.description = timeEntrySubNode.getTextContent();
				} else if ("person-id".equals(nodeName)) {
					timeEntry.personId = timeEntrySubNode.getTextContent();

					if (!UnfuddleToJira.watchs.contains(timeEntry.personId))
						UnfuddleToJira.watchs.add(timeEntry.personId);

				} else if ("created-at".equals(nodeName)) {
					timeEntry.createdTime = timeEntrySubNode.getTextContent();
				} else if ("ticket-id".equals(nodeName)) {
					timeEntry.ticketId = timeEntrySubNode.getTextContent();
				} else if ("date".equals(nodeName)) {
					timeEntry.date = timeEntrySubNode.getTextContent();
				} else if ("hours".equals(nodeName)) {
					timeEntry.hours = timeEntrySubNode.getTextContent();
				} else if ("updated-at".equals(nodeName)) {
					timeEntry.updatedAt = timeEntrySubNode.getTextContent();
				}
			}

			timeEntries.add(timeEntry);
		}

		return timeEntries;
	}
}