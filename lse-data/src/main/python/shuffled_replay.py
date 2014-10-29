 
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import datetime

from orderreplay import *
from thrift.protocol import TBinaryProtocol
from thrift.transport import TSocket

from numpy import log
from numpy import diff
from numpy import matrix

from scipy.stats.kde import gaussian_kde

DEFAULT_SERVER = 'localhost'
DEFAULT_PORT = 9090

def get_shuffled_data(asset, proportion, windowSize, 
                variables = ['midPrice'], 
                server = DEFAULT_SERVER, port = DEFAULT_PORT):
    
    transport = TSocket.TSocket(server, port)
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    client = OrderReplay.Client(protocol)
    transport.open()
    df = pd.DataFrame(client.shuffledReplay(asset, variables, proportion, windowSize))
    if len(df) == 0:
        raise Exception("No data available for " + asset + " between " + \
                            start_date + " and " + end_date)
    event_time = range(df.shape[0])
    for variable in variables:
        df[variable].index = event_time
    return df   

#dataset = get_hf_data('GB0009252882', '2/3/2007', '3/3/2007')
for window in [4 ** (x + 1) for x in range(8)]:
    for proportion in arange(0, 1.1, 0.1):
        percentage = round(proportion * 100)
        print "window size = %d" % window
        print "proportion = %f" % proportion 
        print "Fetching data..."
        dataset = get_shuffled_data('BHP', proportion, window)
        print "done."
        print "Writing data..."
        dataset.to_csv('/home/sphelps/tmp/orderflow-shuffle/bhp-shuffled-ws%d-p%d.csv' % (window, percentage))
        print "done."
        plt.figure()
        dataset.midPrice.plot()
