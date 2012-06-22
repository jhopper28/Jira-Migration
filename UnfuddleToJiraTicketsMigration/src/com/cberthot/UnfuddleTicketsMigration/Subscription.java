package com.cberthot.UnfuddleTicketsMigration;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Subscription {

	public String username;

	public static ArrayList<Subscription> parseSubscriptions(Node ticketSub) {
		Element elem = (Element) ticketSub;
		ArrayList<Subscription> subscriptions = new ArrayList<Subscription>();
		NodeList SubscriptionsList = elem.getElementsByTagName("subscription");
		int nbr = SubscriptionsList.getLength();

		for (int j = 0; j < nbr; j++) {
			Node node = SubscriptionsList.item(j);

			Element nodeElem = (Element) node;
			Subscription subscription = new Subscription();
			NodeList subscriptionElements = nodeElem.getChildNodes();
			for (int k = 0; k < subscriptionElements.getLength(); k++) {
				Node subscriptionSubNode = subscriptionElements.item(k);
				String nodeName = subscriptionSubNode.getNodeName();
				if ("person-id".equals(nodeName))
					subscription.username = Csv.lookupUser(subscriptionSubNode.getTextContent());
			}
			subscriptions.add(subscription);

		}
		return subscriptions;
	}
}