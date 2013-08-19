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
			    configureDatePickerFields,
			    configureSetSearchPages,
			    configureAutoCompleteBasic,
			    configureAutoCompleteAdv,
			    populateSearchFields,
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
	        populateSearchFields,
	        configureDatePickerFields,
	    ]
	},
	
};

configureAdvancedSearchExpand();
configureSetSearchButton();
