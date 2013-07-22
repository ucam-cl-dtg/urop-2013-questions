moduleScripts['questions'] = {
    'view' : {
    	'questionFull': [
		configureInputField,
	    ]
	},
    'search' : {
    	'main': [
	        function() {
	            alert("It works");
	        },
	        function() {
	            confirm("Are you sure it works");
	        }
	    ]
	}
}


