This is Storm-Logstash on vagrant example
=====================================

You can run like this,

./build_deb.sh

You can see the status of storm at http://192.168.82.150:8080

It has 2 VMs. (/tzstorm/setup.conf)
	- storm master(nimbus/supervisor/ui): 192.168.82.150
	- storm worker(supervisor): 192.168.82.152

It includes some features, (/tzstorm/scripts/tzstorm.sh)

- install zookeeper / storm / logstash
- register test app. in a master VM. (TestTopology_tzstorm)
- register test app. in a worker VM. (TestTopology_tzstorm2)

- send log to elasticsearch with logstash (to-do)

