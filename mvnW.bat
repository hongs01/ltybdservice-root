echo %cd%
set APP_HOME=%cd%
echo APP_HOME is  %APP_HOME%

call mvn clean -Dmaven.test.skip=true

cd %APP_HOME%/ltybdservice-schedul/

call mvn clean scala:compile compile -Dmaven.test.skip=true

cd ..

call mvn package -Dmaven.test.skip=true




