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
			    configureForkSetForm,
			    configureDeleteSetForm,
			],
			'list': [
			    configureQuestionSetLoader,
			    configureDatePickerFields,
			    configureSetSearchPages,
			    configureSetSearchButton,
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
			    configureSetCreator,
			],
		},
	    'question' : {
	    	'create' : [
                configureDataEditor,
                configureQuestionCreator,
	    	],
	    },
		    
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
