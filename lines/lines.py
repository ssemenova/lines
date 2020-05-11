from __future__ import division
from sklearn import datasets
from sklearn.svm import SVC
import sqlite3
import time
import random
import numpy as np
import matplotlib.pyplot as plt
import os
try:
    import progressbar
    bar_support = True
except ImportError:
    print("Do pip install progressbar2 for a sweet progress bar")
    bar_support = False

USE_IN_MEMORY = False

# TODO: clean up this entire method and split into smaller ones
def main():
    db_exists = os.path.isfile("db.sqlite")
    conn = sqlite3.connect(':memory:' if USE_IN_MEMORY else 'db.sqlite')
    if USE_IN_MEMORY or not db_exists:
        sampleDB(conn)

    # template, its predicted latency, and (after queries are actually run), another array of (param,resulting latency) pairs
    # TODO: add more templates
    templatesLatencies = [['SELECT COUNT(*) from table1, table2 WHERE table1.attr1 = table2.attr1 AND table1.attr1 = ?', .75]]

    clf = SVC()
    x = []
    y = []
    classification = []
    error = .5
    for template in templatesLatencies:
        print("Executing Queries")
        if bar_support: 
            bar = progressbar.ProgressBar(max_value=249)
        else:
            bar = lambda x: x # bar does nothing if it can't be imported
        for i in bar(range(250)):
            if not bar_support: 
                print("Executing query", i)
            randomParam = random.randrange(1,4)
            x.append(randomParam)
            resultLatency = make_queries(conn, randomParam, template)
            y.append(resultLatency)
            # TODO: add +/- error
            classification.append(getClassification(resultLatency, template[1], error))

    plt.figure()
    plt.scatter(x,y)
    plt.savefig('latencies.png')
    classification = np.array(classification)
    print("Fitted SVM", clf.fit(np.array(zip(x,y)), classification))

    # TODO: keep adding new queries and compare actual to predicted
    # choose some acceptable % of "off" queries - if the amount of wrongly predicted ones
    # increases beyond that percentage, split template into two
    # (easy to do with this template, but need to generalize it later)

    print("Starting to execute new queries")
    notDone = True
    acceptable = .75
    # keeping track of the new queries made so we can use them to predict a new model
    x2 = []
    y2 = []
    classification2 = []
    i = 0
    while (notDone):
        for template in templatesLatencies:
            print("Executing new query", i)
            randomParam = random.randrange(1,4)
            x2.append(randomParam)
            resultLatency = make_queries(conn, randomParam, template)
            y2.append(resultLatency)
            # TODO: add +/- error
            classification2.append(getClassification(resultLatency, template[1], error))
            percentAcceptable = np.where(np.array(classification) == "good")[0].size / classification.size
            print("Current percent acceptable", percentAcceptable)
            if (percentAcceptable < acceptable):
                print("Split template!")
                # TODO: how to know where to split? maybe restructure this mess of arrays
            i += 1

def getClassification(resultLatency, predictedLatency, error):
    predictedLatencyError = [predictedLatency+error, predictedLatency-error]
    if (resultLatency < predictedLatencyError[0]):
        return "over"
    elif (resultLatency > predictedLatencyError[1]):
        return "under"
    else:
        return "good"

# set up table with test data
def sampleDB(conn):
    table1 = 'table1'
    table2 = 'table2'
    attr1 = 'attr1'
    attr2 = 'attr2'
    field_type = 'INT'

    c = conn.cursor()

    c.execute('CREATE TABLE {table1} (attr1 {ft}, attr2 {ft}, attr3 {ft}, attr4 {ft}, attr5 {ft})'.format(table1=table1, ft=field_type))
    c.execute('CREATE TABLE table2 AS SELECT * FROM table1 WHERE 0')
    c.execute('CREATE INDEX table1Idx ON table1 (attr1)')
    c.execute('CREATE INDEX table2Idx ON table2 (attr1)')

    conn.commit()

    print("Inserting 1s...")
    for i in range(0,4000):
        c.execute("INSERT INTO table1 VALUES (1, 1, 1, 1, 1)")
        c.execute("INSERT INTO table2 VALUES (1, 1, 1, 1, 1)")

    print("Inserting 2s...")
    for i in range(0,300):
        c.execute("INSERT INTO table1 VALUES (2, 1, 1, 1, 1)")
        c.execute("INSERT INTO table2 VALUES (2, 1, 1, 1, 1)")

    print("Inserting 3s...")
    for i in range(0,4000):
        c.execute("INSERT INTO table1 VALUES (3, 1, 1, 1, 1)")
        c.execute("INSERT INTO table2 VALUES (3, 1, 1, 1, 1)")

    print("Committing...")
    conn.commit()
    print("Database ready!")

# make queries on table
def make_queries(conn, param, templates):
    template = templates[0]
    predictedLatency = templates[1]

    c = conn.cursor()
    start = time.time()

    c.execute(template, (param,))
    c.fetchone()

    return(time.time() - start)

main()
