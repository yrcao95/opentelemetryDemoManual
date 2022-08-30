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
pass the tracing to. Afterwards, simply start up the application and go to http://localhost:14250