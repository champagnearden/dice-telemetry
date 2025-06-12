export OTEL_TRACES_EXPORTER=none
export OTEL_METRICS_EXPORTER=none
export OTEL_LOGS_EXPORTER=none
export OTEL_JAVAAGENT_LOGGING=none
export OTEL_INSTRUMENTATION_COMMON_DEFAULT_ENABLED=false
export OTEL_INSTRUMENTATION_OPENTELEMETRY_API_ENABLED=true
export OTEL_INSTRUMENTATION_OPENTELEMETRY_INSTRUMENTATION_ANNOTATIONS_ENABLED=true
export JAVA_TOOL_OPTIONS="-javaagent:./opentelemetry-javaagent.jar"

cd service
gradle assemble
java -jar ./build/libs/service.jar
cd ..
