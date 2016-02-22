This is a Kafka-Storm-Esper example on vagrant.
=====================================

1. build
	You can run it on vagrant like this,
	./build_deb.sh
	
	and you can see the status of storm at http://192.168.82.157:8080.

2. examples
	There are some examples.
	
	1) Simple storm example
		/tz-storm/src/main/java/example/tz-storm/TestTopology.java
	
	2) storm + esper example
		/tz-storm/src/main/java/example2/tz-storm/TestTopology2.java
	
	3) multiple bolts example
		/tz-storm/src/main/java/example3/tz-storm/TestTopology3.java
	
	4) kafka - storm(multiple bolts) example
		/tz-storm/src/main/java/example4/tz-storm/TestTopology4.java
	
	5) storm + trident(unique data) + esper example
		/tz-storm/src/main/java/example5/tz-storm/TestTopology5.java
	
	6) zmq PUB/SUB + storm + trident(unique data) + esper example
	
	7) zmq REP/REQ + storm + trident(unique data) + esper example
		/tz-storm/src/main/java/example7/tz-storm/TestTopology7.java
		-classpath .:/home/vagrant/tz-storm/jzmq-3.1.0.jar -Djava.library.path=/usr/local/lib -Xcheck:jni
		
		/tz-storm/src/main/java/example7/tz-storm/zmq/ZMQClient.java
		-classpath .:/home/vagrant/tz-storm/jzmq-3.1.0.jar -Djava.library.path=/usr/local/lib -Xcheck:jni

	8) graphite + storm + trident(unique data) + esper example
		/tz-storm/src/main/java/example8/tz-storm/TestTopology8.java
	
3. set VM configuration for vagrant
	You can define the VM server which you want to run.
	for example, if you define the VMs for example7,
	- in /tz-storm/setup.conf 
	{
	  'ip'=> {
	    'tz-storm7' => "192.168.82.157"
	  }
	}
	- in /tz-storm/setup.rc
	cfg_ip_tz-storm7="192.168.82.157"
	
	Then, vagrant will use this shell, /tz-storm/scripts/tz-storm7.sh for build VM. 

4. set VM configuration for vagrant
	It includes some features, (/tz-storm/scripts/tz-storm.sh)

5. etc 
	When you run topology, you can use VM arguments.
	
	- in local environment like eclipse, use "-DrunType=local".
	- in storm environment, use "-DrunType=storm".
	- in kafka-integrate environment, use "-DrunType=kafka".
	
	You can change logback configration file for each examples like this.
	-Dlogback.configurationFile=logback6.xml

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
		sudo cp -Rf /usr/share/pkgconfig/lib/* /usr/local/lib
		export LD_LIBRARY_PATH=/usr/local/lib



