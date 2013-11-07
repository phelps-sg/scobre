#!/bin/bash

# Replay order-book events from HBase

if [ $# -lt 1 ]; then
	echo "Usage: importdata.sh <TI CODE>"
	exit 1
fi

TICODE=$1
shift

##################
# Main           #
##################

source config.sh

java org.ccfea.tickdata.OrderReplay -t $TICODE $@
#java org.ccfea.tickdata.OrderReplay -t GB0009252882 --with-gui
