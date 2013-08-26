#!/bin/sh

USER=root
PASSWORD=$1

import_pipe() {
	TABLE=$1
	mysql --local_infile=1 -u$USER -p$PASSWORD <<EOF
	    USE lse;
	    LOAD DATA LOCAL INFILE '/tmp/lsedata.txt' INTO TABLE $TABLE FIELDS TERMINATED BY ','
EOF
}

import() {
    BASEFILE=$1
    TABLE=$2

    for file in $BASEFILE*.csv
    do
#    bzcat $BASEFILE.csv > /tmp/lsedata.txt &
    	mysql --local_infile=1 -u$USER -p$PASSWORD <<EOF
	USE lse;
	LOAD DATA LOCAL INFILE '$file' INTO TABLE $TABLE FIELDS TERMINATED BY ','
EOF
    done

    for file in $BASEFILE*.zip
    do
	    unzip -p $file > /tmp/lsedata.txt &
	    import_pipe $TABLE
    done


    for file in $BASEFILE*.csv.gz
    do
	    zcat $file > /tmp/lsedata.txt &
	    import_pipe $TABLE
    done
}

rm -f /tmp/lsedata.txt
mkfifo /tmp/lsedata.txt

import t_OrderDetail tblOrderDetail;
import t_OrderHistory tblOrderHistory;
import t_TradeReport tblTradeReports;

import T_OrderDetail tblOrderDetail;
import T_OrderHistory tblOrderHistory;
import T_TradeReport tblTradeReports;

rm /tmp/lsedata.txt
