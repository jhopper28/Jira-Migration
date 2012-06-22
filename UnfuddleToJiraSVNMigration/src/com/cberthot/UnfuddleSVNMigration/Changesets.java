package com.cberthot.UnfuddleSVNMigration;

/*
 *  
 * Author: Charline Berthot
 *
 */

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

public class Changesets {

	public static String linkToIssue(String input, String projectId) {
		Pattern p = Pattern.compile("#[0-9]+");
		Matcher m = p.matcher(input);
		while (m.find()) {
			String id = input.substring(m.start() + 1, m.end());
			input = input.replaceFirst("#[0-9]+", projectId + "-" + id);
			m = p.matcher(input);
		}
		return input;
	}

	public static boolean toChange(String input) {
		Pattern p = Pattern.compile("#[0-9]+");
		Matcher m = p.matcher(input);
		if (m.find())
			return true;
		else
			return false;
	}

	public static String getRev(String line) {
		Pattern p = Pattern.compile("rev:[0-9]+");
		Matcher m = p.matcher(line);
		m.find();
		return line.substring(m.start() + 4, m.end());
	}

	public static boolean isRev(String line) {
		Pattern p = Pattern.compile("rev:[0-9]+");
		Matcher m = p.matcher(line);
		if (m.find())
			return true;
		else
			return false;
	}

	public void write(InputStream commitMessages, PrintStream messagesToUpdate,
			String extension, String url) {

		String chain = "";
		String out = "";

		try {

			InputStreamReader ipsr = new InputStreamReader(commitMessages);
			BufferedReader br = new BufferedReader(ipsr);
			String line = br.readLine();
			if (extension.equals("bat"))
				out += "@echo off \n";
			else if (extension.equals("sh"))
				out += "#!/bin/bash \n";
			else
				System.err.println("Wrong extension argument!");

			while (line != null) {

				if (Changesets.isRev(line)) {
					String rev = Changesets.getRev(line);
					chain = "";
					line = br.readLine();

					while ((line != null) && (!Changesets.isRev(line))) {
						chain += line;
						chain += " ";
						line = br.readLine();
					}

					/* 
					 * Numbers and name of the projects to change
					 * 
					 * In this example, the PROJECT ONE was dumped first and reached the revision number 1000.
					 * Then, the PROJECT TWO was dumped and reached revision number 2000.
					 * The PROJECT THREE was the last one to be dumped. 
					 * 
					 * In order to have the right links in your new Jira projects, 
					 * you need to adapt these following code lines.
					 * 
					 * The projects names should match your project keys in Jira.
					 */
					if (Changesets.toChange(chain)) {
						if (Integer.parseInt(rev) <= 1000)
							chain = Changesets.linkToIssue(chain, "PROJECT-ONE");
						else if (Integer.parseInt(rev) <= 2000)
							chain = Changesets.linkToIssue(chain, "PROJECT-TWO");
						else 
							chain = Changesets.linkToIssue(chain, "PROJECT-THREE");
					
					/*
					 * 
					 * The rest can stay the same.
					 * 	
					 */
						chain = chain.replace("\"", "\\\"");
						chain = chain.replace("|", "/");
						chain = chain.replace("%", "percent");
						out += "svn propset -r " + rev
								+ " --revprop svn:log \"" + chain + "\" " + url;
						out += "\n";
					}

				}
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		try {

			messagesToUpdate.println(out);
			messagesToUpdate.close();
			System.out
					.println("You can know run messagesToUpdate on the command line!");
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return;
	}

	/**
	 * @param args
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */

	public static void main(String[] args) throws UnsupportedEncodingException,
			FileNotFoundException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		Changesets changesets = new Changesets();
		if (args.length != 2) {
			System.err
					.println("Wrong arguments number :"
							+ "First argument: Please choose > bat or > sh as argument depending on your OS"
							+ "Second argument: Enter the URL where your local SVN Server is located");
			return;
		}

		String extension = args[0];
		String repositoryURL = args[1];

		InputStream commitMessages = new FileInputStream("commitMessages.txt");
		PrintStream messagesToUpdate = new PrintStream(new FileOutputStream(
				"messagesToUpdate." + args[0]), true, "UTF-8");

		changesets.write(commitMessages, messagesToUpdate, extension,
				repositoryURL);

		return;
	}

}
