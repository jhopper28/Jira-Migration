
Unfuddle to Jira Migration

Tickets migration
1. Download the project TicketsMigration and import it.
2. Request a backup from Unfuddle
3. Put the file backup.xml (rename it if needed to backup.xml) in the TicketsMigration folder.
4. Edit the configurations of the project to run it properly:
		The first argument should be either bat or sh (it will become the extensions of the script files) depending on your os.
		The second argument should be the url where you will put your attachment folder from the backup.
5. Run the project.
6. Move the renameAttachmentPROJECT file to the attachment folder from your backup and execute the script. 
7. Upload the attachment folder to the url you entered as second argument.
8. Import the CSV file on Jira. You can use the CSVConfigurationExample to help you map the fields values of the csv file. 
9. Go to the TicketsMigration/atlassian folder. Open jira & jira2 .bat or .sh and enter on jira the server, the username and the password of the administrator. On jira2, enter only the server and the password, no username here. For the password, ask all the users that have logged work (time entries on Unfuddle) to choose the same password and enter it here. 
10. Run either: 
	jira --action run --file actionsPROJECT.txt 
	and 
	timeEntriesPROJECT
	or ./jira.sh --action run --file actionsPROJECT.txt
	and
	./timeEntriesPROJECT.sh

	
SVN Repositories Migration
1. If you are on Microsoft, you may need to download VisualSVN Server. That way, you can run: pathToVisualSVN/vsvnvars in order to run svn commands.
2. Create a new repository and create as many folders as you have projects (their name should match your projects keys). 
3. You will then need to load all your svn dumps from your backup into your new repository thanks to this command: 
type pathToDump/PROJECT.svn.dmp | svnadmin load --parent-dir PROJECT pathToRepository 
or
cat pathToDump/PROJECT.svn.dmp | svnadmin load --parent-dir PROJECT pathToRepository 

***

4. Once it's done, you can create a new dump file with all these projects that you'll be able to import in Jira:
svnadmin dump pathToRepository > pathToNewDumpFile/newDump.dump
5. Upload the dump file into your webdav at this url https://COMPAGNY.atlassian.net/webdav/dump_files/
See more instructions here: https://confluence.atlassian.com/display/AOD/Uploading+Data+via+WebDAV
6. Go to the SVN Importer and import your dump file.
7. Create a working copy of your new repository with svn checkout and you're good to go!
	
***

If you want to create links between the commits and the issues, you may want to do the following before actually creating your final dump file.
1. Download the project SVNMigration and import it.
2. Edit the pre revision property change hook for your repository (in the hook tab in the properties of your repository in VisualSVN)
You can either use the template or copy/paste the content of the pre revision hook text file which is in the SVNMigration folder.
2. Open the file changeRev and update the last revision number as well as the repository location (the svn info command provides it if you ignore it). Run the changeRev script: it will get all the commit messages from your project. 
3. Edit the configurations of the SVNMigration project.
The first argument should be either bat or sh (it will become the extensions of the script files) depending on your os.
The second argument should be the url where you new local svn repository is located (the same location as in #2).
4. For everything to work, you need to dive a bit into the code and edit the Changesets.java file:
Around line 100, you need to write which project match which revision. More explanations are available as comments in the file.				
5. Run the SVNMigration project: it will create a file messagesToUpdate which contains the commands to change the commits.
6. Execute the messagesToUpdate script and finish the SVN import once it's completed (go back to II. 4)
	
