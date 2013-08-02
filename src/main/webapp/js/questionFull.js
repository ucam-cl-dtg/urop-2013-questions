function configureInputField() {
	var $inputField = $("#tags-input");
	$inputField.tokenInput("/q/tagsnotin", {
		method: "post",
        queryParam: $inputField.attr("data-qid"),
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
	
	var $addTagsButton = $("#add-tags");
	var $tagList = $(".main").find(".tags");
	$addTagsButton.click(function(){
		var $newtags = $("<div></div>");
		
		$.post("/q/addtags", {"qid": $tagList.attr("data-qid"), "newTags": $inputField.val()})
			.done(function(data){
				applyTemplate($newtags, "questions.view.tags", data);
				$tagList.append($newtags.children());
				$('li.token-input-token-facebook').remove();
			});
		
		return false;
	});
	
	$(".delete-tag").click(function(){
		$.post("/q/deltag", {"qid": $tagList.attr("data-qid"), "tag": $(this).attr("data-name")});
		$(this).parent().remove();
		return false;
	});
	
	$("#show-more-history").click(function(){
		loadMoreHistory(5, $(this));
		return false;
	});
	loadMoreHistory(5, $("#show-more-history"));
	
	$("#show-more-forks").click(function(){
		loadMoreForks(5, $(this));
		return false;
	});
	loadMoreForks(5, $("#show-more-forks"));
	
	$("#show-more-sets").click(function(){
		loadMoreSets(10, $(this));
		return false;
	});
	loadMoreSets(10, $("#show-more-sets"));
	
	$("#sets-list").on("click", ".list-panel.set-list a", function(e){
		e.stopPropagation();
	});
	
	$("#sets-list").on("click", ".list-panel.set-list.unused", function(){
		var $this = $(this);
		$this.removeClass("unused");
		$this.addClass("success");
		$.post("/sets/fork", {
			"targetSetId": $this.attr("data-sid"), 
			"questions": $inputField.attr("data-qid")
		}).done(function(json){
			toggleComplete("add", json.success, $this);
		});
		return false;
	});
	$("#sets-list").on("click", ".list-panel.set-list.success", function(){
		var $this = $(this);
		$this.removeClass("success");
		$this.addClass("unused");
		$.post("/sets/remove", {
			"sid": $this.attr("data-sid"),
			"qid": $inputField.attr("data-qid")
		}).done(function(json){
			toggleComplete("remove", json.success, $this);
		});
		return false;
	});
	
	$("#edit-minor").change(function(data) {
		var $checkBox = $(this);
		var $setListDiv = $(".main").find("#set-div-to-edit");
		
		if(data.target.value){
			
			if($setListDiv.children().hasClass("set-list-to-edit")){
				$setListDiv.children(".set-list-to-edit").slideToggle();
			}else{
				
				var $newList = $("<div></div>");
				loadModule($newList,
						"sets/mysets/qlimited?qid=" + $inputField.attr("data-qid"),
						"shared.set.setSelectionQuestionEdit",
						function(){
							$newList.find(".panels").hide();
							$newList.find(".panels").addClass("set-list-to-edit");
							$setListDiv.append($newList.children());
							$setListDiv.find(".panels").slideToggle();
						});
			}
			
		}else{
			$setListDiv.children(".set-list-to-edit").slideToggle();
		}
	});
	
	$("#set-div-to-edit").on("click", ".list-panel.qedit-set-list a", function(e){
		e.stopPropagation();
	});
	$("#set-div-to-edit").on("click", ".list-panel.qedit-set-list.unused", function(){
		var $this = $(this);
		$this.removeClass("unused");
		$this.addClass("success");
		return false;
	});
	$("#set-div-to-edit").on("click", ".list-panel.qedit-set-list.success", function(){
		var $this = $(this);
		$this.removeClass("success");
		$this.addClass("unused");
		return false;
	});
	
	$("#revisions-submit-form").on("submit", function() {
		var isMinor = $(this).find("#minor").val();
		var sets = [];
		$(".qedit-set-list.success").each(function(){
			sets.push($(this).attr("data-sid"));
		});
		
		if(isMinor == "false" && i == 0){
			errorNotification("Please either choose a set to edit this question in or only apply a minor change!");
			return false;
		}
		
		$(this).ajaxSubmit({
			beforeSubmit: function(data, form, opts) {
				data.push({
					name: "setId",
					required: true,
					type: "hidden",
					value: -1
				});
				data.push({
					name: "sets",
					required: true,
					type: "hidden", 
					value: sets.toString()
				});
			},
			success: function(data) {
				if (data.success) {
					successNotification("Successfully edited question");
				} else {
					errorNotification(data.error);
				}
			},
			error: function (data) {
				errorNotification("Something went wrong");
				console.log(data);
			}
		});
		
		return false;
	});
	
	
}

function loadMoreHistory(depth, $button){
	if($button.hasClass("disabled")) {
		return false;
	}
	$button.addClass("disabled");
	
	var $historyList = $(".main").find("#history-list");
	var $newQuestions = $("<div></div>");
	
	loadModule($newQuestions,
			"q/parents?qid=" + $historyList.attr("data-qid") + "&depth=" + depth,
			function(json) {
				
				if(json.exhausted) {
					$button.parent().append($("<i>--- End of History ---</i>"));
					$button.remove();
				}
				$historyList.attr("data-qid", json.last);
				
				json.showAddButton = false;
				return "shared.question.multiple";
			}, 
			function() {
				$historyList.append($newQuestions.find(".panels").children());
				$button.removeClass("disabled");
			});
	 
}

function loadMoreForks(amount, $button){
	if($button.hasClass("disabled")) {
		return false;
	}
	$button.addClass("disabled");
		
	var $forksList = $(".main").find("#forks-list");
	var $newQuestions = $("<div></div>");
	
	loadModule($newQuestions,
			"q/forks?qid=" + $forksList.attr("data-qid") + "&disp=" + $forksList.attr("data-disp") + "&amount=" + amount,
			function(json) {
				if(json.exhausted) {
					$button.parent().append($("<i>--- End of forks list ---</i>"));
					$button.remove();
				}
				$forksList.attr("data-disp", json.disp);
				
				json.showAddButton = false;
				return "shared.question.multiple";
			},
			function() {
				$forksList.append($newQuestions.find(".panels").children());
				$button.removeClass("disabled");
			});
}

function loadMoreSets(amount, $button) {
	if($button.hasClass("disabled")) {
		return false;
	}
	$button.addClass("disabled");
	
	var $setsList = $(".main").find("#sets-list");
	var $newSets = $("<div></div>");
	
	loadModule($newSets,
			"sets/mysets/limited?disp=" + $setsList.attr("data-disp") + "&amount=" + amount + "&contains=" + $setsList.attr("data-qid"),
			function(json) {
				if(json.exhausted) {
					$button.parent().append("<i>--- End of set list ---");
					$button.remove();
				}
				$setsList.attr("data-disp", json.disp);
				return "shared.set.multipleHighlight";
			},
			function() {
				$setsList.append($newSets.find(".panels").children());
				$button.removeClass("disabled");
			});
}

function toggleComplete(type, successful, $element){
	if(!successful){
		errorNotification("Error while trying to " + type + " a question.");
		$element.removeClass("success");
		$element.removeClass("unused");
		$element.addClass("delete");
	}
}

function configureQuestionStarToggler() {
	$('.star-question-button').on('click', function() {
		var $star = $(this);
		var data = {id: $star.attr('data-question-id')};
		$.post('/q/togglestar', data, function(data) {
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
	});
}