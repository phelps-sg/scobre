#!/bin/sh

USER=root
PASSWORD=$1
FILE=$2
TABLE=$3

import_pipe() {
	TABLE=$1
	mysql --local_infile=1 -u$USER -p$PASSWORD <<EOF
	    USE lse_tickdata;
	    LOAD DATA LOCAL INFILE '/tmp/lsedata.txt' INTO TABLE $TABLE FIELDS TERMINATED BY ','
EOF
}

replace_null() {
	sed s/NULL/\\\\N/g   
}

import() {
	FILE=$1
	TABLE=$2

	cat $FILE | replace_null > /tmp/lsedata.txt &
	import_pipe $TABLE
}

rm -f /tmp/lsedata.txt
mkfifo /tmp/lsedata.txt

import $FILE $TABLE
#import t_OrderDetail order_detail_raw;
#import t_OrderHistory tblOrderHistory;
#import t_TradeReport tblTradeReports;

rm /tmp/lsedata.txt
