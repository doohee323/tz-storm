#!/usr/bin/env bash

# Get root up in here
sudo su

set -x

# change hosts
echo '' >> /etc/hosts
echo '# for vm' >> /etc/hosts
echo '192.168.82.150 tzstorm.test.com' >> /etc/hosts
echo '192.168.82.152 tzstorm2.test.com' >> /etc/hosts

echo "Reading config...." >&2
source /vagrant/setup.rc

PROJ_NAME=tzstorm
HOME=/home/vagrant
mkdir -p $HOME/$PROJ_NAME
cp /vagrant/target/version.ini $HOME/$PROJ_NAME
export VERSION=$(cat $HOME/$PROJ_NAME/version.ini)

#apt-get update
#apt-get install software-properties-common python-software-properties -y
#add-apt-repository ppa:openjdk-r/ppa -y

apt-get update
apt-get install wget curl unzip -y
#apt-get install openjdk-8-jdk -y
apt-get install openjdk-7-jdk -y

echo '' >> $HOME/.bashrc
echo 'export PATH=$PATH:.:$HOME/apache-storm-0.9.5/bin:$HOME/zookeeper-3.4.6/bin:$HOME/logstash-1.5.3/bin' >> $HOME/.bashrc
echo 'export PROJ_NAME='$PROJ_NAME >> $HOME/.bashrc
echo 'export VERSION='$VERSION >> $HOME/.bashrc

PATH=$PATH:.:$HOME/apache-storm-0.9.5/bin:$HOME/zookeeper-3.4.6/bin:$HOME/logstash-1.5.3/bin

### [install zookeeper] ############################################################################################################
cd $HOME
wget http://apache.arvixe.com/zookeeper/stable/zookeeper-3.4.6.tar.gz
tar xvzf zookeeper-3.4.6.tar.gz
cd zookeeper-3.4.6
cp conf/zoo_sample.cfg conf/zoo.cfg
echo '' >> conf/zoo.cfg
echo 'tzstorm.test.com=zookeeper1:2888:3888' >> conf/zoo.cfg
echo 'tzstorm2.test.com=zookeeper2:2888:3888' >> conf/zoo.cfg

cp /vagrant/etc/init/zookeeper.conf /etc/init
mkdir -p logs
zkServer.sh start &

### [install apache-storm] ############################################################################################################
cd $HOME
wget http://apache.arvixe.com/storm/apache-storm-0.9.5/apache-storm-0.9.5.zip
unzip apache-storm-0.9.5.zip
cd apache-storm-0.9.5
echo '' >> conf/storm.yaml
echo 'storm.zookeeper.servers:' >> conf/storm.yaml
echo '    - "tzstorm.test.com"' >> conf/storm.yaml
echo '    - "tzstorm2.test.com"' >> conf/storm.yaml
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
storm supervisor &

### [launch example app] ############################################################################################################
cd $HOME
cp /vagrant/target/$PROJ_NAME-$VERSION.jar $HOME/$PROJ_NAME

storm jar $HOME/$PROJ_NAME/$PROJ_NAME-$VERSION.jar example.tzstorm.TestTopology TestTopology_tzstorm2

#storm list
#storm deactivate TestTopology_tzstorm2
#storm kill TestTopology_tzstorm2


