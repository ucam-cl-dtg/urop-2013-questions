function configureUserPage() {
	loadUserSets(1, 25);
	loadUserQuestions(1, 25);
	$(".sets-page-numbers").on("click", "a.page-number", function(){
		var p = $(this).attr("data-p");
		var spp = $(this).parent().siblings(".results-per-page-select").val();
		loadUserSets(Number(p), Number(spp));
		return false;
	});
	$(".sets-page-numbers").on("change", ".results-per-page-select", function(){
		var spp = $(this).val();
		loadUserSets(1, Number(spp));
	});
	$(".questions-page-numbers").on("click", "a.page-number", function(){
		var p = $(this).attr("data-p");
		var spp = $(this).parent().siblings(".results-per-page-select").val();
		loadUserQuestions(Number(p), Number(spp));
		return false;
	});
	$(".questions-page-numbers").on("change", ".results-per-page-select", function(){
		var spp = $(this).val();
		loadUserQuestions(1, Number(spp));
	});
}

function loadUserSets(page, amount){
	var crsid = $("#user-content-div").attr("data-crsid");
	var url = "users/" + crsid + "/sets" + "?page=" + page + "&amount=" + amount;
	var $pageNums = $(".sets-page-numbers");
	var $setList = $("#user-set-list");
	
	$pageNums.empty();
	$setList.empty();
	$setList.append("<div class='columns large-12 small-12'><i>Loading...</i></div>");
	
	$.get(prepareURL(url), function(json){
		if(!json.success){
			errorNotification("Unexpected error: " + json.error);
			$setList.empty();
			$setList.append($("Unexpected error: " + json.error));
			return false;
		} else if(Number(json.totalSets < 1)){
			$setList.empty();
			$setList.append("<div class='columns large-12 small-12'><i>No sets were found here</i></div>");
			return false;
		}
		var maxPage = Math.floor(json.totalSets / amount);
		if(json.totalSets % amount > 1){maxPage = Number(maxPage) + Number(1);}
		insertPageNumbers($pageNums, "sets-page-num", page, maxPage, amount);
		$("#sets-search-link").empty();
		$("#sets-search-link").append("<a href='" + CONTEXT_PATH + "/sets?authors=" + crsid + "'>Click here to search within these sets</a>");
		
		var $newList = $("<div></div>");
		applyTemplate($newList, "shared.set.multiple", {sets: json.sets});
		
		$setList.empty();
		$setList.append($newList.children());
		
	});
	
	return false;
	
}
function loadUserQuestions(page, amount){
	var crsid = $("#user-content-div").attr("data-crsid");
	var url = "users/" + crsid + "/questions" + "?page=" + page + "&amount=" + amount;
	var $pageNums = $(".questions-page-numbers");
	var $setList = $("#user-question-list");
	
	$pageNums.empty();
	$setList.empty();
	$setList.append("<div class='columns large-12 small-12'><i>Loading...</i></div>");
	
	$.get(prepareURL(url), function(json){
		if(!json.success){
			errorNotification("Unexpected error: " + json.error);
			$setList.empty();
			$setList.append($("Unexpected error: " + json.error));
			return false;
		} else if(Number(json.totalQuestions < 1)){
			$setList.empty();
			$setList.append("<div class='columns large-12 small-12'><i>No questions were found here</i></div>");
			return false;
		}
		var maxPage = Math.floor(json.totalQuestions / amount);
		if(json.totalQuestions % amount > 1){maxPage = Number(maxPage) + Number(1);}
		insertPageNumbers($pageNums, "questions-page-num", page, maxPage, amount);
		$("#questions-search-link").empty();
		$("#questions-search-link").append("<a href='" + CONTEXT_PATH + "/q/search?authors=" + crsid + "'>Click here to search within these questions</a>");
		
		var $newList = $("<div></div>");
		applyTemplate($newList, "shared.question.multiple", {questions: json.questions});
		
		$setList.empty();
		$setList.append($newList.children());
		
	});
}