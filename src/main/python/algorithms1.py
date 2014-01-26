#!/usr/bin/python

"""
Import Algorithms 1 questions from latex source provided by fms27
"""

import sys
import stajano

def algorithmsI(filename):
    tripos = {}
    stajano.process(filename,"Algorithms I - Supervision %s","Algorithms I",tripos)

if __name__ == "__main__":
    algorithmsI(sys.argv[1])


