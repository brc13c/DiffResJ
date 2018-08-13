if exist DiffResJ.jar del DiffResJ.jar
if exist bin rmdir /S /Q bin
mkdir bin
cd src
javac -d ..\bin @..\buildsources.txt
mkdir ..\bin\typedefs
copy typedefs ..\bin\typedefs
cd ..
jar cvfm DiffResJ.jar Manifest.txt -C bin\ .
