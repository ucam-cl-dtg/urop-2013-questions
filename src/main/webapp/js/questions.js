moduleScripts['questions'] = {
    'view' : {
    	'questionFull': [
		configureInputField,
	    ],
		'set': [
				configureRemoveQuestion,
				configureSelectQuestion,
				configureUseTabSubmitButton,
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
		
	});
}

function configureSelectQuestion () {
	$(document).on('click', '.question-to-add-to-set', function() {
		$(this).children('.list-panel').toggleClass('success');
	});
}

function configureUseTabSubmitButton() {
	$("#add-questions-to-set-button").on("click", function(e) {
		e.preventDefault();
		
		var selected = [];
		$(".list-panel.success").each(function() {
			selected.push($(this).parent().index());
		});
		
		$("input[name=questions]").prop("value", selected);
		
		$(this).parent().submit();
	});
}