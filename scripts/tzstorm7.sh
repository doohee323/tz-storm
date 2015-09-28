#!/usr/bin/env bash

# Get root up in here
sudo su

set -x

# change hosts
echo '' >> /etc/hosts
echo '# for vm' >> /etc/hosts
echo '192.168.82.150 tzstorm.test.com' >> /etc/hosts
echo '192.168.82.152 tzstorm2.test.com' >> /etc/hosts
echo '192.168.82.154 tzstorm3.test.com' >> /etc/hosts

echo "Reading config...." >&2
source /vagrant/setup.rc

PROJ_NAME=tzstorm
HOME=/home/vagrant
mkdir -p $HOME/$PROJ_NAME
cp /vagrant/target/version.ini $HOME/$PROJ_NAME
export VERSION=$(cat $HOME/$PROJ_NAME/version.ini)

#apt-get update
apt-get install libtool autoconf automake uuid-dev build-essential wget g++ git pkg-config -y
#add-apt-repository ppa:openjdk-r/ppa -y

apt-get update
apt-get install wget curl unzip -y
#apt-get install openjdk-8-jdk -y
apt-get install openjdk-7-jdk -y

echo '' >> $HOME/.bashrc
echo 'export PATH=$PATH:.:$HOME/apache-storm-0.9.5/bin:$HOME/kafka_2.11-0.8.2.1/bin:$HOME/logstash-1.5.3/bin' >> $HOME/.bashrc
echo 'export PROJ_NAME='$PROJ_NAME >> $HOME/.bashrc
echo 'export VERSION='$VERSION >> $HOME/.bashrc
echo 'export LD_LIBRARY_PATH=/usr/local/lib' >> $HOME/.bashrc

PATH=$PATH:.:$HOME/apache-storm-0.9.5/bin:$HOME/kafka_2.11-0.8.2.1/bin:$HOME/logstash-1.5.3/bin

### [install libzmq] ############################################################################################################
cd $HOME
git clone https://github.com/zeromq/libzmq.git
cd libzmq
./autogen.sh
./configure --prefix=/usr/share/pkgconfig --without-libsodium
make
make install
cp -Rf /usr/share/pkgconfig/lib/* /usr/local/lib 

git clone https://github.com/zeromq/jzmq.git
cd jzmq
./autogen.sh
./configure
make
make install

export LD_LIBRARY_PATH=/usr/local/lib
ldconfig

### [install apache-storm] ############################################################################################################
cd $HOME
wget http://apache.arvixe.com/storm/apache-storm-0.9.5/apache-storm-0.9.5.zip
unzip apache-storm-0.9.5.zip
cd apache-storm-0.9.5
echo '' >> conf/storm.yaml
echo 'storm.zookeeper.servers:' >> conf/storm.yaml
echo '    - "tzstorm.test.com"' >> conf/storm.yaml
echo '    - "tzstorm2.test.com"' >> conf/storm.yaml
echo '    - "tzstorm3.test.com"' >> conf/storm.yaml
echo 'nimbus.host: "tzstorm.test.com"' >> conf/storm.yaml

echo 'supervisor.slots.ports:' >> conf/storm.yaml
echo '    - 7000' >> conf/storm.yaml
echo '    - 7001' >> conf/storm.yaml
echo '    - 7002' >> conf/storm.yaml
echo '    - 7003' >> conf/storm.yaml
echo '    - 7004' >> conf/storm.yaml

# heartbeat frequency worker start timeout
echo 'supervisor.heartbeat.frequency.secs: 10' >> conf/storm.yaml
echo 'supervisor.worker.start.timeout.secs: 120' >> conf/storm.yaml

cp /vagrant/etc/init/storm.conf /etc/init
storm nimbus &
storm supervisor &
storm ui &

### [launch example app] ############################################################################################################
cd $HOME/$PROJ_NAME
cp /vagrant/target/$PROJ_NAME-$VERSION.jar $HOME/$PROJ_NAME
cp /vagrant/target/$PROJ_NAME-$VERSION-jar-with-dependencies.jar $HOME/$PROJ_NAME
mkdir -p $HOME/$PROJ_NAME/data
cp /vagrant/data/a.txt $HOME/$PROJ_NAME/data

storm jar $HOME/$PROJ_NAME/$PROJ_NAME-$VERSION.jar example7.tzstorm.TestTopology7 TestTopology_tzstorm7
java -Djava.library.path=/usr/local/lib -classpath $HOME/$PROJ_NAME/jzmq-3.1.0.jar -cp tzstorm-0.0.1-SNAPSHOT-jar-with-dependencies.jar example7.tzstorm.zmq.ZMQClient

#storm list
#storm deactivate TestTopology_tzstorm7
#storm kill TestTopology_tzstorm7


