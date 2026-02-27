#!/usr/bin/env bash
set -euo pipefail

JMX_URL="${JMX_URL:-service:jmx:rmi:///jndi/rmi://127.0.0.1:9010/jmxrmi}"
OBJECT_NAME="${PURGE_OBJECT_NAME:-org.springframework.boot:type=Endpoint,name=mqQueuePurge}"
OPERATION="${PURGE_OPERATION:-purge}"

jshell --execution local <<JS
import javax.management.*;
import javax.management.remote.*;

String url = System.getenv().getOrDefault("JMX_URL", "${JMX_URL}");
String objectName = System.getenv().getOrDefault("PURGE_OBJECT_NAME", "${OBJECT_NAME}");
String operation = System.getenv().getOrDefault("PURGE_OPERATION", "${OPERATION}");

JMXServiceURL serviceURL = new JMXServiceURL(url);
JMXConnector connector = JMXConnectorFactory.connect(serviceURL);
MBeanServerConnection mbsc = connector.getMBeanServerConnection();
ObjectName endpoint = new ObjectName(objectName);
Object result = mbsc.invoke(endpoint, operation, new Object[]{}, new String[]{});
System.out.println("mqQueuePurge.purge() => " + result);
connector.close();
/exit
JS
