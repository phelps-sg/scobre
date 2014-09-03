 
import pandas as pd
import matplotlib.pyplot as plt
import datetime

from orderreplay import *
from thrift.protocol import *
from thrift.transport import *


DEFAULT_SERVER = 'localhost'
DEFAULT_PORT = 9090

def get_hf_data(asset, start_date, end_date, property = 'midPrice', server = DEFAULT_SERVER, port = DEFAULT_PORT):
    transport = TSocket.TSocket(server, port)
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    client = OrderReplay.Client(protocol)
    transport.open()
    data = client.replay(asset, property, start_date, end_date)
    return pd.DataFrame({'t': [datetime.datetime.fromtimestamp(tsd.time / 1000) for tsd in data], 'price': [tsd.price for tsd in data]})

dataset = get_hf_data('GB0009252882', '1/1/2007', '1/1/2009')
plt.plot(dataset.price)
plt.show()

