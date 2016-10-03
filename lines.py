from sklearn import datasets
import sqlite3
import time
import random
import numpy as np
import matplotlib.pyplot as plt


def main():
    conn = setup_table()

    plt.figure()
    x = []
    y = []
    for i in range(0,500):
        randomParam = random.randrange(0,4)
        x.append(randomParam)
        y.append(make_queries(randomParam))

    plt.scatter(x,y)
    plt.show()
    print(latencies)

# set up table with test data
def setup_table():
    table1 = 'table1'
    table2 = 'table2'
    attr1 = 'attr1'
    attr2 = 'attr2'
    field_type = 'INT'

    conn = sqlite3.connect('db.sqlite')
    c = conn.cursor()

    c.execute('CREATE TABLE {table1} (attr1 {ft}, attr2 {ft}, attr3 {ft}, attr4 {ft}, attr5 {ft})'.format(table1=table1, ft=field_type))
    c.execute('CREATE TABLE table2 AS SELECT * FROM table1 WHERE 0')

    conn.commit()

    for i in range(0,200000):
        c.execute("INSERT INTO table1 VALUES (1, 1, 1, 1, 1)")
        conn.commit()
        c.execute("INSERT INTO table2 VALUES (1, 1, 1, 1, 1)")
        conn.commit()
    for i in range(0,1000):
        c.execute("INSERT INTO table1 VALUES (2, 1, 1, 1, 1)")
        conn.commit()
        c.execute("INSERT INTO table2 VALUES (2, 1, 1, 1, 1)")
        conn.commit()
    for i in range(0,500):
        c.execute("INSERT INTO table1 VALUES (3, 1, 1, 1, 1)")
        conn.commit()
        c.execute("INSERT INTO table2 VALUES (3, 1, 1, 1, 1)")
        conn.commit()

    conn.commit()
    conn.close()

# make queries on table
def make_queries(param):
    conn = sqlite3.connect('db.sqlite')
    c = conn.cursor()

    start = time.time()

    c.execute('SELECT * FROM table1 INNER JOIN table2 ON table1.attr1 = table2.attr1 WHERE table1.attr1 = ' + str(param))

    return(time.time() - start)

main()
