function configureUserPage() {

	$("#user-content-div")
	
	.on("click", ".sets-page-numbers a.page-number", function(e) {
		e.preventDefault();
		var p = $(this).attr("data-p");
		var spp = $(this).parent().siblings(".results-per-page-select").val();
		loadUserSets(Number(p), Number(spp));
	})
	
	.on("change", ".sets-page-numbers .results-per-page-select", function(){
		var spp = $(this).val();
		loadUserSets(1, Number(spp));
	})
	
	.on("click", ".questions-page-numbers a.page-number", function(e){
		e.preventDefault();
		var p = $(this).attr("data-p");
		var spp = $(this).parent().siblings(".results-per-page-select").val();
		loadUserQuestions(Number(p), Number(spp));
	})
	
	.on("change", ".questions-page-numbers .results-per-page-select", function(){
		var spp = $(this).val();
		loadUserQuestions(1, Number(spp));
	});
}

function loadUserSets(page, amount){
	var crsid = $("#user-content-div").attr("data-crsid");
	var url = "users/" + crsid + "/sets" + "?page=" + page + "&amount=" + amount;
	var $setContent = $("#user-set-content");
	
	applyTemplate($setContent, "shared.util.loading", {});
	
	$.get(prepareURL(url), function(json){
		if(!json.success){
			errorNotification(json.error);
		}
		
		applyTemplate($setContent, "questions.view.user.sets", json);
		
		reloadData();
	});
}
function loadUserQuestions(page, amount){
	var crsid = $("#user-content-div").attr("data-crsid");
	var url = "users/" + crsid + "/questions" + "?page=" + page + "&amount=" + amount;
	var $questionContent = $("#user-question-content");
	
	applyTemplate($questionContent, "shared.util.loading", {});
	
	$.get(prepareURL(url), function(json){
		if(!json.success){
			errorNotification(json.error);
		}
		
		applyTemplate($questionContent, "questions.view.user.questions", json);
		
		reloadData();
	});
	
}