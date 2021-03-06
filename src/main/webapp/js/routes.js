/*Demo Routes:

The first term must be the route from where the template will get the data.
The second term must be either a string representing the template name or
a function that returns the template name. The function will receive the json returned
by the request as the first parameter.
*/
SOY_GLOBALS = {URLPrefix: CONTEXT_PATH, UploadPrefix: '/api/uploads/'};

ROUTER_OPTIONS.pushState=true;

$(document).ready(function() {
	router = Router({
        /* "test(/:id)": "main.test",
        //For getting params in get requests
        "search?:params" : "main.searchtemplate",
        "tester": function(json) { return json['isSupervisor'] ? "a" : "b";}
        // Use the last line to redirect unmatched routes to an error page
        "*undefined": "errors.notfound"*/

		"": "questions.main",
		
		"q/:id" : "questions.view.question.full",
		"q/:id/:target" : "questions.view.question.full",
		"q/search((?)(:params(/))*)" : saveJSON("questions.search.main"),
		"q/add" : "questions.form.question.createpage",
		
		"sets((?)(:params(/))*)": saveJSON("questions.view.set.list"),
    	"sets/:id" : "questions.view.set.full",
    	"sets/:id/:target" : "questions.view.set.full",
    	"sets/:id/import(?:params)" : "questions.search.main",
    	"sets/add": "questions.form.set.create",

    	"users/:crsid" : "questions.view.user.full",
    	"users/me" : "questions.view.user.full",
    	
    	"fairytale" : "questions.misc.fairytale",
    	
    	"overview" : "questions.overview",
    	
    });
	
});
