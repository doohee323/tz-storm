This is a Kafka-Storm-Esper example on vagrant.
=====================================

You can run like this,

./build_deb.sh

You can see the status of storm at http://192.168.82.150:8080.

It has 2 VMs. (/tzstorm/setup.conf)

- storm master(nimbus/supervisor/ui): 192.168.82.150
- storm worker(supervisor): 192.168.82.152

It includes some features, (/tzstorm/scripts/tzstorm.sh)

- install kafka / zookeeper / storm(with esper) / logstash
- register storm test app. in a master VM. (TestTopology_tzstorm)
- register storm-esper test app. in a worker VM. (TestTopology_tzstorm2)

- send log to elasticsearch with logstash (to-do)

When you run topology, you can use VM arguments.

- in local environment like eclipse, use "-DrunType=local".
- in storm environment, use "-DrunType=storm".
- in kafka-integrate environment, use "-DrunType=kafka".

There are some examples.
/tzstorm/src/main/java/example/tzstorm/TestTopology.java
-> Simple storm example 

/tzstorm/src/main/java/example2/tzstorm/TestTopology2.java
-> storm + esper example 

/tzstorm/src/main/java/example3/tzstorm/TestTopology3.java
-> multiple bolts example

/tzstorm/src/main/java/example4/tzstorm/TestTopology4.java
-> kafka - storm(multiple bolts) example

