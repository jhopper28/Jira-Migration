@echo off

echo rev:0 > commitMessages.txt
rem the last number in the for line should be the last revision number you got from loading the dump files.
FOR /L %%i IN (1,1,9467) DO (	
	CALL :loopbody %%i
)
GOTO :EOF

:loopbody
echo %1
echo rev:%1 >> commitMessages.txt

rem svn propget -r %1 --revprop svn:log UrlToYourLocalRepository  >> commitMessages.txt
svn propget -r %1 --revprop svn:log  >> commitMessages.txt