function reloadView (set) {
	set.editable = $('.star-question-button').attr('data-enabled');
	applyTemplate($("#set-plan-tab").children(".content"), "questions.view.set.tab.plan.full", set);
	applyTemplate($("#set-questions-tab").children(".content"), "questions.view.set.tab.questions.full", set);
	applyTemplate($("#set-tags-tab").children(".content"), "questions.view.set.tab.tags.full", set);
	applyTemplate($("#set-use-tab").children(".content"), "questions.view.set.tab.use.full", set);
	applyTemplate($("#set-edit-tab").children(".content"), "questions.view.set.tab.edit.full", set);
	applyTemplate($("#set-createquestion-tab").children(".content"), "questions.view.set.tab.createquestion.full", set);
	applyTemplate($(".question-set-name"), "questions.view.set.name", set);
	reloadData();
	var scripts = moduleScripts.questions.view.set.full;
	for (var i = 0; i < scripts.length; i++) {
		scripts[i]();
	}
}

function configureRemoveQuestionButton () {
	$('#set-edit-tab .panels').on('click', '.remove-question-from-set', function() {
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
	$('#set-edit-tab form').on('click', '#edit-set-submit-button', function(e) {
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
			beforeSubmit: function (data, $form, opts) {
				opts.url = prepareURL($form.attr('action'));
			},
			success: function (data) {
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
	$('#set-use-tab .panels').on('click', '.question-to-add-to-set', function() {
		$(this).children('.list-panel').toggleClass('success');
		$(this).toggleClass('success');
	});
}

function configureUseTabSubmitButton() {
	$('#set-use-tab .async-loader').on("click", "#export-questions-button", function(e) {
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
		var action = prepareURL($(this).parents("form").attr("action"));
		
		$.post(action, data, function(data) {
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
	$('.star-question-button').on('click', function() {
		var editable = $(this).attr('data-enabled') == "true";
		if (editable) {
			var $star = $(this);
			var data = {id: $star.attr('data-set-id')};
			$.post(prepareURL('sets/togglestar'), data, function(data) {
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
	$('#search-results').on('click', '.expand-question-list', function() {
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
	$('#set-questions-tab .panels').on('click', '#edit-question-button', function(e) {
		e.preventDefault();
		var $form = $(this).parents('form');
		$form.ajaxSubmit({
			beforeSubmit: function (data, $form, opts) {
				opts.url = prepareURL($form.attr('action'));
			},
			success: function(data) {
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
					errorNotification(data.error);
				}
			},
			error: function (data) {
				errorNotification("Error while editing question");
				console.log(data);
			}
		});
	});
}

function configureCreateQuestionForm() {
	$('#set-createquestion-tab form').on('click', '#create-question-button', function(e) {
		e.preventDefault();
		var $form = $(this).parents('form');
		console.log($form);
		$form.ajaxSubmit({
			beforeSubmit: function (data, $form, opts) {
				opts.url = prepareURL($form.attr('action'));
			},
			success: function(data) {
				if (data.success) {
					reloadView(data.set);
					successNotification("Question added successfully");
					
				} else {
					errorNotification(data.error);
				}
			},
			error: function(data) {
				errorNotification("Something went wrong");
				console.log(data);
			}
		});
	});
}

function configureInPlaceAnchors() {
	$('.in-place-anchor').click(function(evt) {
		//if(evt.ctrlKey) return true;
		if(evt.which ==  2) return;
		var target = $(this).attr("href");
		target = target.slice(0, CONTEXT_PATH.length) == CONTEXT_PATH ? target.slice(CONTEXT_PATH.length) : target;
		router.navigate(target);
	});
}

function configureSetCreator() {
	
	$('#create-set-button').click(function(e) {
		e.preventDefault();
		var $form = $(this).parents('form');
		$form.ajaxSubmit({
			beforeSubmit: function (data, $form, opts) {
				opts.url = prepareURL($form.attr('action'));
			},
			success: function(data) {
				//console.log(data);
				if (data.success) {
					loadModule($('.main'), 'sets/'+data.set.id, 'questions.view.set.full', function() {
						router.navigate('sets/'+data.set.id);
						successNotification('Set created successfully');
					});
				} else {
					errorNotification(data.error);
				}
			},
			error: function(data) {
				errorNotification('Something went wrong');
				console.log(data);
			}
		});
	});
}

function configureSetTags() {
	$('#set-tags-tab .tags').on('click', '.delete-tag', function() {
		var data = {
				tags: $(this).attr('data-name'),
				setid: $(this).parents('.tags').attr('data-setid')
		};
		$.post(prepareURL('sets/removetags'), data, function(data) {
			if (data.success) {
				applyTemplate($('.tags'), 'questions.view.set.tab.tags.list', {tags: data.set.tags});
				successNotification('Successfully removed tag(s)');
			} else {
				errorNotification(data.error);
			}
		}).fail( function(data) {
			errorNotification('Something went wrong');
			console.log(data);
		});
	});
	
	var $inputField = $("#tags-input");
	$inputField.tokenInput(prepareURL("sets/tagsnotin/" + $inputField.attr("data-setid")), {
		method: "post",
        queryParam: "q",
        tokenValue: "name",
        propertyToSearch: "name",
        theme: "facebook",
        minChars: 1,
        hintText: "Begin typing tags to add here!",
        noResultsText: "No tags found.",
        resultsLimit: 10,
        preventDuplicates: true,
        allowFreeTagging: true,
        
        resultsFormatter: function(item){ return "<li><div style='display: inline-block; padding-left: 10px;'><div class='tag_name'>" + item.name + "</div></li>"; },
        tokenFormatter: function(item) { return "<li>" + item.name + "</li>"; }         
	});
	
	$('#set-tags-tab .tag-search-panel').on('click', '#add-tags', function(e) {
		e.preventDefault();
		var data = {
				setid: $(this).parents('.question-set').attr('data-set-id'),
				tags: $('#tags-input').val()
		};
		$.post(prepareURL('sets/addtags'), data, function(data) {
			if (data.success) {
				applyTemplate($('.tags'), 'questions.view.set.tab.tags.list', {tags: data.set.tags});
				$('#tags-input').val('');
				$('li.token-input-token-facebook').remove();
				successNotification('Tags added successfully');
			} else {
				errorNotification(data.error);
			}
		}).fail(function(data) {
			errorNotification('Something went wrong');
			console.log(data);
		});
		return false;
	});
}

function configureForkSetForm() {
	$('#set-fork-tab').on('click', '#set-fork-button', function(e) {
		e.preventDefault();
		var $form = $(this).parents('form');
		//var $button = $(this);
		
		$form.ajaxSubmit({
			beforeSubmit: function(data, $form, options) {
				options.url = prepareURL($form.attr('action'));
			},
			success: function(data) {
				if (data.success) {
					router.navigate('sets/'+data.set.id, {trigger: true});
					successNotification("Successfully forked the set");
				} else {
					errorNotification(data.error);
				}
			},
			error: function(data) {
				errorNotification("Something went wrong");
				console.log(data);
			}
		});
	});
}

function configureDeleteSetForm() {
	$('#set-delete-tab').on('click', '#set-delete-button', function(e) {
		e.preventDefault();
		var $form = $(this).parents('form');
		
		$form.ajaxSubmit({
			beforeSubmit: function(data, $form, options) {
				options.url = prepareURL($form.attr('action'));
			},
			success: function(data) {
				if (data.success) {
					router.navigate('users/me', {trigger: true});
					successNotification("Successfully deleted set");
				} else {
					errorNotification(data.error);
				}
			},
			error: function(data) {
				errorNotification("Something went wrong");
				console.log(data);
			}
		});
	});
}
