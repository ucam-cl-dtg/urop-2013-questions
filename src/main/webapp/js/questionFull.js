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