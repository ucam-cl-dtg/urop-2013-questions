#!/usr/bin/python

"""
Import Security 2 questions from latex source provided by fms27
"""

import sys
import stajano

def security2(filename):
    tripos = {}
    stajano.process(filename,"Security II - Supervision %s","Security II",tripos)

if __name__ == "__main__":
    security2(sys.argv[1])


