class Fragment:
    rows = []
    description = []

    def __init__(self):
        pass

    def addRow(self, row):
        self.rows.append(row)

    def printFrag(self):
        return self.rows
