function configureInputField() {
	var $inputField = $(".main").find("#tags-input");
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
	
	var $addTagsButton = $(".main").find("#add-tags");
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
	
	$(".main").on("click", ".delete-tag", function(){
		$.post("/q/deltag", {"qid": $tagList.attr("data-qid"), "tag": $(this).attr("data-name")});
		//$.get("/q/deltag" + $tagList.attr("data-qid") + "&tag=" + $(this).attr("data-name"));
		$(this).parent().remove();
		return false;
	});
	
	$(".main").on("click", "#show-more-history", function(){
		loadMoreHistory(5, $(this));
		return false;
	});
	loadMoreHistory(5, $(".main").find("#show-more-history"));
	
	$(".main").on("click", "#show-more-forks", function(){
		loadMoreForks(5, $(this));
		return false;
	});
	loadMoreForks(5, $(".main").find("#show-more-forks"));
	
	$(".main").on("click", "#show-more-sets", function(){
		loadMoreSets(10, $(this));
		return false;
	});
	loadMoreSets(10, $(".main").find("#show-more-sets"));
	
	$(".main").on("click", ".list-panel.set-list a", function(e){
		e.stopPropagation();
	});
	
	$(".main").on("click", ".list-panel.set-list.unused", function(){
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
	$(".main").on("click", ".list-panel.set-list.success", function(){
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
	
	$(".main").on("click", "#minor-edit", function() {
		var $checkBox = $(this);
		var $setListDiv = $(".main").find("#set-div-to-edit");
		
		if($checkBox.attr("checked")){
			$checkBox.attr("checked", false);
			
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
			$checkBox.attr("checked", true);
			$setListDiv.children(".set-list-to-edit").slideToggle();
		}
	});
	
	$(".main").on("click", ".list-panel.qedit-set-list a", function(e){
		e.stopPropagation();
	});
	$(".main").on("click", ".list-panel.qedit-set-list.unused", function(){
		var $this = $(this);
		$this.removeClass("unused");
		$this.addClass("success");
		return false;
	});
	$(".main").on("click", ".list-panel.qedit-set-list.success", function(){
		var $this = $(this);
		$this.removeClass("success");
		$this.addClass("unused");
		return false;
	});
	
	$(".main").on("submit", "#revisions-submit-form", function() {
		/*$.post("/q/update", {
			"qid": $inputField.attr("data-qid"),
			"minor":  
		}).done(
			
		);*/
		//console.log($(this));
		//console.log($("textarea[name=console]"))
		//console.log($("input[name=expectedDuration]").val());
		$.post("/q/update", {
			"id": $inputField.attr("data-qid"),
			"minor": $(this).find("#edit-minor").val(),
			"setId": -1,
			"expectedDuration": $(this).find("#expDur").val(),
			"content": $(this).find("#edit-content").val(),
			"notes": $(this).find("#edit-notes").val()
		}).done(function(json){
			if(json.success){
				alert("Successfully edited question " + json.question.id + "!");
			} else {
				alert("There was a problem while editing question " + json.question.id + ":\n"
					+ "Error message: " + json.error);
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
		alert("Error while trying to " + type + " a question.");
		$element.removeClass("success");
		$element.removeClass("unused");
		$element.addClass("delete");
	}
}

function configureQuestionStarToggler() {
	$('.star-question-button').on('click', function() {
		var questionId = $(this).attr('data-question-id');
		var $star = $(this);
		$.post('/q/'+questionId+'/togglestar', function(data) {
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