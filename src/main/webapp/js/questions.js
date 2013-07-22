moduleScripts['questions'] = {
    'view' : {
    	'questionFull': [
		configureInputField,
	    ],
		'set': [
				configureRemoveQuestion,
				configureSelectQuestion
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

function configureRemoveQuestion () {
	$(document).on('click', '.remove-question-from-set', function() {
		alert('adsf');
	});
}

function configureSelectQuestion () {
	$(document).on('click', '.question-to-add-to-set', function() {
		$(this).children('.list-panel').toggleClass('success');
	});
}
