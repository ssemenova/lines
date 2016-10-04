from sklearn import datasets
import sqlite3
import time
import random
import numpy as np
import matplotlib.pyplot as plt
import os

USE_IN_MEMORY = False

def main():
    db_exists = os.path.isfile("db.sqlite")
    conn = sqlite3.connect(':memory:' if USE_IN_MEMORY else 'db.sqlite')
    if USE_IN_MEMORY or not db_exists:
        setup_table(conn)

    x = []
    y = []
    for i in range(0,100):
        print("Executing query", i)
        randomParam = random.randrange(0,4)
        x.append(randomParam)
        y.append(make_queries(conn, randomParam))

    plt.figure()
    plt.scatter(x,y)
    plt.show()

# set up table with test data
def setup_table(conn):
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
    for i in range(0,3000):
        c.execute("INSERT INTO table1 VALUES (2, 1, 1, 1, 1)")
        c.execute("INSERT INTO table2 VALUES (2, 1, 1, 1, 1)")

    print("Inserting 3s...")
    for i in range(0,5):
        c.execute("INSERT INTO table1 VALUES (3, 1, 1, 1, 1)")
        c.execute("INSERT INTO table2 VALUES (3, 1, 1, 1, 1)")

    print("Committing...")
    conn.commit()
    print("Database ready!")

# make queries on table
def make_queries(conn, param):
    c = conn.cursor()

    start = time.time()

    c.execute('SELECT COUNT(*) from table1, table2 WHERE table1.attr1 = table2.attr1 AND table1.attr1 > ?', (param,))
    c.fetchone()
    

    return(time.time() - start)

main()
