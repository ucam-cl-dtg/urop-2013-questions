import re
import sys
import os
from subprocess import Popen, PIPE, STDOUT
from questions import *
import shutil

# Mapping topic names to equivalent current ones
TOPIC_MAP = {
    'Additional Topics I and II'		 : 'Additional Topics',
    'Artificial Intelligence'			 : 'Artificial Intelligence I',
    'Communicating Automata'			 : 'Communicating Automata and Pi Calculus',
    'Complexity Theory'				 : 'Complexity',
    'Computer System Modelling'			 : 'Computer Systems Modelling',
    'Computational Neuroscience'		 : 'Neural Computing',
    'Concurrency Theory'			 : 'Concurrency',
    'Digital Electronics and Computer Design'	 : 'Digital Electronics',
    'Formal Languages and Automata'		 : 'Regular Languages and Finite Automata',
    'Graphics I'				 : 'Computer Graphics and Image Processing',
    'Graphics II'				 : 'Computer Graphics and Image Processing',
    'Graphics'					 : 'Computer Graphics and Image Processing',
    'Graphics and Image Processing'		 : 'Computer Graphics and Image Processing',
    'HCI'					 : 'Human-Computer Interaction',
    'Introduction to Computer Architecture'	 : 'Computer Architecture',
    'Introduction to UNIX'			 : 'Introduction to Unix',
    'Java'					 : 'Programming in Java',
    'Pi Calculus'				 : 'Communicating Automata and Pi Calculus',
    'Programming Language Compilation'		 : 'Compiler Construction',
    'Programming in C'				 : 'Programming in C and C++',
    'Programming in ML'				 : 'Foundations of Computer Science',
    'Programming in Modula-3'			 : 'Modula-3',
    'Prolog'					 : 'Prolog for Artificial Intelligence',
    'Proving Programs Correct'			 : 'Specification and Verification I',
    'Running a Business'			 : 'Business Studies',
    'Semantics of Programming Languages'	 : 'Semantics',
    'Specification and Verification of Hardware' : 'Specification and Verification II',
    'UNIX Case Study'				 : 'Unix Case Study',
    'VLSI'					 : 'VLSI Design'
 }

class TriposQuestion:
    year = None
    author = None
    paper = None
    questionNumber = None
    topic = None
    question = None
    solution = None

    externalFiles = None

    def __init__(self,filename):
        rawTex = file(filename).read()

        # Some questions are redirections to others. Detect these
        # looking for files that start with an input directive (sans
        # comments)
        noComments = re.sub('^%.*\n','',rawTex,flags=re.MULTILINE)
        m = re.search('^\\\\input (\S+)',noComments)
        if m:
            path = os.path.split(filename)[0]
            name = m.group(1)
            name = re.sub('.tex$','',name)+".tex"
            rawTex = file(os.path.join(path,name)).read()

        m = re.search('\\\\begin\{question\}\[(.*?)\]{(.*?)}',rawTex)
        if m:
            self.__init_post2011(filename, m,rawTex)
        else:    
            m = re.search('^\{\\\\bf (.*?)\}',rawTex)
            if m:
                self.__init_bfquestion(filename,m,rawTex)
            else:
                m = re.search('% (.*)',rawTex)
                if m:
                    self.__init_commentquestion(filename,m,rawTex)
                else:
                    raise Exception("Unparseable "+filename)

        if TOPIC_MAP.has_key(self.topic):
            self.topic = TOPIC_MAP[self.topic]


    def __init_commentquestion(self,filename,m,rawTex):
        self.topic = m.group(1)
        self.question = latexToMarkdown(rawTex)
        m = re.search('p(\d+)q(\d+).tex',filename)
        self.questionNumber = int(m.group(2))
        self.paper = int(m.group(1))

        (pathToDay,questionName) = os.path.split(filename)
        (pathToYear,dayName) = os.path.split(pathToDay)
        self.year = os.path.split(pathToYear)[1]

    def __init_bfquestion(self,filename,m,rawTex):
        self.topic = m.group(1)
        self.questionNumber = int(re.search('p\d+q(\d+).tex',filename).group(1))

        (pathToDay,questionName) = os.path.split(filename)
        (pathToYear,dayName) = os.path.split(pathToDay)
        self.year = os.path.split(pathToYear)[1]

        nameMatch = re.match('p(\d+)q(\d+).tex',questionName)
        self.paper = nameMatch.group(1)

        rawTex = re.sub('^%.*','',rawTex,flags=re.MULTILINE)

        self.externalFiles = map(lambda x:(os.path.join(pathToDay,x),"%s-%s.png" % (self.year,x)),re.findall('\\\\psfig\{figure=([^,\}]*)',rawTex))

        rawTex = re.sub(' ?\\\\marks\{(\d+)\}',' [\\1 marks]',rawTex)
        rawTex = re.sub('\{\\\\bf.*\}','',rawTex)
        rawTex = re.sub('.*\\\\psfig\{figure=([^,\}]*).*\}','\\includegraphics{/questions/api/uploads/%s-\\1.png}' % self.year,rawTex)


        def input_replace(match):
            name = match.group(1)
            name = re.sub(".tex$",'',name)+".tex"
            fileName = os.path.join(pathToDay,name)
            data = file(fileName).read()
            return data

        rawTex = re.sub('\\\\input (\S+)',input_replace,rawTex)

        self.question = latexToMarkdown(rawTex)

        solutionName = os.path.join(pathToYear,"solutions",questionName)

        if os.path.exists(solutionName):
            solutionTex = file(solutionName).read()
            if re.match(" SETTER'S TEXT",solutionTex): 
                authorMatch = re.search('\\\\def\\\\lecturer\{(.*?)\}',solutionTex)
                self.author = authorMatch.group(1)

                solutionTex = re.sub("^.*SETTER'S TEXT[^\n]*",'',solutionTex,flags=re.DOTALL)
                solutionTex = re.sub('\\\\end\{document\}','',solutionTex)
                solutionTex = re.sub('\{alltt\}','{verbatim}',solutionTex)

            self.solution = latexToMarkdown(solutionTex)
                


    def __init_post2011(self,filename,m,rawTex):
        metaData = m.group(1)
        self.topic = m.group(2)
        self.year = re.search('year=(\d+)',metaData).group(1)
        self.author = re.search('author=([\d\w]+)',metaData).group(1)
        self.paper = re.search('paper=(\d+)',metaData).group(1)
        self.questionNumber = int(re.search('question=(\d+)',metaData).group(1))
        rawTex = re.sub('(\\\\begin\{question\}).*','\\1',rawTex)
        rawTex = re.sub('\\\\fullmarks\{(\d+)\}','[\\1 marks]',rawTex)
        rawTex = re.sub('\\\\label\{.*?\}','',rawTex)
        def _strip(removeAnswers):
            t = re.sub('(\\\\begin\{question\}).*','\\1',rawTex)
            t = re.sub('\\\\fullmarks\{(\d+)\}','[\\1 marks]',t)
            
            t = re.sub('\\\\includegraphics\{(.*?)\}','\includegraphics{/questions/api/uploads/\\1.png}',t)
            if removeAnswers:
                t = re.sub('\\\\begin\{answer\}.*?\\\\end\{answer\}','',t,flags=re.DOTALL)
            return t

        self.question = latexToMarkdown(_strip(True))
        self.solution = latexToMarkdown(_strip(False))
        path = os.path.split(filename)[0]
        self.externalFiles = map(lambda x:(os.path.join(path,x),x+".png"),re.findall('\\\\includegraphics\{(.*?)\}',rawTex))

def latexToMarkdown(latex):
     # remove double backslash linebreaks
     latex = re.sub('\\\\\\\\',"\n",latex)
     latex = re.sub('\\\\scalebox\{.*?\}\{(.*)\}','\\1',latex)
     latex = re.sub('\\\\begin\{tabular\}\{.*\}','\\\\begin{tabular}',latex)

     p = Popen(['pandoc','-f','latex','-t','markdown','-s'], stdout=PIPE, stdin=PIPE, stderr=STDOUT)
     return p.communicate(latex)[0]

def getPapers(rootDir):
    papers = {}
    for a in os.listdir(rootDir):
        if re.match('\d+',a):
            year = a
            for e in os.listdir(os.path.join(rootDir,a)):
                if re.match('DAY\d+',e):
                    for f in os.listdir(os.path.join(rootDir,a,e)):
                        m = re.match('p(\d+)q(\d+)\.tex',f)
                        if m:
                            fileName = os.path.join(rootDir,a,e,f)
                            paperName = m.group(1)
                            if len(paperName) == 1:
                                paperName = "0"+paperName
                            paperName = "%s-%s" % (year,paperName)
                            questionNumber = int(m.group(2))
                            if not papers.has_key(paperName):
                                papers[paperName] = {}
                            papers[paperName][questionNumber] = fileName
                m = re.match('\d\d\d\d-p(\d+)-q(\d+)-\w+\.tex',e)
                if m:
                    fileName = os.path.join(rootDir,a,e)
                    paperName = "%s-%s" % (year,m.group(1))
                    questionNumber = int(m.group(2))
                    if not papers.has_key(paperName):
                        papers[paperName] = {}
                    papers[paperName][questionNumber] = fileName
    return papers


def main(rootdir):
    db = DatabaseOps()

    papers = getPapers(rootdir)
    for paper in sorted(papers.keys()):
        set = QuestionSet("Past paper "+paper,"cst-papers",[paper],db)
        
        for question in sorted(papers[paper]):
            fileName = papers[paper][question]
            q = TriposQuestion(fileName)
            if not q.topic:
                q.topic = ""

            ottQuestion = Question("NAME","acr31",db)

            url = "http://www.cl.cam.ac.uk/teaching/exams/pastpapers/y%dp%dq%d.pdf" % (int(q.year),int(q.paper),int(q.questionNumber))

            title = "%d-p%d-q%d %s ([original version](%s))\n\n" % (int(q.year),int(q.paper),int(q.questionNumber),q.topic,url)
            
            ottQuestion.question = title + q.question
            ottQuestion.solution = q.solution
            ottQuestion.tags = [paper,q.topic]
            if q.author:
                ottQuestion.tags.append(q.author)
            ottQuestion.save()
            set.addQuestion(ottQuestion)

            if q.externalFiles:
                for (s,d) in q.externalFiles:
                    if os.path.exists(s) and not os.path.exists("/local/data/questions/%s" % d):
                        os.system("convert %s /local/data/questions/%s" % (s,d))

        db.con.commit()

if __name__ == "__main__":
    main(sys.argv[1])
