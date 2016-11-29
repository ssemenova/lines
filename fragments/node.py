class Node:
    fragments = []
    description = []
    send = 0
    scan = 0

    def __init__(self, fragments=0):
        self.fragments = fragments

    def addFragment(self, fragment):
        self.fragments.append(fragment)

    def addFragDescription(self, description):
        self.fragmentDescription.append(description)
