#!/bin/bash

echo "Lanzando ligador de RMI"
rmiregistry &

echo
echo "Compilando con javac"
javac *.java

sleep 2

echo
echo "Lanzando el servidor 1..."
java -cp . -Djava.rmi.server.hostname=localhost -Djava.security.policy=server.policy Servidor &

sleep 2

echo
echo "Lanzando cliente en SERVIDOR 1..."
java -cp . -Djava.rmi.server.hostname=localhost -Djava.security.policy=server.policy Cliente server1
