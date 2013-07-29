moduleScripts['questions'] = {
    'view' : {
    	'questionFull': [
			configureInputField,
			configureQuestionStarToggler,
	    ],
		'set': {
			'full': [
			    configureQuestionSetView
			],
			'list': [
			    configureQuestionSetLoader,
			]
    	}

	},
    'search' : {
    	'main': [
	        searchSetup,
	        questionShortSetup,
	    ]
	}
}