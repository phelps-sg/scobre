#!/bin/bash

# Replay order-book events from HBase

##################
# Main           #
##################

source config.sh

java org.ccfea.tickdata.OrderReplayer $@
#java org.ccfea.tickdata.OrderReplayer -t GB0009252882 --with-gui
