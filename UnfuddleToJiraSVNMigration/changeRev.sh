#!/bin/bash

echo rev:0 > commitMessages.txt
#the last number in the while line should be the last revision number you got from loading the dump files.

i=1
while [ $i -lt 586 ] 
do 
	echo $i
	echo rev:$i >> commitMessages.txt
	
#svn propget -r $i --revprop svn:log UrlToYourLocalRepository >> commitMessages.txt

	svn propget -r $i --revprop svn:log https://traackr.unfuddle.com/svn/traackr_sysadmin >> commitMessages.txt
	((i++))

done

