# Hono example bridge

This is a small project using Quarkus to forward Eclipse Hono telemetry data to an
Apache Kafka cluster.

You can drop this into your OpenShift cluster by executing:

    oc new-app quay.io/quarkus/ubi-quarkus-native-s2i:20.0.0-java11~https://github.com/ctron/hono-example-bridge-quarkus

**Note:** This project is the Quarkus version of the Hono/Kafka bridge
          originally implemented with Apache Camel: [ctron/hono-kafka-bridge](https://github.com/ctron/hono-kafka-bridge)

In order to understand how the example is being used, take a look at the
EclipseCon 2019 IoT Playground repository: [ctron/ece2019-iot-playground](https://github.com/ctron/ece2019-iot-playground).
