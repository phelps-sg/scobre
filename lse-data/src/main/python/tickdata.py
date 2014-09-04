 
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

def get_hf_data(asset, start_date, end_date, 
                variables = ['midPrice', 'lastTransactionPrice', 'volume'], 
                server = DEFAULT_SERVER, port = DEFAULT_PORT):
    '''
    Retrieve the specified data from the order-book reconstructor as a pandas DataFrame.
    :param asset:           The ISIN of the asset
    :param start_date:      The start date as a string
    :param end_date:        The end date as a string
    :param variables:       The variables to retrieve
    :param server:          The host-name of the server hosting the tick-data
    :param port:            The port-number of the server
    :return:                A pandas data frame containing time-series for all requested variables
    '''
    transport = TSocket.TSocket(server, port)
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    client = OrderReplay.Client(protocol)
    transport.open()
    df = pd.DataFrame(client.replay(asset, variables, start_date, end_date))
    timestamps = [datetime.datetime.fromtimestamp(t) for t in df.t]
    for variable in variables:
        df[variable].index = timestamps
    return df   

dataset = get_hf_data('GB0009252882', '2/3/2007', '3/3/2007')

# Plot 1 minute prices for 3/3/2007 between 8am and 4pm
prices_1min = \
    dataset.midPrice['2007-03-02 08:00':'2007-03-02 16:00'].resample('1min')
prices_1min.plot()

# Plot 1 minute returns
rets_1min = diff(log(prices_1min))
plt.figure()
plt.plot(rets_1min)

# Kernel-density estimate of 1-min return distribution
plt.figure()
matrix.sort(rets_1min)
density = gaussian_kde(rets_1min)
plt.fill(rets_1min, density(rets_1min))


