#!/bin/bash
warFolder=questions-1.0-SNAPSHOT

mvn -Dmaven.test.skip=true jetty:run-war

#sleep 40

#targetDir="`pwd`/target/$warFolder/templates/"
#find ./src/main/webapp/js -name *.js -exec ln -sf `pwd`/\{\} -t "./target/$warFolder/js/" \;
#(cd src/main/webapp/templates/; find . -name *.soy -exec ln -sf `pwd`/\{\} $targetDir\{\} \;)
#
#fg
