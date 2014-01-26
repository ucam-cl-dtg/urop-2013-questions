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
        latex = re.sub(r"\\includegraphics(.*?)\{(.*?)\}",r"\includegraphics\1{/questions/api/uploads/\2.png}",latex)
        p1 = subprocess.Popen(['echo', latex], stdout=subprocess.PIPE)
        p2 = subprocess.Popen(['pandoc','-f','latex','-t','markdown'], stdin=p1.stdout, stdout=subprocess.PIPE)
        p1.stdout.close()
        md = p2.communicate()[0]
        md = re.sub("\\\\ "," ",md)
        md = re.sub(r"aligned\*","aligned",md)
        return md

class DatabaseOps:
    con = None
    cur = None

    def __init__(self):
        self.con = psycopg2.connect(dbname='questions', user='questions',password='questions',host='localhost') 
        self.cur = self.con.cursor()
    
    def nextVal(self,sequence):
        self.cur.execute("SELECT nextval('%s')" % (sequence))
        ids = self.cur.fetchone()
        if ids and ids[0]:
            return ids[0]
        return 1

    def findTag(self,name):
        self.cur.execute("SELECT id from TAGS where name=%s",[name])
        ids = self.cur.fetchone()
        if not ids:
            id = self.nextVal("tag_seq")
            self.cur.execute("INSERT INTO TAGS(id,name) VALUES (%s,%s)",[id,name])
            return id
        else:
            return ids[0]

    def now(self):
        return time.strftime("%Y-%m-%d %H:%M")

    def createQuestionSet(self,name,owner,tags):
        id = self.nextVal("set_seq")
        self.cur.execute("INSERT INTO QUESTIONSETS(id,isstarred,name,timestamp,owner_id,plan_data,plan_type) VALUES (%s,%s,%s,%s,%s,'','PLAIN_TEXT')",[id,"true",name,self.now(),owner])
        for tag in tags:
            tagId = self.findTag(tag)
            self.cur.execute("INSERT INTO QUESTIONSETS_TAGS(questionsets_id,tags_id) VALUES (%s,%s)",[id,tagId])
        return id

    def insertQuestion(self,question,solution,owner):
        id = self.nextVal("question_seq")
        self.cur.execute("INSERT INTO QUESTIONS(id,content_data,content_type,expectedduration,isstarred,notes_data,notes_type,timestamp,usagecount,owner_id) values (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)",[id,question,"MARKDOWN",20,"true",solution,"MARKDOWN",self.now(),0,owner])
        return id

    def createTags(self,questionId,tags):
        for tag in tags:
            tagid = self.findTag(tag)
            self.cur.execute("INSERT INTO QUESTIONS_TAGS(questions_id,tags_id) VALUES (%s,%s)",[questionId,tagid])
    
    def addToQuestionSet(self,questionId,questionSetId):
        placementId = self.nextVal("placement_seq")
        self.cur.execute("SELECT max(place) from placement,questionsets_placement where questionsets_placement.questions_id = placement.id and questionsets_placement.questionsets_id =%s",[questionSetId])
        r = self.cur.fetchone()
        nextPlacement = r[0]+1 if r and r[0] else 1
        self.cur.execute("INSERT INTO placement(id,place,question_id) VALUES (%s,%s,%s)",[placementId,nextPlacement,questionId])
        self.cur.execute("INSERT INTO questionsets_placement(questionsets_id,questions_id) VALUES (%s,%s)",[questionSetId,placementId])
        
    def lookupQuestionId(self,triposCode):
        self.cur.execute("SELECT id from questions where content_data like '%s%%'" % (triposCode))
        return self.cur.fetchone()[0]

    def deleteQuestion(self,questionId):
        # first we need to remove ourselves from the inheritance hierachy
        # replace the parent_id of any questions which reference us to be our parent_id
        self.cur.execute("SELECT parent_id from questions where id=%s",[questionId])
        parent_id = self.cur.fetchone()[0]

        # now update the parent_id of our chidren
        self.cur.execute("UPDATE questions set parent_id = %s where parent_id = %s",[parent_id,questionId])

        # now we need to remove our tags
        self.cur.execute("DELETE from questions_tags where questions_id = %s",[questionId])

        # now find out which question sets we are in
        self.cur.execute("SELECT distinct questionsets_id from questionsets_placement, placement where questionsets_placement.questions_id = placement.id and placement.question_id = %s",[questionId])
        ids = map(lambda x:x[0],self.cur.fetchall())

        # remove ourselves from these sets
        self.cur.execute("DELETE FROM questionsets_placement where questions_id in (select id from placement where question_id = %s)",[questionId])
        self.cur.execute("DELETE FROM placement where question_id = %s",[questionId])

        # now delete the question itself
        self.cur.execute("DELETE FROM questions where id = %s",[questionId])

        # now check questionsets - if we've removed all the questions from a question set then delete that too
        for setid in ids:
            self.cur.execute("SELECT count(*) from questionsets_placement where questionsets_id=%s",[setid])
            count = int(self.cur.fetchone()[0])
            if count == 0:
                # remove the tags
                self.cur.execute("DELETE from questionsets_tags where questionsets_id = %s",[setid])
                # remove the set
                self.cur.execute("DELETE from questionsets where id = %s",[setid])

        
    # delete all questions owned by this person which have these tags
    def deleteAllQuestions(self,owner,tags):
        tagids = []
        for tag in tags:
            tagids.append(self.findTag(tag))

        self.cur.execute("select id from questions where owner_id = %s",[owner])
        ids = map(lambda x:x[0],self.cur.fetchall())
        for qid in ids:
            if self.testTagIds(qid,tagids):                
                self.deleteQuestion(qid)
    
    def testTagIds(self,qid,tagids):
        for tagid in tagids:
            self.cur.execute("select count(*) from questions_tags where questions_id = %s and tags_id = %s", [qid,tagid])
            count = int(self.cur.fetchone()[0])
            if count == 0:
                return False
        return True
