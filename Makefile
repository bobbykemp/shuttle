clean:
	mvn clean

clean_examples:
	rm examples/*.class

compile:
	mvn compile

test:
	mvn test

package:
	mvn package

run:
	java -cp target/shuttle-1.0-SNAPSHOT.jar com.rjkemp.shuttle.App

keys:
	mkdir ./keys
	ssh-keygen -t rsa -b 2048 -f ./keys/id_rsa