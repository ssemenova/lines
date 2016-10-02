from sklearn import datasets
import sqlite3

def main():
    conn = setup_table()
    # make_queries(conn)

# set up table with test data
def setup_table():
    table1 = 'table1'
    table2 = 'table2'
    attr1 = 'attr1'
    attr2 = 'attr2'
    attr3 = 'attr3'
    attr4 = 'attr4'
    field_type = 'INTEGER'

    conn = sqlite3.connect('db.sqlite')
    c = conn.cursor()

    c.execute('CREATE TABLE {table1} (attr1 {ft} attr2 {ft} attr3 {ft} attr4 {ft} attr5 {ft} attr6 {ft} attr7 {ft})'.format(table1=table1, ft=field_type))
    c.execute('CREATE TABLE table2 AS SELECT * FROM table1 WHERE 0')

    for i in range(0,5):
        c.execute("INSERT OR IGNORE INTO {table} ({idf}, {cn}) VALUES (123456, 'test')".\
                format(table=table1, idf=id_column, cn=column_name))


    conn.commit()
    conn.close()

# # make queries on table
# def make_queries(conn):
#     c.
#     c.execute("""SELECT Clients.CompanyID, Clients.Forename, Clients.Surname, Clients.eMail, Company.CompanyID, Company.CompanyName
#                        FROM Clients
#                        INNER JOIN Company
#                        ON Clients.CompanyID = Company.CompanyID
#                        WHERE Clients.CompanyID = ?""",(data,))
#
main()
