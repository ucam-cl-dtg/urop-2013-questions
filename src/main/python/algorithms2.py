#!/usr/bin/python

"""
Import Algorithms 2 questions from latex source provided by fms27
"""

import sys
import stajano

def algorithmsII(filename):
    tripos = {
        '1' : ["2013-p3-q2",
               "2012-p3-q1",
               "2010-p3-q2", # a b
               "2009-p4-q1",
               "2009-p4-q2"],
        '2' : [
            "2013-p3-q1",
            "2010-p3-q1",
            "2010-p3-q2", # c d
            "2008-p4-q2" # = Paper 11 Question 8 
            ],
        '3' : [
            "2012-p3-q2",
            "2011-p3-q1",
            "2011-p3-q2",
            "2007-p4-q9" # = Paper 11 Question 10 
            ]
        }
    stajano.process(filename,"Algorithms II - Supervision %s","Algorithms II",tripos)

if __name__ == "__main__":
    algorithmsII(sys.argv[1])


