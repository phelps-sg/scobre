 
import pandas as pd
import matplotlib.pyplot as plt
import datetime

from orderreplay import *
from thrift.protocol import TBinaryProtocol
from thrift.transport import TSocket


DEFAULT_SERVER = 'localhost'
DEFAULT_PORT = 9090

def get_hf_data(asset, start_date, end_date, 
                variables = ['midPrice', 'lastTransactionPrice', 'volume'], 
                server = DEFAULT_SERVER, port = DEFAULT_PORT):
    transport = TSocket.TSocket(server, port)
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    client = OrderReplay.Client(protocol)
    transport.open()
    return pd.DataFrame(client.replay(asset, variables, start_date, end_date))

dataset = get_hf_data('GB0009252882', '2/3/2007', '3/3/2007')
plt.plot(dataset.midPrice)
plt.show()

