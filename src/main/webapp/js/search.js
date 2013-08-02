function searchSetup() {
	$(".add-question-to-set").click(function() {
		$(this).parents('.list-panel').toggleClass('success');
	});
	
	$('#add-questions-to-set-button').click(function(e) {
		e.preventDefault();
		
		var selected = [];
		$('.list-panel.success').each(function() {
			var questionId = $(this).parent().attr('data-question-id');
			if (questionId) {
				selected.push(questionId);
			}
		});
		$('input[name=questions]').attr('value', selected);
		
		var data = $(this).parents('form').serialize();
		$.post('/sets/fork', data, function(data) {
			if (data.success) {
				successNotification("Questions added successfully to a set");
			} else {
				errorNotification(data.error);
			}
		}).fail(function(data) {
			errorNotification("Something went wrong");
			
		});
	});
	
	var $searchField = $("#txtSearch");
	$searchField.tokenInput("/q/search/autocomplete", {
		method: "post",
        queryParam: "st",
        tokenValue: "value",
        propertyToSearch: "value",
        minChars: 1,
        hintText: "Add a category in which to search...",
        noResultsText: "No results found",
        resultsLimit: 10,
        preventDuplicates: true,
        
        resultsFormatter: function(item){ return "<li><div class='st-value'>" + item.value + "</div></li>"; },
        tokenFormatter: getTokenFormatter,
        onAdd: function(elem){
        	$(this).closest("form").find('.token-input-token[data-type="' + elem.type + '"]').find("#token-input-").focus();
        }
	});
	
	populateSearchField($("#questions-searchform"));
	
	$("#questions-searchform").on("submit", function(){
		search();
		return false;
	});
	search();
}

function getTokenFormatter(item){
	var $criteria = $(questions.search.searchCriteria({item: item}));
	var $inputField = $criteria.find(".search-item-input-field");
	
	if($inputField.length > 0){	
		$inputField.tokenInput("/q/search/autocomplete", {
			method: "post",
			queryParam: $inputField.parents(".token-input-criteria-token-facebook").attr("data-type"),
			tokenValue: "value",
			propertyToSearch: "value",
			theme: "facebook",
			minChars: 1,
			hintText: "Begin typing your search terms...",
			noResultsText: "No results found",
			resultsLimit: 10,
			preventDuplicates: true,
			
			resultsFormatter: function(item){ return "<li><div class='st-value'>" + item.value + "</div></li>"; },
			tokenFormatter: function(item) { return "<li>" + item.value + "</li>"; }         
		});
		
	}
	var $dateField = $criteria.find(".search-item-date");
	if($dateField.length > 0){
		$dateField.datepicker({dateFormat: "dd/mm/yy"});
	}
	
	return $criteria;
}

function search(){
	var $qlist = $("#questionList");
	$qlist.empty();
	var newList = document.createElement("div");
	
	var searchTerms = "?";
	var $tokensList = $(".token-input-list");
	
	var val; var tmp;
	
	$tokensList.children(".token-input-token").each(function(){
		tmp = $(this).attr("data-type") + "=";
		val = null;
		
		for(var i = 0; i < 3; i++){
			switch(i){
				case 0: val = $(this).find(".search-item-input-field").val(); break;
				case 1: val = $(this).find(".search-item-number").val(); break;
				case 2: val = $(this).find(".search-item-date").val(); break;
			}
			if(val != null){break;}
		}
		
		if(val == null){
			var $tmp2 = $(this).find(".search-item-checkbox");
			if ($tmp2.length > 0){
				($tmp2.is(":checked") ? val = "true" : val = "false");
			}
		}
		
		if(val != null && val != ""){
			if(searchTerms != "?"){
				searchTerms = searchTerms + "&";
			}
			searchTerms = searchTerms + tmp + val;
		}
		
	});
	
	var permalink = window.location.hash;
	if(permalink.indexOf("?") > 0) {
		permalink = permalink.substring(0, window.location.hash.indexOf("?"));
	}
	
	if(searchTerms == "?"){
		$qlist.append($("<p>Type into search bar at top to find questions...</p>"));
		return false;
	}
	
	permalink = permalink + searchTerms; 
	router.navigate(permalink);
	
	loadModule($(newList), "q/search" + searchTerms, function(json){
		if(!json.success){
			errorNotification(json.error);
		}
		return "questions.search.results";
	});
	
	$qlist.append(newList);
	return false;
}

function populateSearchField($searchForm){
	var argStr = window.location.hash;
	argStr = argStr.substring(argStr.indexOf("?") + 1, argStr.length + 1);
	var args = argStr.split("&");
	var arg;
	var argName;
	var touched = false;
	for(var i = 0; i < args.length; i++){
		arg = args[i].substring(args[i].indexOf("=") + 1, args[i].length + 1);
		argName = args[i].substring(0, args[i].indexOf("="));
		if(argName == "tags"){
			
			touched = true;
			$searchForm.find("#txtSearch").tokenInput("add", makeTagSearch());
			var $tmp = $searchForm.find('.token-input-token[data-type="tags"]').find(".search-item-input-field");
			var tmp2 = arg.split(",");
			for(var j = 0; j < tmp2.length; j++){
				$tmp.tokenInput("add", {value: tmp2[j]});
			}
			
		}else if(argName == "after"){
			touched = true;
			$searchForm.find("#txtSearch").tokenInput("add", makeAfterSearch());
			$searchForm.find('.token-input-token[data-type="after"]').find(".search-item-date").datepicker("setDate", arg);
			
		}else if(argName == "before"){
			touched = true;
			$searchForm.find("#txtSearch").tokenInput("add", makeBeforeSearch());
			$searchForm.find('.token-input-token[data-type="before"]').find(".search-item-date").datepicker("setDate", arg);
			
		}else if(argName == "durmin"){
			touched = true;
			$searchForm.find("#txtSearch").tokenInput("add", makeDurMinSearch());
			$searchForm.find('.token-input-token[data-type="durmin"]').find(".search-item-number").val(arg);
			
		}else if(argName == "durmax"){
			touched = true;
			$searchForm.find("#txtSearch").tokenInput("add", makeDurMaxSearch());
			$searchForm.find('.token-input-token[data-type="durmax"]').find(".search-item-number").val(arg);
			
		}else if(argName == "usagemin"){
			touched = true;
			$searchForm.find("#txtSearch").tokenInput("add", makeUsageMinSearch());
			$searchForm.find('.token-input-token[data-type="usagemin"]').find(".search-item-number").val(arg);
			
		}else if(argName == "usagemax"){
			touched = true;
			$searchForm.find("#txtSearch").tokenInput("add", makeUsageMaxSearch());
			$searchForm.find('.token-input-token[data-type="usagemax"]').find(".search-item-number").val(arg);
			
		}else if(argName == "owners"){
			touched = true;
			$searchForm.find("#txtSearch").tokenInput("add", makeOwnersSearch());
			var $tmp = $searchForm.find('.token-input-token[data-type="owners"]').find(".search-item-input-field");
			var tmp2 = arg.split(",");
			for(var j = 0; j < tmp2.length; j++){
				$tmp.tokenInput("add", {value: tmp2[j]});
			}
			
		}else if(argName == "parents"){
			touched = true;
			$searchForm.find("#txtSearch").tokenInput("add", makeParentsSearch());
			var $tmp = $searchForm.find('.token-input-token[data-type="parents"]').find(".search-item-input-field");
			var tmp2 = arg.split(",");
			for(var j = 0; j < tmp2.length; j++){
				$tmp.tokenInput("add", {value: tmp2[j]});
			}
			
		}else if(argName == "star"){
			touched = true;
			$searchForm.find("#txtSearch").tokenInput("add", makeStarSearch());
			if(arg == "false"){
				$searchForm.find('.token-input-token[data-type="star"]').find(".search-item-checkbox").attr("checked", false);
			}
			
		}else if(argName == "supervisor"){
			touched = true;
			$searchForm.find("#txtSearch").tokenInput("add", makeSupervisorSearch());
			if(arg == "false"){
				$searchForm.find('.token-input-token[data-type="supervisor"]').find(".search-item-checkbox").attr("checked", false);
			}
		}
	}
	if(!touched){
		$searchForm.find("#txtSearch").tokenInput("add", makeTagSearch());
	}
}
function makeTagSearch(){
	return {value: "Tags", helpText: "Questions with certain tags", type: "tags", displayType: "text"};
}
function makeAfterSearch(){
	return {value: "After date", helpText: "Only questions after a certain date", type: "after", displayType: "date"};
}
function makeBeforeSearch(){
	return {value: "Before date", helpText: "Only questions before a certain date", type: "before", displayType: "date"};
}
function makeDurMinSearch(){
	return {value: "Min. duration", helpText: "Only questions with a certain min. duration in minutes", type: "durmin", displayType: "number"};
}
function makeDurMaxSearch(){
	return {value: "Max. duration", helpText: "Only questions with a certain max. duration in minutes", type: "durmax", displayType: "number"};
}
function makeUsageMinSearch(){
	return {value: "Min. usage", helpText: "Only questions being used min. _ times", type: "usagemin", displayType: "number"};
}
function makeUsageMaxSearch(){
	return {value: "Max. usage", helpText: "Only questions being used max. _ times", type: "usagemax", displayType: "number"};
}
function makeOwnersSearch(){
	return {value: "Authors", helpText: "Questions by certain authors", type: "owners", displayType: "text"};
}
function makeParentsSearch(){
	return {value: "Parent", helpText: "Only questions with certain parent question IDs", type: "parents", displayType: "text"};
}
function makeStarSearch(){
	return {value: "Starred", helpText: "Only starred/not starred questions", type: "star", displayType: "boolean"};
}
function makeSupervisorSearch(){
	return {value: "Supervisor", helpText: "Only questions by supervisors/students", type: "supervisor", displayType: "boolean"};
}