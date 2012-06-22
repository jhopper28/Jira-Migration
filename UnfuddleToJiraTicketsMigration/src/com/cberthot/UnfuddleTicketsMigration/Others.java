package com.cberthot.UnfuddleTicketsMigration;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Others {

	/*
	 * parse : projects, milestones, versions, components, people, severities,
	 * strategies, types, prioritiPoints
	 */
	public static Map<String, String> parseProjects(Document doc) {
		Map<String, String> projects = new HashMap<String, String>();
		NodeList projectNodes = doc.getElementsByTagName("project");

		for (int i = 0; i < projectNodes.getLength(); i++) {

			String parentNodeName = projectNodes.item(i).getParentNode()
					.getParentNode().getNodeName();
			if (!"repository".equals(parentNodeName)) {
				Element elem = (Element) projectNodes.item(i);
				String title = elem.getElementsByTagName("title").item(0)
						.getTextContent();
				String id = elem.getElementsByTagName("id").item(0)
						.getTextContent();
				projects.put(id, title);
			}
		}
		System.out
				.println("Found " + projects.size() + "projects: " + projects);
		return projects;
	}

	public static Map<String, String> parseMilestones(Document doc) {
		Map<String, String> milestones = new HashMap<String, String>();
		NodeList milestoneNodes = doc.getElementsByTagName("milestone");
		for (int i = 0; i < milestoneNodes.getLength(); i++) {
			Element elem = (Element) milestoneNodes.item(i);
			String title = elem.getElementsByTagName("title").item(0)
					.getTextContent();
			String id = elem.getElementsByTagName("id").item(0)
					.getTextContent();
			milestones.put(id, title);
		}
		System.out.println("Found " + milestones.size() + " milestones: "
				+ milestones);
		return milestones;
	}

	public static Map<String, String> parseVersions(Document doc) {
		Map<String, String> versions = new HashMap<String, String>();
		NodeList versionNodes = doc.getElementsByTagName("version");
		for (int i = 0; i < versionNodes.getLength(); i++) {

			String parentNodeName = versionNodes.item(i).getParentNode()
					.getNodeName();
			if ("versions".equals(parentNodeName)) {

				Element elem = (Element) versionNodes.item(i);
				String name = elem.getElementsByTagName("name").item(0)
						.getTextContent();
				String id = elem.getElementsByTagName("id").item(0)
						.getTextContent();
				versions.put(id, name);
			}
		}
		System.out.println("Found " + versions.size() + " versions: "
				+ versions);
		return versions;
	}

	public static Map<String, String> parseComponents(Document doc) {
		Map<String, String> components = new HashMap<String, String>();
		NodeList componentNodes = doc.getElementsByTagName("component");
		for (int i = 0; i < componentNodes.getLength(); i++) {
			Element elem = (Element) componentNodes.item(i);
			String name = elem.getElementsByTagName("name").item(0)
					.getTextContent();
			String id = elem.getElementsByTagName("id").item(0)
					.getTextContent();
			components.put(id, name);
		}
		System.out.println("Found " + components.size() + " components: "
				+ components);
		return components;
	}

	public static Map<String, String> parsePeople(Document doc) {
		Map<String, String> people = new HashMap<String, String>();
		NodeList peopleNodes = doc.getElementsByTagName("person");
		for (int i = 0; i < peopleNodes.getLength(); i++) {
			Element elem = (Element) peopleNodes.item(i);
			if (elem.getElementsByTagName("username").item(0) != null) {
				String name = elem.getElementsByTagName("username").item(0)
						.getTextContent();
				String id = elem.getElementsByTagName("id").item(0)
						.getTextContent();
				people.put(id, name);
			}
		}
		System.out.println("Found " + people.size() + " people: " + people);
		return people;
	}

	public static Map<String, String> parseSeverities(Document doc) {
		Map<String, String> severities = new HashMap<String, String>();
		NodeList severityNodes = doc.getElementsByTagName("severity");
		for (int i = 0; i < severityNodes.getLength(); i++) {
			Element elem = (Element) severityNodes.item(i);
			String name = elem.getElementsByTagName("name").item(0)
					.getTextContent();
			String id = elem.getElementsByTagName("id").item(0)
					.getTextContent();
			severities.put(id, name);
		}
		System.out.println("Found " + severities.size() + " severities: "
				+ severities);
		return severities;
	}

	public static Map<String, String> parseFieldValue(Document doc) {
		Map<String, String> types = new HashMap<String, String>();
		NodeList typeNodes = doc.getElementsByTagName("custom-field-value");

		for (int i = 0; i < typeNodes.getLength(); i++) {
			Element elem = (Element) typeNodes.item(i);
			String value = elem.getElementsByTagName("value").item(0)
					.getTextContent();
			String id = elem.getElementsByTagName("id").item(0)
					.getTextContent();
			types.put(id, value);
		}
		System.out.println("Found " + types.size() + "field values: " + types);
		return types;
	}

}
