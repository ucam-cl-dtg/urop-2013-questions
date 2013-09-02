import re
import subprocess
import psycopg2
import time

class QuestionSet:
    questionSetId = None
    dbOps = None

    def __init__(self,name,owner,tags,dbOps):
        self.questionSetId = dbOps.createQuestionSet(name,owner,tags)
        self.dbOps = dbOps

    def addQuestion(self,question):
        self.dbOps.addToQuestionSet(question.questionId,self.questionSetId)
        

class Question:
    question = None
    solution = None
    questionId = None
    dbOps = None
    tags = None
    owner = None

    def __init__(self,name,owner,dbOps):
        self.name = name
        self.owner = owner
        self.dbOps = dbOps

    def save(self):
        self.questionId = self.dbOps.insertQuestion(self.question,self.solution,self.owner)
        self.dbOps.createTags(self.questionId,self.tags)

    def latexToMarkdown(self):
        self.question = self._latexToMarkdown(self.question)
        self.solution = self._latexToMarkdown(self.solution)

    def _latexToMarkdown(self,latex):
        p1 = subprocess.Popen(['echo', latex], stdout=subprocess.PIPE)
        p2 = subprocess.Popen(['pandoc','-f','latex','-t','markdown'], stdin=p1.stdout, stdout=subprocess.PIPE)
        p1.stdout.close()
        md = p2.communicate()[0]
        md = re.sub("\\\\ "," ",md)
        return md

class DatabaseOps:
    con = None
    cur = None

    def __init__(self):
        self.con = psycopg2.connect(dbname='questions', user='questions',password='questions',host='localhost') 
        self.cur = self.con.cursor()
    
    def nextVal(self,table,column):
        self.cur.execute("SELECT max(%s) from %s" % (column,table))
        ids = self.cur.fetchone()
        if ids and ids[0]:
            return ids[0]+1
        return 1

    def findTag(self,name):
        self.cur.execute("SELECT id from TAGS where name=%s",[name])
        ids = self.cur.fetchone()
        if not ids:
            id = self.nextVal("tags","id")
            self.cur.execute("INSERT INTO TAGS(id,name) VALUES (%s,%s)",[id,name])
            return id
        else:
            return ids[0]

    def now(self):
        return time.strftime("%Y-%m-%d %H:%M")

    def createQuestionSet(self,name,owner,tags):
        id = self.nextVal("questionsets","id")
        self.cur.execute("INSERT INTO QUESTIONSETS(id,isstarred,name,timestamp,owner_id) VALUES (%s,%s,%s,%s,%s)",[id,"false",name,self.now(),owner])
        for tag in tags:
            tagId = self.findTag(tag)
            self.cur.execute("INSERT INTO QUESTIONSETS_TAGS(questionsets_id,tags_id) VALUES (%s,%s)",[id,tagId])
        return id

    def insertQuestion(self,question,solution,owner):
        id = self.nextVal("questions","id")
        self.cur.execute("INSERT INTO QUESTIONS(id,content_data,content_type,expectedduration,isstarred,notes_data,notes_type,timestamp,usagecount,owner_id) values (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)",[id,question,"MARKDOWN",20,"false",solution,"MARKDOWN",self.now(),0,owner])
        return id

    def createTags(self,questionId,tags):
        for tag in tags:
            tagid = self.findTag(tag)
            self.cur.execute("INSERT INTO QUESTIONS_TAGS(questions_id,tags_id) VALUES (%s,%s)",[questionId,tagid])
    
    def addToQuestionSet(self,questionId,questionSetId):
        placementId = self.nextVal("placement","id")
        self.cur.execute("SELECT max(place) from placement,questionsets_placement where questionsets_placement.questions_id = placement.id and questionsets_placement.questionsets_id =%s",[questionSetId])
        r = self.cur.fetchone()
        nextPlacement = r[0]+1 if r and r[0] else 1
        self.cur.execute("INSERT INTO placement(id,place,question_id) VALUES (%s,%s,%s)",[placementId,nextPlacement,questionId])
        self.cur.execute("INSERT INTO questionsets_placement(questionsets_id,questions_id) VALUES (%s,%s)",[questionSetId,placementId])
        
