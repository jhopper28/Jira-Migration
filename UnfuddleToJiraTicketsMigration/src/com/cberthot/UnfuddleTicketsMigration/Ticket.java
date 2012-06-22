package com.cberthot.UnfuddleTicketsMigration;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Ticket implements Comparable<Ticket> {

	public String assigneeId;
	public String componentId;
	public String createdTime;
	public String description;
	public String dueDate;
	public String field1;
	public String field2;
	public String field3;
	public String hoursEstimateCurrent;
	public String hoursEstimateInitial;
	public String id;
	public String milestoneId;
	public String number;
	public String priority;
	public String projectId;
	public String projectName;
	public String reporterId;
	public String resolution;
	public String resolutionDescription;
	public String severityId;
	public String status;
	public String summary;
	public String lastUpdateTime;
	public String versionId;
	public ArrayList<Attachment> attachments;
	public ArrayList<Comment> comments;
	public ArrayList<Subscription> watchers;
	public ArrayList<AssociatedTicket> associatedTickets;
	public ArrayList<TimeEntry> timeEntries;

	@Override
	public int compareTo(Ticket other) {
		return Integer.parseInt(number) - Integer.parseInt(other.number);
	}

	public static ArrayList<Ticket> parseTickets(NodeList ticketNodes) {
		ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		ArrayList<String> projects = new ArrayList<String>();

		for (int i = 0; i < ticketNodes.getLength(); i++) {
			Node node = ticketNodes.item(i);

			// to make sure that the ticket isn't an associated ticket
			String parentNodeName = node.getParentNode().getNodeName();
			if (!"associated-tickets".equals(parentNodeName)) {
				Element nodeElem = (Element) node;
				Ticket ticket = new Ticket();
				NodeList ticketElements = nodeElem.getChildNodes();
				for (int j = 0; j < ticketElements.getLength(); j++) {
					Node ticketSubNode = ticketElements.item(j);
					String nodeName = ticketSubNode.getNodeName();

					if ("id".equals(nodeName)) {
						ticket.id = ticketSubNode.getTextContent();
					} else if ("status".equals(nodeName)) {
						ticket.status = ticketSubNode.getTextContent();
					} else if ("summary".equals(nodeName)) {
						ticket.summary = ticketSubNode.getTextContent();
					} else if ("description".equals(nodeName)) {
						ticket.description = ticketSubNode.getTextContent();
					} else if ("milestone-id".equals(nodeName)) {
						ticket.milestoneId = ticketSubNode.getTextContent();
					} else if ("field1-value-id".equals(nodeName)) { // field-value
						ticket.field1 = ticketSubNode.getTextContent();
					} else if ("field2-value-id".equals(nodeName)) { // field-value
						ticket.field2 = ticketSubNode.getTextContent();
					} else if ("field3-value-id".equals(nodeName)) { // field-value
						ticket.field3 = ticketSubNode.getTextContent();
					} else if ("project-id".equals(nodeName)) {
						ticket.projectName = Csv.lookupProject(ticketSubNode
								.getTextContent());
						ticket.projectId = ticket.projectName.toUpperCase();
					} else if ("assignee-id".equals(nodeName)) {
						ticket.assigneeId = ticketSubNode.getTextContent();
					} else if ("reporter-id".equals(nodeName)) {
						ticket.reporterId = ticketSubNode.getTextContent();
					} else if ("resolution".equals(nodeName)) {
						ticket.resolution = ticketSubNode.getTextContent();
					} else if ("resolution-description".equals(nodeName)) {
						ticket.resolutionDescription = ticketSubNode
								.getTextContent();
					} else if ("created-at".equals(nodeName)) {
						ticket.createdTime = ticketSubNode.getTextContent();
					} else if ("updated-at".equals(nodeName)) {
						ticket.lastUpdateTime = ticketSubNode.getTextContent();
					} else if ("number".equals(nodeName)) {
						ticket.number = ticketSubNode.getTextContent();
					} else if ("component-id".equals(nodeName)) {
						ticket.componentId = ticketSubNode.getTextContent();
					} else if ("priority".equals(nodeName)) {
						ticket.priority = ticketSubNode.getTextContent();
					} else if ("severity-id".equals(nodeName)) {
						ticket.severityId = ticketSubNode.getTextContent();
					} else if ("due-on".equals(nodeName)) {
						ticket.dueDate = ticketSubNode.getTextContent();
					} else if ("hours-estimate-current".equals(nodeName)) {
						String hours = ticketSubNode.getTextContent();
						float h = new Float(hours);
						h = h * 3600;
						ticket.hoursEstimateCurrent = String.valueOf(h)
								.replace(".0", "");
					} else if ("hours-estimate-initial".equals(nodeName)) {
						String hours = ticketSubNode.getTextContent();
						float h = new Float(hours);
						h = h * 3600;
						ticket.hoursEstimateInitial = String.valueOf(h)
								.replace(".0", "");
					} else if ("comments".equals(nodeName)) {
						ticket.comments = Comment.parseComments(ticketSubNode);
					} else if ("time-entries".equals(nodeName)) {
						ticket.timeEntries = TimeEntry
								.parseTimeEntries(ticketSubNode);
					} else if ("attachments".equals(nodeName)) {
						ticket.attachments = Attachment.parseAttachments(
								ticketSubNode, "ticket");
					} else if ("subscriptions".equals(nodeName)) {
						ticket.watchers = Subscription
								.parseSubscriptions(ticketSubNode);
					} else if ("associated-tickets".equals(nodeName)) {
						ticket.associatedTickets = AssociatedTicket
								.parseAssociatedTickets(ticketSubNode);
					} else if ("version-id".equals(nodeName)) {
						ticket.versionId = ticketSubNode.getTextContent();
					}
				}

				// get the comments' attachment
				for (int k = 0; k < ticket.comments.size(); k++)
					for (int j = 0; j < ticket.comments.get(k).attachments
							.size(); j++)
						ticket.attachments.add(new Attachment(ticket.comments
								.get(k).attachments.get(j).id, ticket.comments
								.get(k).attachments.get(j).fullName));

				if (!projects.contains(ticket.projectId))
					projects.add(ticket.projectId);

				if (UnfuddleToJira.nbrAtt < ticket.attachments.size())
					UnfuddleToJira.nbrAtt = ticket.attachments.size();

				tickets.add(ticket);
			}
		}

		System.out.println("Writing " + tickets.size() + " tickets...");
		UnfuddleToJira.projects = projects;

		return tickets;
	}

}
