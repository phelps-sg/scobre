#!/bin/bash

# Replay order-book events from HBase

##################
# Main           #
##################

source config.sh

java -Xmx3g org.ccfea.tickdata.ReplayOrders $@
#java org.ccfea.tickdata.ReplayOrders -t GB0009252882 --with-gui
