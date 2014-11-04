import pandas 
import numpy 
import pp
import csv

from orderreplay import *
import thrift

def get_shuffled_data(asset, proportion, window_size, intra_window = False,
                variables = ['midPrice'],
                server = 'cseesp1', port = 9090):
    from thrift.transport import TSocket
    from thrift.protocol import TBinaryProtocol
    from orderreplay import *
    transport = TSocket.TSocket(server, port)
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    client = OrderReplay.Client(protocol)
    transport.open()
    result = client.shuffledReplay(asset, variables, proportion, window_size, intra_window)
    return result
    
    
def perform_shuffle(proportion, window, intra_window = False, directory='/var/data/orderflow-shuffle'):
    for i in range(100):    
        percentage = round(proportion * 100)
        dataset = get_shuffled_data('BHP', proportion, window, intra_window)
        filename = '%s/bhp-shuffled-ws%d-p%d-i%d-%d.csv' % (directory, window, percentage, intra_window, i)
        f = open(filename, 'w', buffering=200000)
        csv_writer = csv.writer(f)
        for row in dataset:
            csv_writer.writerow([ round(row['midPrice'], 4) ])
        f.close()
    None

job_server = pp.Server(ncpus=4, secret='shuffle') 

dep_modules = ('pandas', 'orderreplay', 'thrift', 'csv')
dep_functions = (get_shuffled_data, )

jobs = []
for intra_window in [True, False]:
    for window in [4 ** (x + 1) for x in range(8)]:
        for proportion in numpy.arange(0, 1.1, 0.1):
            job = job_server.submit(perform_shuffle, (proportion, window, intra_window), dep_functions, dep_modules)
            jobs.append(job)
