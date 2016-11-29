from __future__ import division
import sqlite3
import os
import sys
import csv
from node import Node
from fragment import Fragment
from operator import itemgetter
import random

USE_IN_MEMORY = False

def main(fragType, wltxt, dbcsv, numberOfNodes, valRangestxt=None, nodeToFragtxt=None):
    isValue = True if fragType == "value" else False  # hash vs. value fragmentation

    wl = []
    with open(wltxt, "r") as f:
        for row in f:
            wl.append(row)
    f.close()

    # if fragmentation type is value, open the value ranges txt file and read the attribute and ranges
    # then, for each range, create fragments and assign them to nodes round-robin style
    if (isValue):
        db = dbcsv[1:]
        dbAttributes = dbcsv[0]
        valRanges = []
        if (not valRangestxt):
            print("Need a file of value ranges if choosing the value fragmentation type")
            return
        with open(valRangestxt, "r") as f2:
            valAttr = f2.readline().rstrip("\n")
            for row in f2:
                valRanges.append(row.split("-"))
            valRanges = [[first, second.rstrip("\n")] for first, second in valRanges]
        f2.close()
        valAttrIndex = dbAttributes.index(valAttr)
        db = sorted(db, key=itemgetter(valAttrIndex)) # sort by value we want to fragment on
        fragments = []
        fragments.append(Fragment())
        i = 0
        for row in db:
            currValRange = valRanges[i]
            if ((row[valAttrIndex] >= int(currValRange[0])) and (row[valAttrIndex] <= int(currValRange[1]))):
                fragments[i].addRow(row)
            else:
                fragments.append(Fragment())
                i += 1
                fragments[i].description.append(currValRange)

        # for frag in fragments:
        #     print(frag.printFrag())
    # else:
        #hash?

    nodes = []
    fragmentsPerNode = len(fragments) // numberOfNodes
    i = 0
    for node in range(0,numberOfNodes):
        nodes.append(Node(fragments[i:i+fragmentsPerNode]))
        i += fragmentsPerNode

    for node in nodes:
        for fragment in node.fragments:
            node.description.append(fragment.description)

    for query in wl:
        if (">" in query):
            year = int(query.strip(">"))
            for node in nodes:
                checkYearRange(node, year, ">", valAttrIndex)
        elif ("<" in query):
            year = int(query.strip("<"))
            for node in nodes:
                checkYearRange(node, year, "<", valAttrIndex)
        elif ("-" in query):
            year = query.strip("-")
            for node in nodes:
                checkYearRange(node, year, "-", valAttrIndex)
        else:
            for node in nodes:
                checkYearRange(node, query, "=", valAttrIndex)

    for node in nodes:
        print("SEND = " + node.send)
        print("SCAN = " + node.scan)
        print("-----")

# I know this is bad bear with me
def checkYearRange(node, year, comparator, valueIndex):
    for description, frag in zip(node.description, node.fragments):
        if (comparator == ">"):
            if (int(description[0]) >= year):
                for row in frag:
                    node.scan += 1
                    if (row[valAttrIndex] >= year):
                        node.send += 1
        elif (comparator == "<"):
            if (int(description[0] <= year)):
                for row in frag:
                    node.scan += 1
                    if (row[valAttrIndex] <= year):
                        node.send += 1
        elif (comparator == "-"):
            yearStart = year[0]
            yearEnd = year[1]
            if ((yearStart >= description[0]) and (yearStart <= description[1]) or (yearEnd >= description[0]) and (yearEnd <= description[1])):
                for row in frag:
                    node.scan += 1
                    if ((row[valAttrIndex] >= yearStart) or (row[valAttrIndex] <= yearEnd)):
                        node.send += 1
        else:  # date range OR date equality
            if ((year >= description[0]) and (year <= description[1])):
                for row in frag:
                    node.scan += 1
                    if (row[valAttrIndex] == year):
                        node.send += 1

def generateDB():
    # First row of db should be attributes
    dbChanges = [["Something", "Year"]]
    numEntries = 200

    for i in range(0, numEntries):
        dbChanges.append([random.randrange(0,1000), random.randrange(1950, 2020)])

    return dbChanges

if __name__ == "__main__":
    fragType = "value"  # fragmentation type (hash or value)
    wltxt = "wl.csv"  # csv file of workload (given as a list of queries)
    dbcsv = generateDB()  # database saved as a csv
    valRangestxt = 'ranges.txt'  # if frag type is value, a csv file of value ranges in each fragment, with the first line of the csv being the value attribute
    # nodeToFragtxt = 'nodeToFragtxt.txt'  # txt file mapping nodes to their fragments - not implemented for now
    numberOfNodes = 5  # number of nodes
    main(fragType, wltxt, dbcsv, numberOfNodes, valRangestxt)
