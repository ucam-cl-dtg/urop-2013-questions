/*Demo Routes:

The first term must be the route from where the template will get the data.
The second term must be either a string representing the template name or
a function that returns the template name. The function will receive the json returned
by the request as the first parameter.
*/

$(document).ready(function() {
	router = Router({
        /* "test(/:id)": "main.test",
        //For getting params in get requests
        "search?:params" : "main.searchtemplate",
        "tester": function(json) { return json['isSupervisor'] ? "a" : "b";}
        // Use the last line to redirect unmatched routes to an error page
        "*undefined": "errors.notfound"*/

		"": "questions.main",
		
		"q/search(?:params)" : "questions.search.main",
		"q/:id" : "questions.view.questionFull",
		"q/add/:setid" : "questions.form.question.add",
		
		"sets": "questions.view.set.list",
    	"sets/:id" : "questions.view.set.full",
    	"sets/add": "questions.form.set.create",
    	"fairytale": "questions.misc.fairytale",
    });
	
});