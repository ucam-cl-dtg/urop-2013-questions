function configureInputField() {
	var $inputField = $(".main").find("#tags-input");
	$inputField.tokenInput("/q/tagsnotin", {
		method: "post",
        queryParam: $inputField.attr("data-qid"),
        tokenValue: "name",
        propertyToSearch: "name",
        theme: "facebook",
        minChars: 2,
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
		loadModule($newtags, 
				"q/addtags?qid=" + $tagList.attr("data-qid") + "&newtags=" + $inputField.val(),
				"questions.view.tags",
				function() {
					$tagList.append($newtags.children());	
					$('li.token-input-token-facebook').remove();		
				});
		return false;
	});
	
	$(".main").on("click", ".delete-tag", function(){
		$.get("/q/deltag?qid=" + $tagList.attr("data-qid") + "&tag=" + $(this).attr("data-name"));
		$(this).parent().remove();
		return false;
	});
	
	$(".main").on("click", "#show-more-history", function(){
		loadMoreHistory(5);
		return false;
	});
	loadMoreHistory(5);
	
	$(".main").on("click", "#show-more-forks", function(){
		loadMoreForks(5);
		return false;
	});
	loadMoreForks(5);
	
	$(".main").on("click", "#show-more-sets", function(){
		loadMoreSets(10);
		return false;
	});
	loadMoreSets(10);
	
	$(".main").on("click", ".list-panel.set-list a", function(e){
		e.stopPropagation();
	});
	
	$(".main").on("click", ".list-panel.set-list.unused", function(){
		var $this = $(this);
		$this.removeClass("unused");
		$this.addClass("success");
		$.getJSON("/sets/add?sid=" + $(this).attr("data-sid") + "&qid=" + $inputField.attr("data-qid"), function(successful) {
			toggleComplete("add", successful, $this);
		});
		return false;
	});
	$(".main").on("click", ".list-panel.set-list.success", function(){
		var $this = $(this);
		$this.removeClass("success");
		$this.addClass("unused");
		$.getJSON("/sets/remove?sid=" + $(this).attr("data-sid") + "&qid=" + $inputField.attr("data-qid"), function(successful) {
			toggleComplete("remove", successful, $this);
		});
		return false;
	});
	
	
	$(".main").on("submit", "#revisions-submit-form", function() {
		
		return false;
	});
	
	
}

function loadMoreHistory(depth){
	var $historyList = $(".main").find("#history-list");
	var $newQuestions = $("<div></div>");
	
	loadModule($newQuestions,
			"q/parents?qid=" + $historyList.attr("data-qid") + "&depth=" + depth,
			function(json) {
				
				if(json.exhausted) {
					$(".main").find("#show-more-history").remove();
				}
				$historyList.attr("data-qid", json.last);
				return "shared.question.multiple";
			}, 
			function() {
				$historyList.append($newQuestions.find(".panels").children());
			});
	 
}

function loadMoreForks(amount){
	var $forksList = $(".main").find("#forks-list");
	var $newQuestions = $("<div></div>");
	
	loadModule($newQuestions,
			"q/forks?qid=" + $forksList.attr("data-qid") + "&disp=" + $forksList.attr("data-disp") + "&amount=" + amount,
			function(json) {
				if(json.exhausted) {
					$(".main").find("#show-more-forks").remove();
				}
				$forksList.attr("data-disp", json.disp);
				return "shared.question.multiple";
			},
			function() {
				$forksList.append($newQuestions.find(".panels").children());
			});
}

function loadMoreSets(amount) {
	var $setsList = $(".main").find("#sets-list");
	var $newSets = $("<div></div>");
	
	loadModule($newSets,
			"sets/mysets/limited?disp=" + $setsList.attr("data-disp") + "&amount=" + amount + "&contains=" + $setsList.attr("data-qid"),
			function(json) {
				if(json.exhausted) {
					$(".main").find("#show-more-sets").remove();
				}
				$setsList.attr("data-disp", json.disp);
				return "shared.set.multipleHighlight";
			},
			function() {
				$setsList.append($newSets.find(".panels").children());
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