#!/bin/sh

# Replay order-book events from HBase

if [ $# -lt 1 ]; then
	echo "Usage: importdata.sh <TI CODE>"
	exit 1
fi

TICODE=$1

##################
# Main           #
##################

BASEDIR=`dirname $0`
export BASEDIR=$BASEDIR/..

# Configure Java class path
export CLASSPATH=$BASEDIR/etc:$BASEDIR/target/scala-2.10/lse-data-assembly-0.4.jar

java org.ccfea.tickdata.OrderReplay -t $TICODE
#java org.ccfea.tickdata.OrderReplay -t GB0009252882 --with-gui
