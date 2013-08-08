moduleScripts['questions'] = {
    'view' : {
    	'questionFull': [
			configureInputField,
			configureQuestionStarToggler,
			configureInPlaceAnchors,
			configureDataEditor,
			configureDataRenderer,
			reloadMathJax,
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
			    configureSetTags,
			    configureDataEditor,
			    configureDataRenderer,
			    reloadMathJax,
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
	    ]
	},
	
};