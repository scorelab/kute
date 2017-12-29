#!/bin/bash
if [[ $EUID -ne 0 ]]; then
   echo "This script must be run as root" 
   exit 1
fi
# Install dependencies.
sudo apt-get update
sudo apt-get install rabbitmq-server -y
sudo apt-get install celery -y
sudo apt-get install supervisor -y
sudo apt-get install python-pip apache2 libapache2-mod-wsgi -y

# Create log file.
sudo mkdir /var/log/celeryd/
sudo touch /var/log/celeryd/MicroService.log
chown -hR www-data:www-data /var/log/celeryd/MicroService.log

# Configuring supervisor.
echo "[program:MicroService-celery]
command=python ${PWD}/RouteMatcher/manage.py celeryd --loglevel=INFO
environment=PYTHONPATH=${PWD}/RouteMatcher
directory=${PWD}/RouteMatcher
user=www-data
numprocs=1
stdout_logfile=/var/log/celeryd/MicroService.log
stderr_logfile=/var/log/celeryd/MicroService.log
autostart=true
autorestart=true
startsecs=10
stopwaitsecs = 600
priority=998" > /etc/supervisor/conf.d/Micro-Service.conf

# Configuring apache2
echo "Listen 88" >> /etc/apache2/ports.conf

echo "<VirtualHost *:88>
DocumentRoot ${PWD}/RouteMatcher
</VirtualHost>
<Directory ${PWD}/RouteMatcher>
Require all granted
</Directory>

<Directory ${PWD}/RouteMatcher/RouteMatcher>
WSGIApplicationGroup %{GLOBAL}
Require all granted
WSGIApplicationGroup %{GLOBAL}
WSGIDaemonProcess MicroService python-home=/kute/RouteMatcher/env python-path=/kute/RouteMatcher 
WSGIProcessGroup MicroService 
WSGIScriptAlias / ${PWD}/RouteMatcher/RouteMatcher/wsgi.py
</Directory>" >> /etc/apache2/sites-enabled/000-default.conf
sudo chown -hR www-data:www-data ${PWD}
sudo apachectl restart
echo "You are set."
