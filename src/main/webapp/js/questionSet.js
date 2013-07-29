function reloadView (set) {
	applyTemplate($("#set-content-tab"), "questions.view.set.tab.plan.full", set);
	applyTemplate($("#set-questions-tab"), "questions.view.set.tab.questions.full", set);
	applyTemplate($("#set-use-tab"), "questions.view.set.tab.use.full", set);
	applyTemplate($("#set-edit-tab"), "questions.view.set.tab.edit.full", set);
	applyTemplate($("#set-createquestion-tab"), "questions.view.set.tab.createquestion.full", set);
	applyTemplate($(".question-set-name"), "questions.view.set.name", set);
}

function configureRemoveQuestionButton () {
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
}

function configureEditSetForm () {
	$(document).on('click', '#edit-set-submit-button', function(e) {
		e.preventDefault();
		
		var questions = [];
		$(".sortable .panel-wrapper:not(.delete)").each(function() {
			var questionId = $(this).attr("data-question-id");
			if (questionId) {
				questions.push(questionId);
			}
		});
		$("input[name=questions]").attr("value", questions);
		
		var data = $("#set-edit").serialize();
		$.post ("/sets/update",	data, function (data) {
			if (data.success) {
				reloadView(data.set);
				successNotification("Set edited successfully");
			} else {
				successNotification(data.error);
			}
		}).fail(function (data) {
			console.log(data);
			errorNotification("Error while editing the file");
		});
	});
	
	$(".sortable").sortable();
}

function configureSelectQuestion () {
	$('.question-to-add-to-set').on('click', function() {
		$(this).children('.list-panel').toggleClass('success');
		$(this).toggleClass('success');
	});
}

function configureUseTabSubmitButton() {
	$("#add-questions-to-set-button").on("click", function(e) {
		e.preventDefault();
		
		var selected = [];
		$('.question-to-add-to-set.success').each(function() {
			var questionId = $(this).attr('data-question-id');
			if (questionId) {
				selected.push(questionId);
			}
		});
		
		if (selected.length > 0) {
			$("input[name=questions]").attr("value", selected);
			$(this).parent().submit();
		} else {
			alert("No questions were selected");
		}
	});
}

function configureSetStarToggler() {
	$('.star-question-button').on('click', function() {
		var setId = $(this).attr('data-set-id');
		var $star = $(this);
		$.get('/sets/'+setId+'/togglestar', function() {
			$star.find('i')
				.toggleClass('icon-star')
				.toggleClass('icon-star_empty');
		});
	});
}

function configureQuestionSetLoader() {
	$('.expand-question-list').on('click', function() {
		if ($(this).hasClass('loaded')) {
			$(this).parents('.panel-wrapper').find('.sub-panel').slideToggle();
		} else {
			var source = 'sets/'+$(this).attr('data-qid')+'/questions';
			var $questionList = $(this).parents('.panel-wrapper').children('.question-list');
			loadModule($questionList, source, 'questions.view.set.listquestions', function() {
				$(this).find('.sub-panel').slideToggle();
			});
			$(this).toggleClass('loaded');
		}
	});
}