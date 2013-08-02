function reloadView (set) {
	set.editable = $('star-question-button').attr('data-enabled');
	applyTemplate($("#set-plan-tab").children(".content"), "questions.view.set.tab.plan.full", set);
	applyTemplate($("#set-questions-tab").children(".content"), "questions.view.set.tab.questions.full", set);
	applyTemplate($("#set-use-tab").children(".content"), "questions.view.set.tab.use.full", set);
	applyTemplate($("#set-edit-tab").children(".content"), "questions.view.set.tab.edit.full", set);
	applyTemplate($("#set-createquestion-tab").children(".content"), "questions.view.set.tab.createquestion.full", set);
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
		
		$('#set-edit').ajaxSubmit({ 
			success: function (data) {
				console.log(data);
				if (data.success) {
					var executed = false;
					if ($(".list-panel-delete").size() == 0) {
						reloadView(data.set);
						successNotification("Set edited successfully");
					}
					$(".list-panel.delete").slideToggle(400, function() {
						if (!executed) {
							executed = true;
							reloadView(data.set);
							successNotification("Set edited successfully");
						}
					});
					
				} else {
					errorNotification(data.error);
					console.log(data);
				}
		}, error: function (data) {
			errorNotification("Something went wrong");
			console.log(data);
		}});
	});
	
	$(".sortable").sortable();
}

function configureSelectQuestion () {
	$(document).on('click', '.question-to-add-to-set', function() {
		$(this).children('.list-panel').toggleClass('success');
		$(this).toggleClass('success');
	});
}

function configureUseTabSubmitButton() {
	$(document).on("click", "#export-questions-button", function(e) {
		e.preventDefault();
		
		var selected = [];
		$('.question-to-add-to-set.success').each(function() {
			var questionId = $(this).attr('data-question-id');
			if (questionId) {
				selected.push(questionId);
			}
		});
		
		$("input[name=questions]").attr("value", selected);
		var data = $(this).parents("form").serialize();
		console.log(data);
		$.post("/sets/fork", data, function(data) {
			if (data.success) {
				$(".success").removeClass("success");
				console.log(data);
				successNotification("Questions exported successfully");
			} else {
				errorNotification(data.error);
			}
			
		}).fail(function(data) {
			errorNotification("Something went wrong");
			console.log(data);
		});
		
	});
}

function configureSetStarToggler() {
	$(document).on('click', '.star-question-button', function() {
		var editable = $(this).attr('data-enabled') == "true";
		if (editable) {
			var $star = $(this);
			var data = {id: $star.attr('data-set-id')};
			$.post('/sets/togglestar', data, function(data) {
				if (data.success) {
					if (data.starred) {
						$star.find('i')
							.addClass('icon-star')
							.removeClass('icon-star_empty');
					} else {
						$star.find('i')
							.removeClass('icon-star')
							.addClass('icon-star_empty');
					}
				} else {
					errorNotification(data.error);
				}
			}).fail(function(data) {
				errorNotification("Something went wrong");
				console.log(data);
			});
		}
	});
}

function configureQuestionSetLoader() {
	$(document).on('click', '.expand-question-list', function() {
		if ($(this).hasClass('loaded')) {
			$(this).parents('.panel-wrapper').find('.sub-panel').slideToggle();
		} else {
			var source = 'sets/'+$(this).attr('data-qid');
			var $questionList = $(this).parents('.panel-wrapper').children('.question-list');
			loadModule($questionList, source, 'questions.view.set.listquestions', function() {
				$(this).find('.sub-panel').slideToggle();
			});
			$(this).toggleClass('loaded');
		}
	});
}

function configureEditQuestionForm() {
	$(document).on('click', '#edit-question-button', function(e) {
		e.preventDefault();
		var data = $(this).parents("form").serialize();
		$.post("/q/update", data, function(data) {
			if(data.success) {
				var executed = false;
				$(".sub-panel:not(.hidden)").slideUp(function() {
					if (!executed) {
						executed = true;
						reloadView(data.set);
						successNotification("Question edited successfully");
					}
				});
			} else {
				alert("dupa");
				errorNotification(data.error);
			}
		}).fail(function (data) {
			errorNotification("Error while editing question");
			console.log(data);
		});
	});
}

function configureCreateQuestionForm() {
	$(document).on('click', '#add-question-button', function(e) {
		e.preventDefault();
		var data = $(this).parents("form").serialize();
		$.post("/q/save", data, function(data) {
			if (data.success) {
				reloadView(data.set);
				successNotification("Question added successfully");
				
			} else {
				errorNotification(data.error);
			}
		}).fail(function(data) {
			errorNotification("Something went wrong");
			console.log(data);
		});
	});
}

function configureInPlaceAnchors() {
	$(document).on('click', '.in-place-anchor', function() {
		var target = $(this).attr("href");
		router.navigate(target);
	});
}