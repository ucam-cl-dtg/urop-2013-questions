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
	        searchSetup,
	        questionShortSetup,
	    ]
	}
}

function configureRemoveQuestion () {
	$(document).on('click', '.remove-question-from-set', function() {
		$(this).parents('div.list-panel')
			.toggleClass('delete');
		
		$(this).parents('li.panel-wrapper')
		.toggleClass('delete');
		
		$(".sortable").sortable({
			cancel: "li.delete"
		});
		$( ".sortable li" ).disableSelection();
	});
	
	$(document).on('click', '#edit-set-submit-button', function(e) {
		e.preventDefault();
		
		var deleted = [];
		$(".panel-wrapper.delete").each(function() {
			var questionId = $(this).attr("data-question-id");
			if (questionId) {
				deleted.push(questionId);
			}
		});
		$("input[name=delete]").attr("value", deleted);
		
		var neworder = [];
		$(".panel-wrapper:not(.delete)").each(function() {
			var questionId = $(this).attr("data-question-id");
			if (questionId) {
				neworder.push(questionId);
			}
		});
		$("input[name=neworder]").attr("value", neworder);
		
		$(this).parents('form').submit();
	});
	
	$(".sortable").sortable();
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
		
		$("input[name=questions]").attr("value", selected);
		
		$(this).parent().submit();
	});
}
