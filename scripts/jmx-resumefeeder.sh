#!/usr/bin/env bash
set -euo pipefail

JMX_URL="${JMX_URL:-service:jmx:rmi:///jndi/rmi://127.0.0.1:9010/jmxrmi}"
OBJECT_NAME="${FEEDING_OBJECT_NAME:-org.springframework.boot:type=Endpoint,name=feedingControl}"
OPERATION="${FEEDING_RESUME_OPERATION:-resumeFeeding}"

jshell --execution local <<JS
import javax.management.*;
import javax.management.remote.*;

String url = System.getenv().getOrDefault("JMX_URL", "${JMX_URL}");
String objectName = System.getenv().getOrDefault("FEEDING_OBJECT_NAME", "${OBJECT_NAME}");
String operation = System.getenv().getOrDefault("FEEDING_RESUME_OPERATION", "${OPERATION}");

try {
    JMXServiceURL serviceURL = new JMXServiceURL(url);
    JMXConnector connector = JMXConnectorFactory.connect(serviceURL);
    try {
        MBeanServerConnection mbsc = connector.getMBeanServerConnection();
        ObjectName endpoint = new ObjectName(objectName);
        Object result = mbsc.invoke(endpoint, operation, new Object[]{}, new String[]{});
        System.out.println("feedingControl.resumeFeeding() => " + result);
    } finally {
        connector.close();
    }
} catch (Exception e) {
    System.err.println("ERREUR : Impossible de se connecter au JMX à l'URL : " + url);
    System.err.println("Détail : " + e.getMessage());
    System.exit(1);
}
/exit
JS
