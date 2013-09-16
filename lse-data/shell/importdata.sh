#!/bin/sh

USER=root
PASSWORD=$1
FILE=$2
TABLE=$3
SOCKET=$4

import_pipe() {
	TABLE=$1
	mysql --local_infile=1 -u$USER -p$PASSWORD --socket=$SOCKET <<EOF
	    USE lse_tickdata;
	    LOAD DATA LOCAL INFILE '/tmp/lsedata.txt' INTO TABLE $TABLE FIELDS TERMINATED BY ','
EOF
}

# Replace NULL with \N for use with MySql import
replace_null() {
	sed s/NULL/\\\\N/g   
}

# Import the specified CSV file into the specified mysql table
import() {
	FILE=$1
	TABLE=$2

	cat $FILE | replace_null > /tmp/lsedata.txt &
	import_pipe $TABLE
}

##################
# Main           #
##################

if [ -z "$SOCKET" ]; then
	export SOCKET=/var/run/mysqld/mysqld.sock
fi

# Remove previous named pipe
rm -f /tmp/lsedata.txt

# Create a named pipe to the mysql command
mkfifo /tmp/lsedata.txt

import $FILE $TABLE

#import t_OrderDetail order_detail_raw;
#import t_OrderHistory tblOrderHistory;
#import t_TradeReport tblTradeReports;

# Clean up
rm /tmp/lsedata.txt
