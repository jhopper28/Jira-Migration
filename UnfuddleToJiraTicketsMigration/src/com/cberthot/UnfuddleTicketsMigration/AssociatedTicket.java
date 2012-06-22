package com.cberthot.UnfuddleTicketsMigration;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AssociatedTicket {

	public String id;
	public String number;
	public String relationship;

	public static ArrayList<AssociatedTicket> parseAssociatedTickets(
			Node ticketsAssociatedTickets) {

		Element elem = (Element) ticketsAssociatedTickets;
		ArrayList<AssociatedTicket> associatedTickets = new ArrayList<AssociatedTicket>();
		NodeList AssociatedTicketsList = elem.getElementsByTagName("ticket");
		NodeList RelationshipList = elem.getElementsByTagName("relationship");

		int nbr = AssociatedTicketsList.getLength();

		for (int j = 0; j < nbr; j++) {

			Node node = AssociatedTicketsList.item(j);
			Node relation = RelationshipList.item(j);

			Element nodeElem = (Element) node;
			Element relationElem = (Element) relation;

			AssociatedTicket associatedTicket = new AssociatedTicket();
			NodeList associatedTicketElements = nodeElem.getChildNodes();
			associatedTicket.relationship = relationElem.getTextContent();

			for (int k = 0; k < associatedTicketElements.getLength(); k++) {
				Node associatedTicketSubNode = associatedTicketElements.item(k);
				String nodeName = associatedTicketSubNode.getNodeName();
				if ("id".equals(nodeName)) {
					associatedTicket.id = associatedTicketSubNode
							.getTextContent();
				} else if ("number".equals(nodeName)) {
					associatedTicket.number = associatedTicketSubNode
							.getTextContent();
				}
			}
			associatedTickets.add(associatedTicket);
		}

		return associatedTickets;
	}
}
