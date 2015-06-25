import numpy
import pp
import csv
import time
import datetime
import os

from orderreplay import *
from numpy.random import randint

import thrift

OFFSETTING_NONE =     0
OFFSETTING_SAME =     1
OFFSETTING_MID =      2
OFFSETTING_OPPOSITE = 3

ITERATIONS = 100

BASE_DIR = '/var/data/orderflow-shuffle'

DATE_YEAR = 2007
DATE_MONTH = 7

def dir_name(d):
    return "%s/one-day/%d-%d-%d" % ('/var/data/orderflow-shuffle', d.year, d.month, d.day)
                   
def create_dirs(days):
    for day in days:
        name = dir_name(day)
        if not os.path.isdir(name):
            os.mkdir(name)
    
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
    
def perform_shuffle(proportion, window, n, intra_window, offsetting, date_range):
    if date_range is None:
        directory = BASE_DIR
    else:
        directory = dir_name(date_range[0])
    for i in range(n):
        percentage = round(proportion * n)
        dataset = get_shuffled_data('BHP', proportion, window, intra_window, offsetting, date_range = date_range)
        filename = '%s/bhp-shuffled-ws%d-p%d-i%d-o%d-%d.csv' % (directory, window, percentage, intra_window, offsetting, i)
        f = open(filename, 'w', buffering=200000)
        csv_writer = csv.writer(f)
        for row in dataset:
            csv_writer.writerow([ round(row['midPrice'], 4) ])
        f.close()
    return None

def sweep(fn):
#for offsetting in [OFFSETTING_NONE, OFFSETTING_SAME, OFFSETTING_MID, OFFSETTING_OPPOSITE]:
    offsetting = OFFSETTING_NONE
#for intra_window in [True, False]:
    intra_window = False
#for window in [4 ** (x + 1) for x in range(6)]:
    window = 1
    for proportion in numpy.arange(0, 1.1, 0.1):
        fn(proportion, window, intra_window, offsetting)

def submit_shuffling_jobs(job_server, t0, iterations):
    
    dep_modules = ('pandas', 'orderreplay', 'thrift', 'csv', 'time', 'datetime')
    dep_functions = (dir_name, get_shuffled_data, date_to_time, )
    jobs = []
    
    t1 = t0 + datetime.timedelta(days = 1)
    date_range = (t0, t1)
    
    def submit_job(proportion, window, intra_window, offsetting):
         job = job_server.submit(perform_shuffle, (proportion, window, iterations, intra_window, offsetting, date_range), dep_functions, dep_modules)
         jobs.append(job)
#         time.sleep(randint(0, 3))
         
    sweep(submit_job)
    return jobs

def submit_all(num_cpus = 8, iterations = ITERATIONS):
    job_server = pp.Server(ncpus=num_cpus, secret='shuffle')
    days_all = [datetime.datetime(DATE_YEAR, DATE_MONTH, d+1) for d in range(30)]
    week_days = filter(lambda d: d.isoweekday() < 6, days)
    jobs = []
    for day in week_days:
        jobs.extend(submit_shuffling_jobs(job_server, day, iterations))
    return (job_server, jobs)    
                       
def plot_graphs():
    
    def plot_graph(proportion, window, intra_window, offsetting):
        t0 = datetime.datetime(2007, 7, 20)
        t1 = datetime.datetime(2007, 7, 21)
        ds = get_shuffled_data('BHP', proportion, window, intra_window, offsetting, date_range = (t0, t1))    
        df = dict_to_df(ds, ['midPrice'])
        figure()
        plot(df.midPrice)    
    
    sweep(plot_graph)     