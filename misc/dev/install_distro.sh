#!/bin/bash
#
# Installs a KenyaEMR distribution zip to OpenMRS

MOD_DIR=/usr/share/tomcat6/.OpenMRS/modules

echo "[$HOSTNAME] Installing $1 ..."

rm $MOD_DIR/*.omod

echo "[$HOSTNAME] Deleted existing omods"

unzip -oj "$1" -d "$MOD_DIR"

echo "[$HOSTNAME] Extracted new omods"

service tomcat6 restart

echo "[$HOSTNAME] Restarted Tomcat"