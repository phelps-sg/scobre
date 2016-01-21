 
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import datetime
import time

from orderreplay import *
from thrift.protocol import TBinaryProtocol
from thrift.transport import TSocket

from numpy import log
from numpy import diff
from numpy import matrix

from scipy.stats.kde import gaussian_kde

DEFAULT_SERVER = 'localhost'
DEFAULT_PORT = 9090

def date_to_time(d):
    return long(time.mktime(d.timetuple())) * 1000
 
def dict_to_df(data, variables):
    df = pd.DataFrame(data)
    df.index = pd.Series([datetime.datetime.fromtimestamp(t) for t in df.t])
    return df         
    
def get_hf_data(asset, start_date, end_date, 
                variables = ['midPrice', 'lastTransactionPrice', 'volume'], 
                server = DEFAULT_SERVER, port = DEFAULT_PORT):
    '''
    Retrieve the specified data from the order-book reconstructor as a pandas DataFrame.
    :param asset:           The ISIN of the asset
    :param start_date:      The start date as a datetime object
    :param end_date:        The end date as a a datetime object
    :param variables:       The variables to retrieve
    :param server:          The host-name of the server hosting the tick-data
    :param port:            The port-number of the server
    :return:                A pandas data frame containing time-series for all requested variables
    '''
    transport = TSocket.TSocket(server, port)
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    client = OrderReplay.Client(protocol)
    transport.open()
    t0 = date_to_time(start_date)
    t1 = date_to_time(end_date)
    raw_data = client.replay(asset, variables, t0, t1)
    if len(raw_data) == 0:
        raise Exception("No data available")
    return dict_to_df(raw_data, variables)
    
dataset = get_hf_data('GB0009252882', datetime.datetime(2007, 3, 2), 
                          datetime.datetime(2007, 3, 3), server='localhost')
#dataset = get_hf_data('GB0002875804', '2/3/2007', '3/3/2009', server='localhost')

#dataset = get_hf_data('BHP', '2/7/2007', '3/7/2007')

mid_price = dataset.midPrice['2007-03-02 08:00':'2007-03-02 16:00'].dropna(how='any')

# Plot 1 minute prices for 3/3/2007 between 8am and 4pm
prices_1min = mid_price.resample('3min')
#prices_1min = \
#    dataset.midPrice['2007-02-03 08:00':'2007-02-03 16:00'].resample('1min')
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


