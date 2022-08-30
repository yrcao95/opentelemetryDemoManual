# A demo for opentelemetry manual instrumentation

This is a demo to show the tracing with opentelemetry. 
To see the tracing, you need to run docker and the following command:
```
docker run --rm -it --name jaeger\
  -p 16686:16686 \
  -p 14250:14250 \
  -d jaegertracing/all-in-one:1.16
```

This starts a jaeger instance that listens on port 14250 where our application will
pass the tracing to. Afterwards, simply start up the application and go to http://localhost:16686

To demonstrate automatic instrumentation, check out the branch "automatic", and run the following the command (with the jaeger up):
```
# This creates the jar package in the /target folder
mvn clean package
# Run the jar package with the java agent
java -javaagent:./src/main/resources/opentelemetry-javaagent.jar -Dotel.traces.exporter=jaeger -Dotel.service.name=tracingDemoAgent -jar ./target/tracingDemo-0.0.1-SNAPSHOT.jar
```