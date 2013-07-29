moduleScripts['questions'] = {
    'view' : {
    	'questionFull': [
			configureInputField,
			configureQuestionStarToggler,
	    ],
		'set': {
			'full': [
			    configureRemoveQuestionButton,
			    configureEditSetForm,
				configureSelectQuestion,
				configureUseTabSubmitButton,
				configureSetStarToggler,
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