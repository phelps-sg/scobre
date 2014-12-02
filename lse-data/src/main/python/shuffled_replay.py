import numpy
import pp
import csv
import time

from orderreplay import *
from numpy.random import randint

import thrift

OFFSETTING_NONE =     0
OFFSETTING_SAME =     1
OFFSETTING_MID =      2
OFFSETTING_OPPOSITE = 3

def get_shuffled_data(asset, proportion, window_size, intra_window = False,
                          offsetting = 0,
                          variables = ['midPrice'],
                            server = 'cseesp1', port = 9090):
    from thrift.transport import TSocket
    from thrift.protocol import TBinaryProtocol
    from orderreplay import *
    transport = TSocket.TSocket(server, port)
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    client = OrderReplay.Client(protocol)
    transport.open()
    result = \
        client.shuffledReplay(asset, variables, proportion, window_size, \
                                intra_window, offsetting)
    return result
    
    
def perform_shuffle(proportion, window, n = 100, intra_window = False, offsetting = 0, directory='/var/data/orderflow-shuffle'):
    for i in range(n):
        percentage = round(proportion * n)
        dataset = get_shuffled_data('BHP', proportion, window, intra_window, offsetting)
        filename = '%s/bhp-shuffled-ws%d-p%d-i%d-o%d-%d.csv' % (directory, window, percentage, intra_window, offsetting, i)
        f = open(filename, 'w', buffering=200000)
        csv_writer = csv.writer(f)
        for row in dataset:
            csv_writer.writerow([ round(row['midPrice'], 4) ])
        f.close()
    return None

job_server = pp.Server(ncpus=4, secret='shuffle') 

dep_modules = ('pandas', 'orderreplay', 'thrift', 'csv')
dep_functions = (get_shuffled_data, )

jobs = []
for offsetting in [OFFSETTING_NONE, OFFSETTING_SAME, OFFSETTING_MID, OFFSETTING_OPPOSITE]:
    for intra_window in [True, False]:
        for window in [4 ** (x + 1) for x in range(8)]:
            for proportion in numpy.arange(0, 1.1, 0.1):
                job = job_server.submit(perform_shuffle, (proportion, window, intra_window, offsetting), dep_functions, dep_modules)
                jobs.append(job)
                #time.sleep(randint(0, 10))
                    