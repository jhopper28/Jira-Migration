package com.cberthot.UnfuddleTicketsMigration;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Comment {

	public String authorId;
	public String body;
	public String createdTime;
	public String id;
	public String parentId;
	public String parentType;
	public String updatedAt;
	public ArrayList<Attachment> attachments;

	public static int maxComments;

	public static ArrayList<Comment> parseComments(Node ticketsComments) {
		Element elem = (Element) ticketsComments;
		ArrayList<Comment> comments = new ArrayList<Comment>();
		NodeList CommentsList = elem.getElementsByTagName("comment");

		int nbr = CommentsList.getLength();

		for (int j = 0; j < nbr; j++) {
			Node node = CommentsList.item(j);
			Element nodeElem = (Element) node;
			Comment comment = new Comment();
			NodeList commentElements = nodeElem.getChildNodes();

			for (int k = 0; k < commentElements.getLength(); k++) {
				Node commentSubNode = commentElements.item(k);
				String nodeName = commentSubNode.getNodeName();
				if ("id".equals(nodeName)) {
					comment.id = commentSubNode.getTextContent();
				} else if ("body".equals(nodeName)) {
					comment.body = commentSubNode.getTextContent();
				} else if ("author-id".equals(nodeName)) {
					comment.authorId = commentSubNode.getTextContent();
				} else if ("created-at".equals(nodeName)) {
					comment.createdTime = commentSubNode.getTextContent();
				} else if ("parent-id".equals(nodeName)) {
					comment.parentId = commentSubNode.getTextContent();
					if (nbr > maxComments)
						maxComments = nbr;
				} else if ("parent-type".equals(nodeName)) {
					comment.parentType = commentSubNode.getTextContent();
				} else if ("updated-at".equals(nodeName)) {
					comment.updatedAt = commentSubNode.getTextContent();
				} else if ("attachments".equals(nodeName)) {
					comment.attachments = Attachment.parseAttachments(
							commentSubNode, "comment");
				}
			}
			comments.add(comment);
		}

		return comments;
	}
}