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
	'form' : {
		'set' : {
			'create' : [
			    configureDataEditor,
			    configureSetCreator
			],
		}
		    
	},
    'search' : {
    	'main': [
	        searchSetup,
	        questionShortSetup,
	    ]
	},
	
}