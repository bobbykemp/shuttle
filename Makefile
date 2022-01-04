clean:
	rm examples/*.class

compile:
	mvn package

run:
	java -cp target/shuttle-1.0-SNAPSHOT.jar com.rjkemp.shuttle.App