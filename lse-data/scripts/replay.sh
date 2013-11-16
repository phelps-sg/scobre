#!/bin/bash

# Replay order-book events from HBase

##################
# Main           #
##################

source config.sh

java org.ccfea.tickdata.OrderReplay $@
#java org.ccfea.tickdata.OrderReplay -t GB0009252882 --with-gui
