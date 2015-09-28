This is a Kafka-Storm-Esper example on vagrant.
=====================================

You can run it on vagrant like this,
./build_deb.sh

and you can see the status of storm at http://192.168.82.150:8080.

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

You can change logback configration file for each examples like this.
-Dlogback.configurationFile=logback6.xml

There are some examples.
1) Simple storm example
/tzstorm/src/main/java/example/tzstorm/TestTopology.java

2) storm + esper example
/tzstorm/src/main/java/example2/tzstorm/TestTopology2.java

3) multiple bolts example
/tzstorm/src/main/java/example3/tzstorm/TestTopology3.java

4) kafka - storm(multiple bolts) example
/tzstorm/src/main/java/example4/tzstorm/TestTopology4.java

5) storm + trident(unique data) + esper example
/tzstorm/src/main/java/example5/tzstorm/TestTopology5.java

6) zmq PUB/SUB + storm + trident(unique data) + esper example

7) zmq REP/REQ + storm + trident(unique data) + esper example
/tzstorm/src/main/java/example7/tzstorm/TestTopology7.java
-classpath .:/Users/mac/git2/tzstorm/lib/jzmq-3.1.0.jar -Djava.library.path=/usr/local/lib -Xcheck:jni

/tzstorm/src/main/java/example7/tzstorm/zmq/ZMQClient.java
-classpath .:/Users/mac/git2/tzstorm/lib/jzmq-3.1.0.jar -Djava.library.path=/usr/local/lib -Xcheck:jni

cf) zmq install on mac

1) libzmq
git clone git://github.com/zeromq/libzmq.git
cd libzmq
./autogen.sh
./configure --prefix=/usr/share/pkgconfig --without-libsodium
make
sudo make install

2) jzmq
git clone https://github.com/zeromq/jzmq.git
cd jzmq
./autogen.sh
./configure --with-zeromq=/home/vagrant/libzmq
(./configure --with-zeromq=/Users/mac/tmp/libzmq)
make
sudo make install

3) copy libraries
sudo cp /usr/local/share/java/zmq.jar /usr/local/lib
sudo cp /usr/local/lib/libjzmq.* /usr/local/lib
(sudo cp /usr/share/pkgconfig/lib/*.* /usr/local/lib)




