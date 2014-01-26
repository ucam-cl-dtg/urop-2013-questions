#!/usr/bin/python

"""
Import questions from latex source provided by fms27
"""

import sys
import re
from questions import *
        
def check(question):
    for field in [question.question,question.solution]:
        if re.search("\\\\defexercise",field):
            raise Exception("Parse error")

def process(filename,titlePattern,tag,triposQuestions):

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
                    questionSet = QuestionSet(titlePattern % sv,"fms27",[tag],db)
                    try:
                        for triposCode in triposQuestions[sv]:
                            id = db.lookupQuestionId(triposCode)
                            db.addToQuestionSet(id,questionSet.questionSetId)
                    except KeyError:
                        pass
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
                currentQuestion.tags = [tag]
                currentQuestion.save()
                questionSet.addQuestion(currentQuestion)
                currentQuestion = None
            else: # accumulating solution
                currentQuestion.solution += line

    db.con.commit()


