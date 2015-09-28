This is a Kafka-Storm-Esper example on vagrant.
=====================================

1. build
	You can run it on vagrant like this,
	./build_deb.sh
	
	and you can see the status of storm at http://192.168.82.157:8080.

2. examples
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

3. set VM configuration for vagrant
	You can define the VM server which you want to run.
	for example, if you define the VMs for example7,
	- in /tzstorm/setup.conf 
	{
	  'ip'=> {
	    'tzstorm7' => "192.168.82.157"
	  }
	}
	- in /tzstorm/setup.rc
	cfg_ip_tzstorm7="192.168.82.157"
	
	Then, vagrant will use this shell, /tzstorm/scripts/tzstorm7.sh for build VM. 

4. set VM configuration for vagrant
	It includes some features, (/tzstorm/scripts/tzstorm.sh)

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
	sudo cp /usr/local/share/java/zmq.jar /usr/local/lib
	sudo cp /usr/local/lib/libjzmq.* /usr/local/lib
	(sudo cp /usr/share/pkgconfig/lib/*.* /usr/local/lib)




