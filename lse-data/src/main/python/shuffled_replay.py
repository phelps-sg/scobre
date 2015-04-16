import numpy
import pp
import csv
import time
import datetime

from orderreplay import *
from numpy.random import randint

import thrift

OFFSETTING_NONE =     0
OFFSETTING_SAME =     1
OFFSETTING_MID =      2
OFFSETTING_OPPOSITE = 3

ITERATIONS = 10

def date_to_time(d):
    return long(time.mktime(d.timetuple())) * 1000
    
def get_shuffled_data(asset, proportion, window_size, 
                      intra_window = False, offsetting = 0, 
                      variables = ['midPrice'], 
                        server = 'localhost', port = 9090,
                        date_range = None):
    from thrift.transport import TSocket
    from thrift.protocol import TBinaryProtocol
    from orderreplay import *
    transport = TSocket.TSocket(server, port)
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    client = OrderReplay.Client(protocol)
    transport.open()
    if date_range is None:
        result = \
            client.shuffledReplay(asset, variables, proportion, window_size, \
                                    intra_window, offsetting)
    else:
        t0 = date_range[0]
        t1 = date_range[1]                        
        result = \
            client.shuffledReplayDateRange(asset, variables, proportion, \
                                    window_size, intra_window, offsetting, \
                                    date_to_time(t0), date_to_time(t1))
    return result
    
    
def perform_shuffle(proportion, window, n, intra_window, offsetting, date_range, directory='/var/data/orderflow-shuffle'):
    for i in range(n):
        percentage = round(proportion * n)
        dataset = get_shuffled_data('BHP', proportion, window, intra_window, offsetting, date_range)
        filename = '%s/bhp-shuffled-ws%d-p%d-i%d-o%d-%d.csv' % (directory, window, percentage, intra_window, offsetting, i)
        f = open(filename, 'w', buffering=200000)
        csv_writer = csv.writer(f)
        for row in dataset:
            csv_writer.writerow([ round(row['midPrice'], 4) ])
        f.close()
    return None

job_server = pp.Server(ncpus=8, secret='shuffle') 

dep_modules = ('pandas', 'orderreplay', 'thrift', 'csv')
dep_functions = (get_shuffled_data, date_to_time, )
jobs = []

t0 = datetime.datetime(2007, 7, 20)
t1 = datetime.datetime(2007, 7, 21)
intra_window = False
window = 1
for offsetting in [OFFSETTING_NONE, OFFSETTING_SAME, OFFSETTING_MID, OFFSETTING_OPPOSITE]:
    for intra_window in [True, False]:
       for window in [4 ** (x + 1) for x in range(6)]:
            for proportion in numpy.arange(0, 1.1, 0.1):
                job = job_server.submit(perform_shuffle, (proportion, window, ITERATIONS, intra_window, offsetting, (t0, t1)), dep_functions, dep_modules)
                jobs.append(job)
                time.sleep(randint(0, 3))
