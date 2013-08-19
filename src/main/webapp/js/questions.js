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
			    
			],
			'list': [
			    configureQuestionSetLoader,
			    configureSetSearchFields,
			    configureSetSearchPages,
			    configureAutoCompleteBasic,
			    configureAutoCompleteAdv,
			],
    	},
		'user': {
			'full': [
			    configureUserPage,
			 ],
		},
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
	        configureAutoCompleteBasic,
	        configureAutoCompleteAdv,
	    ]
	},
	
};

configureAdvancedSearchExpand();
configureSetSearchButton();
