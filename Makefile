package:
	mvn package

clean:
	mvn clean

clean_examples:
	rm examples/*.class

test_files:
	./testing/make_test_files.sh

clean_test_files:
	rm testing/source/*.dat
	rm testing/destination/*.dat

compile:
	mvn compile

test:
	mvn test

run:
	java -cp target/shuttle-1.0-SNAPSHOT.jar com.rjkemp.shuttle.App

keys:
	mkdir ./keys
	ssh-keygen -t rsa -b 2048 -f ./keys/id_rsa