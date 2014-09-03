#!/bin/bash

##################
# Main           #
##################

source config.sh

java org.ccfea.tickdata.OrderReplayService $@
