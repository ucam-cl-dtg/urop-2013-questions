moduleScripts['questions'] = {
    'view' : {
    	'questionFull': [
			configureInputField,
			configureQuestionStarToggler,
			configureInPlaceAnchors,
	    ],
		'set': {
			'full': [
			    configureRemoveQuestionButton,
			    configureEditSetForm,
			    configureSelectQuestion,
			    configureUseTabSubmitButton,
			    configureSetStarToggler,
			    configureEditQuestionForm,
			    configureCreateQuestionForm,
			    configureInPlaceAnchors,
			    configureDataEditor,
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
	},
	
}