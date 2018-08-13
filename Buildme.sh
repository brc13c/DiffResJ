if [ -e "DiffResJ.jar" ];
then
	rm DiffResJ.jar
fi
if [ -e "bin" ];
then
	rm -rf bin
fi
mkdir bin
cd src
javac -d ../bin @../buildsources.txt
cp -r typedefs/ ../bin/
cd ..
jar cvfm DiffResJ.jar Manifest.txt -C bin/ .
