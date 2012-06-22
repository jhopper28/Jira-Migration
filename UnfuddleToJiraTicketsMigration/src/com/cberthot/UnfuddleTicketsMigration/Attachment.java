package com.cberthot.UnfuddleTicketsMigration;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Attachment {

	public String id;
	public String fullName;

	public Attachment(String id, String fullName) {
		this.id = id;
		this.fullName = fullName;
	}

	public static ArrayList<Attachment> parseAttachments(
			Node ticketsAttachments, String belongsTo) {

		Element elem = (Element) ticketsAttachments;
		ArrayList<Attachment> attachments = new ArrayList<Attachment>();
		NodeList AttachmentsList = elem.getElementsByTagName("attachment");

		int nbr = AttachmentsList.getLength();

		for (int j = 0; j < nbr; j++) {
			Node node = AttachmentsList.item(j);
			String grparentNodeName = node.getParentNode().getParentNode()
					.getNodeName();
			if (belongsTo.equals(grparentNodeName)) {
				Element nodeElem = (Element) node;
				Attachment attachment = new Attachment("", "");
				NodeList attachmentElements = nodeElem.getChildNodes();
				String extension = "";
				for (int k = 0; k < attachmentElements.getLength(); k++) {
					Node attachmentSubNode = attachmentElements.item(k);
					String nodeName = attachmentSubNode.getNodeName();
					if ("id".equals(nodeName)) {
						attachment.id = attachmentSubNode.getTextContent();
					} else if ("filename".equals(nodeName)) {
						extension = attachmentSubNode.getTextContent();
						extension = extension.substring(extension
								.lastIndexOf('.') + 1);
					}
				}
				attachment.fullName = attachment.id + "." + extension;
				attachments.add(attachment);
			}
		}

		return attachments;
	}
}