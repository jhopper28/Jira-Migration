package com.cberthot.UnfuddleTicketsMigration;

import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.*;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.petebevin.markdown.MarkdownProcessor;


public class Page implements Comparable<Page> {

	public String id;
	public String number;
	public String title;
	public String version;
	public String body;
	public String bodyFormat;
	public String message;
	public String authorId;
	public String createdAt;
	public String updatedAt;

	public int compareTo(Page other) {
		return Integer.parseInt(id) - Integer.parseInt(other.id);
	}

	public static ArrayList<Page> parsePages(Document doc) {

		ArrayList<Page> pages = new ArrayList<Page>();
		NodeList PagesList = doc.getElementsByTagName("page");

		int nbr = PagesList.getLength();

		for (int j = 0; j < nbr; j++) {
			Node node = PagesList.item(j);

			Element nodeElem = (Element) node;
			Page page = new Page();
			NodeList pageElements = nodeElem.getChildNodes();
			for (int k = 0; k < pageElements.getLength(); k++) {
				Node pageSubNode = pageElements.item(k);
				String nodeName = pageSubNode.getNodeName();
				if ("id".equals(nodeName)) {
					page.id = pageSubNode.getTextContent();
				} else if ("created-at".equals(nodeName)) {
					page.createdAt = pageSubNode.getTextContent();
				} else if ("body".equals(nodeName)) {
					page.body = pageSubNode.getTextContent();
				} else if ("body-format".equals(nodeName)) {
					page.bodyFormat = pageSubNode.getTextContent();
				} else if ("author-id".equals(nodeName)) {
					page.authorId = pageSubNode.getTextContent();
				} else if ("message".equals(nodeName)) {
					page.message = pageSubNode.getTextContent();
				} else if ("version".equals(nodeName)) {
					page.version = pageSubNode.getTextContent();
				} else if ("title".equals(nodeName)) {
					page.title = pageSubNode.getTextContent();
				} else if ("number".equals(nodeName)) {
					page.number = pageSubNode.getTextContent();
				} else if ("updatedAt".equals(nodeName)) {
					page.updatedAt = pageSubNode.getTextContent();
				}
			}
			pages.add(page);
		}

		System.out.println("Found " + pages.size() + "pages...");
		return pages;
	}

	public static void wikiToHtml(Page page, String project) {

		String title = page.title;
		String content = page.title + "\n\n";

		content += "Version " + page.version + " Created by "
				+ Csv.lookupUser(page.authorId);
		content += " on " + page.createdAt.substring(0, 10) + "\n\n";
		content += page.body;

		PrintStream htmlPage;
		try {
			htmlPage = new PrintStream(new FileOutputStream("notes/"
					+ project + " - " + title.replace("/", "-") + ".html"), true, "UTF-8");

			if (page.bodyFormat.equals("textile")) {
				MarkupParser markupParser = new MarkupParser();
				markupParser.setMarkupLanguage(new TextileLanguage());

				Pattern p = Pattern.compile("[^a-zA-Z0-9]\\* \\* ");
				Matcher m = p.matcher(content);
				while (m.find()) {
					String first = content.substring(m.start(), m.start() + 1);
					content = content.replaceFirst("[^a-zA-Z0-9]\\* \\* ", " "
							+ first + "twostars ");
					m = p.matcher(content);
				}

				p = Pattern.compile("[^\\*,&\\]a-zA-Z0-9] #[0-9]");
				m = p.matcher(content);
				while (m.find()) {
					String first = content.substring(m.start(), m.start() + 1);
					String last = content.substring(m.end() - 1, m.end());
					content = content.replaceFirst(
							"[^\\*,&\\]a-zA-Z0-9] #[0-9]", first + " onesharp"
									+ last);
					m = p.matcher(content);
				}

				p = Pattern.compile("[^a-z0-9A-Z ]#[0-9]");
				m = p.matcher(content);
				while (m.find()) {
					String first = content.substring(m.start(), m.start() + 1);
					String last = content.substring(m.end() - 1, m.end());
					content = content.replaceFirst("[^a-z0-9A-Z ]#[0-9]", first
							+ "onesharp" + last);
					m = p.matcher(content);
				}

				p = Pattern.compile("[^a-zA-Z0-9\\*\\.]\\* ");
				m = p.matcher(content);
				while (m.find()) {
					String first = content.substring(m.start(), m.start() + 1);

					content = content.replaceFirst("[^a-zA-Z0-9\\*\\.]\\* ",
							first + "onestar ");
					m = p.matcher(content);
				}

				content = content.replaceAll("twostars ", "\n* * ");
				content = content.replaceAll("onestar ", "\n* ");
				content = content.replaceAll("onesharp", "\n* #");
				content = content.replace("\"#", "\n* \"#");

				content = markupParser.parseToHtml(content);

			}

			if (page.bodyFormat.equals("markdown")) {
				MarkdownProcessor m = new MarkdownProcessor();
				content = m.markdown(content);
			}

			if (page.bodyFormat.equals("plain")) {
				MarkupParser markupParser = new MarkupParser();
				markupParser.setMarkupLanguage(new TextileLanguage());

				content = markupParser.parseToHtml(content);
			}

			htmlPage.println(content);
			htmlPage.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return;
	}
}