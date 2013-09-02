#!/usr/bin/python

"""
Import Algorithms 2 questions from latex source provided by fms27
"""

import sys
import re
from questions import *
        
def check(question):
    for field in [question.question,question.solution]:
        if re.search("\\\\defexercise",field):
            raise Exception("Parse error")

def main(filename):

    db = DatabaseOps()
    source = open(filename)
    currentQuestion = None

    questionSet = QuestionSet("Algorithms 2","fms27",["Algorithms2"],db)

    for line in source:
        if currentQuestion == None:
            match = re.match("\\\\defexercise\\{(.*)\\}",line)
            if match:
                currentQuestion = Question(match.group(1),"fms27",db)
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
                currentQuestion.tags = ["Algorithms2"]
                currentQuestion.save()
                questionSet.addQuestion(currentQuestion)
                currentQuestion = None
            else: # accumulating solution
                currentQuestion.solution += line

    db.con.commit()

if __name__ == "__main__":
    main(sys.argv[1])


