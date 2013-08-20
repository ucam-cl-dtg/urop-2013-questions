function configureQuestionCreator() {
	$createButton = $('#create-question-button');
	$createButton.click(function(e) {
		e.preventDefault();
		if($createButton.hasClass("disabled")) return false;
		
		$createButton.toggleClass("disabled");
		$createButton.val("Loading...");
		
		var $form = $(this).parents('form');
		$form.ajaxSubmit({
			beforeSubmit: function (data, $form, opts) {
				opts.url = prepareURL($form.attr('action'));
			},
			success: function(data) {
				if (data.success) {
					router.navigate('q/'+data.question.id, {trigger: true});
					successNotification('Question created successfully');
				} else {
					errorNotification(data.error);
					$createButton.toggleClass("disabled");
					$createButton.val("Create new question");
				}
			},
			error: function(data) {
				errorNotification('Something went wrong');
				console.log(data);
				$createButton.toggleClass("disabled");
				$createButton.val("Create new question");
			}
		});
	});
}