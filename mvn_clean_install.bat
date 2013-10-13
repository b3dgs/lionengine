@echo off

set PATH=C:\Program Files (x86)\Apache Maven\bin;%PATH%
set M2_HOME=C:\Program Files (x86)\Apache Maven
set JAVA_HOME=C:\Program Files\Java\jdk1.7.0_40

mvn clean install
