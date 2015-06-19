#!/bin/bash

##################
# Main           #
##################

source config.sh

java -Xmx8g org.ccfea.tickdata.OrderReplayService $@
