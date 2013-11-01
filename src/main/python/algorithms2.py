#!/usr/bin/python

"""
Import Algorithms 2 questions from latex source provided by fms27
"""

import sys
import re
from questions import *
        
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

def check(question):
    for field in [question.question,question.solution]:
        if re.search("\\\\defexercise",field):
            raise Exception("Parse error")

def main(filename):

    db = DatabaseOps()
    source = open(filename)
    currentQuestion = None
    currentSv = None
    questionSet = None


    for line in source:
        if currentQuestion == None:
            match = re.match("\\\\defexercise\\{(.*)\\}\\{(.*)\\}",line)
            if match:
                (exercise,sv) = (match.group(1),match.group(2))
                if sv != currentSv:
                    questionSet = QuestionSet("Algorithms II - Supervision %s" % sv,"fms27",["Algorithms II"],db)
                    for triposCode in tripos[sv]:
                        id = db.lookupQuestionId(triposCode)
                        db.addToQuestionSet(id,questionSet.questionSetId)
                    currentSv = sv
                currentQuestion = Question(exercise,"fms27",db)
                currentQuestion.question = ""
        elif currentQuestion.solution == None: # accumulating question
            if re.match("\\}\\{",line): # end of question
                currentQuestion.solution = ""
            else:
                currentQuestion.question += line
        elif currentQuestion.solution != None: # accumulating solution
            if re.match("\\}",line): # end of solution
                check(currentQuestion)
                currentQuestion.latexToMarkdown()
                currentQuestion.tags = ["Algorithms II"]
                currentQuestion.save()
                questionSet.addQuestion(currentQuestion)
                currentQuestion = None
            else: # accumulating solution
                currentQuestion.solution += line

    db.con.commit()

if __name__ == "__main__":
    main(sys.argv[1])


