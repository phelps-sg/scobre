 
import pandas as pd
import matplotlib.pyplot as plt
import datetime

from orderreplay import *
from thrift.protocol import TBinaryProtocol
from thrift.transport import TSocket

from numpy import log
from numpy import diff

DEFAULT_SERVER = 'localhost'
DEFAULT_PORT = 9090

def get_hf_data(asset, start_date, end_date, 
                variables = ['midPrice', 'lastTransactionPrice', 'volume'], 
                server = DEFAULT_SERVER, port = DEFAULT_PORT):
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
prices_1min = \
    dataset.midPrice['2007-03-02 08:00':'2007-03-02 16:00'].resample('1min')
rets_1min = diff(log(prices_1min))
prices_1min.plot()
