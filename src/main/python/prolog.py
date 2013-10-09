#!/usr/bin/python

"""
Import Prolog questions from xml source provided by acr31
"""

import lxml.etree as etree
import sys
import re
import os
from questions import *
        
def check(question):
    for field in [question.question,question.solution]:
        if re.search("\\\\defexercise",field):
            raise Exception("Parse error")

def namesToXPath(names,qtype):
    return "/exercises/section[" + " or ".join(map(lambda x:"@name='%s'" % x,names)) + "]/" + qtype +"/question"

def formatCodeBlock(code):
    result = ""
    code = re.sub("\t","        ",code)
    code = re.sub("^ *\n","",code)
    indent = re.search("^( *)",code).group(0)
    for line in code.split("\n"):
        line = "    "+re.sub(indent,"",line)
        result += line +"\n"
    return result

def xmlToMarkdown(node,sourceCode):
    result = node.text
    for n in node.iterchildren():
        if n.tag == "code":
            result += "`%s` " % n.text
        elif n.tag == "include":
            result += "\n" + formatCodeBlock(sourceCode[n.attrib["file"]])
        elif n.tag == "codeblock":
            result += formatCodeBlock(n.text)
        elif n.tag == "url":
            result += n.text
        else:
            raise Exception("New tag %s" % n.tag)
        result += n.tail.strip()+"\n"
    return result

def markupToLatex(text):
    text = re.sub("^<.*?>(.*)</.*?>","\\1",text,flags=re.DOTALL)
    text = re.subn("<code>(.*)</code>","\\\\verb^\\1^",text)[0]
    
    
    return text

def main(filenames):

    xml = filenames[0]
    sourceFiles = filenames[1:]

    sourceCode = {}

    for sourceFile in sourceFiles:
        f = file(sourceFile)
        sourceCode[os.path.basename(sourceFile)] = f.read()

    db = DatabaseOps()
    currentQuestion = None

    part1Names = ["Prolog Basics","Zebra Puzzle","Rules","Lists","Arithmetic","Backtracking","Generate and Test","Symbolic Evaluation"]
    part2Names = ["Cut","Negation","Databases","Countdown","Graph search","Difference lists","Sudoku","Constraints","Extra-fun"]

    sets = [ ("Supervision 1 - Review Questions",namesToXPath(part1Names,"review")),
             ("Supervision 1 - Supervision Questions",namesToXPath(part1Names,"supervision")),
             ("Supervision 2 - Review Questions",namesToXPath(part2Names,"review")),
             ("Supervision 2 - Supervision Questions",namesToXPath(part2Names,"supervision")) ]

    doc = etree.parse(xml)
 
    for (name,xpath) in sets:
        questionSet = QuestionSet("Prolog "+name,"acr31",["Prolog"],db)

        res = doc.xpath(xpath)

        for questionT in res:
            section = questionT.getparent().getparent().attrib['name']
            level = questionT.attrib["level"]

            question = Question("NAME","acr31",db)

            query = xmlToMarkdown(questionT.xpath("query")[0],sourceCode)
            question.question = section+"\n\n"+query

            try:
                solution = xmlToMarkdown(questionT.xpath("solution")[0],sourceCode)
                question.solution = solution
            except IndexError:
                pass

            question.tags = ["Prolog",level]
            question.save()
            questionSet.addQuestion(question)

    db.con.commit()


if __name__ == "__main__":
    main(sys.argv[1:])


