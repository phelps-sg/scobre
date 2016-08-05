 
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import datetime as dt
import time

from orderreplay import OrderReplay
from thrift.protocol import TBinaryProtocol
from thrift.transport import TSocket

from numpy import log
from numpy import diff
from numpy import matrix

from scipy.stats.kde import gaussian_kde

DEFAULT_SERVER = 'localhost'
DEFAULT_PORT = 9090
DEFAULT_VARIABLES = ['midPrice', 'lastTransactionPrice', 'volume']

def date_to_time(d):
    return long(time.mktime(d.timetuple())) * 1000
 
def dict_to_df(data, variables):
    df = pd.DataFrame(data)
    df.index = pd.Series([dt.datetime.fromtimestamp(t) for t in df.t])
    return df
    
def convert_timestamp_from_long(t):
    seconds = t / 1000
    milliseconds = (t % 1000)
    return dt.datetime.fromtimestamp(seconds) + \
            dt.timedelta(microseconds = milliseconds * 1000)
    
def load_csv_as_df(csv_file_name):
    df = pd.read_csv(csv_file_name, sep='\t')    
    df.index = pd.Series([convert_timestamp_from_long(t) for t in df.t])
    return df    
    
class ReplayClient(object):

    def __init__(self, server = DEFAULT_SERVER, port = DEFAULT_PORT):
        self.server = server
        self.port = port
                     
        
    def connect(self):
        self.transport = TSocket.TSocket(self.server, self.port)
        self.protocol = TBinaryProtocol.TBinaryProtocol(self.transport)
        self.client = OrderReplay.Client(self.protocol)
        self.transport.open()        
            
    def get_hf_data(self, asset, start_date, end_date, 
                    variables = DEFAULT_VARIABLES):
                    
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
        t0 = date_to_time(start_date)
        t1 = date_to_time(end_date)
        raw_data = self.client.replay(asset, variables, t0, t1)
        if len(raw_data) == 0:
            raise Exception("No data available")
        return dict_to_df(raw_data, variables)
        
    def write_hf_data_to_csv(self, asset, 
                                 start_date, end_date, csv_file_name,
                                 variables = DEFAULT_VARIABLES):                                 
        '''
        Retrieve the specified data from the order-book reconstructor and write it
        to a CSV file.
        :param asset:           The ISIN of the asset
        :param start_date:      The start date as a datetime object
        :param end_date:        The end date as a a datetime object
        :param variables:       The variables to retrieve
        :param csv_file_name:   The file name of the CSV file
        :return:                A pandas data frame containing time-series for all requested variables
        '''        
        t0 = date_to_time(start_date)
        t1 = date_to_time(end_date)
        return self.client.replayToCsv(asset, variables, t0, t1, csv_file_name)
        
    def get_hf_data_with_csv(self, asset, 
                                 start_date, end_date, csv_file_name,
                                 variables = DEFAULT_VARIABLES):
        self.write_hf_data_to_csv(asset, start_date, end_date, csv_file_name, variables)
        return load_csv_as_df(csv_file_name)
    
def test():

    client = ReplayClient(server='localhost')
    client.connect()
    dataset = client.get_hf_data('GB0009252882', dt.datetime(2007, 3, 2), 
                          dt.datetime(2007, 3, 3))

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


