function searchSetup() {
	$('#question-list').on('click', '.add-question-to-set', function() {
		$(this).parents('.list-panel').toggleClass('success');
	});
	
	$('#search-export-questions-form').on('click', '#export-questions-button', function(e) {
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
		$.post(prepareURL('sets/fork'), data, function(data) {
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
	$searchField.tokenInput(prepareURL("q/search/autocomplete/st"), {
		method: "post",
        queryParam: "q",
        tokenValue: "value",
        propertyToSearch: "value",
        minChars: 1,
        hintText: "Add a category in which to search...",
        resultsLimit: 10,
        preventDuplicates: true,
        
        resultsFormatter: function(item){ return "<li><div class='st-value'>" + item.value + "</div></li>"; },
        tokenFormatter: getTokenFormatter,
        onAdd: function(elem){
        	$(this).closest("form").find('.token-input-token[data-type="' + elem.type + '"]').find("#token-input-").focus();
        }
	});
	
	$(".page-numbers").on("click", "a.page-number", function(){
		var p = $(this).attr("data-p");
		var rpp = $(this).parent().siblings(".results-per-page-select").val();
		search(Number(p), Number(rpp));
		return false;
	});
	$(".page-numbers").on("change", ".results-per-page-select", function(){
		var spp = $(this).val();
		search(1, Number(spp));
	});
	
	populateSearchField($("#questions-searchform"));
	
	$("#questions-searchform").on("submit", function(){
		var resultsPerPage = $(".page-numbers").find(".results-per-page-select").val();
		search(1, Number(resultsPerPage));
		return false;
	});
	search();
	
}

function getTokenFormatter(item){
	var $criteria = $(questions.search.searchCriteria({item: item}));
	var $inputField = $criteria.find(".search-item-input-field");
	
	if($inputField.length > 0){	
		$inputField.tokenInput(prepareURL("q/search/autocomplete/" + $inputField.parents(".token-input-criteria-token-facebook").attr("data-type")), {
			method: "post",
			queryParam: "q",
			tokenValue: "value",
			propertyToSearch: "value",
			theme: "facebook",
			minChars: 1,
			hintText: "Begin typing your search terms...",
			noResultsText: "No results found",
			resultsLimit: 10,
			preventDuplicates: true,
			
			resultsFormatter: function(item){ return "<li>" + "<div style='display: inline-block; padding-left: 10px;'><div class='full_name'>" + item.name + " (" + item.crsid + ")</div><div class='email'>" + item.crsid + "@cam.ac.uk</div></div></li>" },
			tokenFormatter: function(item) { return "<li><p>" + item.name + " (" + item.crsid + ")</p></li>" }
        
		});
		
	}
	var $dateField = $criteria.find(".search-item-date");
	if($dateField.length > 0){
		$dateField.datepicker({dateFormat: "dd/mm/yy"});
	}
	
	return $criteria;
}

function search(page, amount){
	if(!(page >=  1)){
		page = 1;
	}
	if(!(amount >= 1)){
		amount = 25;
	}
	
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
	
	if(searchTerms == "?"){
		$("#questionList").empty();
		$("#questionList").append($("<div class='column large-12 small-12'><i>Please enter one or more search terms to find questions</i></div>"));
		return false;
	}
	
	var permalink = searchTerms + "&amount=" + amount + "&page=" + page;
	router.navigate("q/search" + permalink);
	
	$.get(prepareURL("q/find" + permalink), function(json){
		displayResults(json);
		
		displayPageNumbers(page, json.totalAmount, amount);
	});
	
	return false;
}

function displayPageNumbers(page, totalAmount, amount){
	var maxPage = Math.floor(totalAmount / amount);
	if(totalAmount % amount > 1){maxPage = Number(maxPage) + Number(1);}
	insertPageNumbers($(".page-numbers"), "search-page-number", page, maxPage, amount);
}
function displayResults(json){
	var $qlist = $("#questionList");
	var $pNumSections = $(".page-numbers");
	var $newList = $("<div></div>");
	
	$qlist.empty();
	$pNumSections.empty();
	
	applyTemplate($newList, function(data){
		if(!data.success){
			errorNotification(data.error);
		}
		return "questions.search.results";
	}, json);
	$qlist.append($newList.children());
	
}

function populateSearchField($searchForm){
	var argStr = window.location.search;
	
	argStr = decodeURIComponent(argStr).substring(1, argStr.length + 1);
	var args = argStr.split("&");
	
	var arg; 
	var page = 1; 
	var amount = 25;
	var argName;
	var touched = false;
	var $txtSearch = $searchForm.find("#txtSearch");
	for(var i = 0; i < args.length; i++){
		arg = args[i].substring(args[i].indexOf("=") + 1, args[i].length + 1);
		argName = args[i].substring(0, args[i].indexOf("="));
		if(argName == "tags"){
			
			touched = true;
			$txtSearch.tokenInput("add", makeTagSearch());
			var $tmp = $searchForm.find('.token-input-token[data-type="tags"]').find(".search-item-input-field");
			var tmp2 = arg.split(",");
			for(var j = 0; j < tmp2.length; j++){
				$tmp.tokenInput("add", {value: tmp2[j]});
			}
			
		}else if(argName == "after"){
			touched = true;
			$txtSearch.tokenInput("add", makeAfterSearch());
			$searchForm.find('.token-input-token[data-type="after"]').find(".search-item-date").datepicker("setDate", arg);
			
		}else if(argName == "before"){
			touched = true;
			$txtSearch.tokenInput("add", makeBeforeSearch());
			$searchForm.find('.token-input-token[data-type="before"]').find(".search-item-date").datepicker("setDate", arg);
			
		}else if(argName == "durmin"){
			touched = true;
			$txtSearch.tokenInput("add", makeDurMinSearch());
			$searchForm.find('.token-input-token[data-type="durmin"]').find(".search-item-number").val(arg);
			
		}else if(argName == "durmax"){
			touched = true;
			$txtSearch.tokenInput("add", makeDurMaxSearch());
			$searchForm.find('.token-input-token[data-type="durmax"]').find(".search-item-number").val(arg);
			
		}else if(argName == "usagemin"){
			touched = true;
			$txtSearch.tokenInput("add", makeUsageMinSearch());
			$searchForm.find('.token-input-token[data-type="usagemin"]').find(".search-item-number").val(arg);
			
		}else if(argName == "usagemax"){
			touched = true;
			$txtSearch.tokenInput("add", makeUsageMaxSearch());
			$searchForm.find('.token-input-token[data-type="usagemax"]').find(".search-item-number").val(arg);
			
		}else if(argName == "owners"){
			touched = true;
			$txtSearch.tokenInput("add", makeOwnersSearch());
			var $tmp = $searchForm.find('.token-input-token[data-type="owners"]').find(".search-item-input-field");
			var tmp2 = arg.split(",");
			for(var j = 0; j < tmp2.length; j++){
				$tmp.tokenInput("add", {value: tmp2[j]});
			}
			
		}else if(argName == "parents"){
			touched = true;
			$txtSearch.tokenInput("add", makeParentsSearch());
			var $tmp = $searchForm.find('.token-input-token[data-type="parents"]').find(".search-item-input-field");
			var tmp2 = arg.split(",");
			for(var j = 0; j < tmp2.length; j++){
				$tmp.tokenInput("add", {value: tmp2[j]});
			}
			
		}else if(argName == "star"){
			touched = true;
			$txtSearch.tokenInput("add", makeStarSearch());
			if(arg == "false"){
				$searchForm.find('.token-input-token[data-type="star"]').find(".search-item-checkbox").attr("checked", false);
			}
			
		}else if(argName == "supervisor"){
			touched = true;
			$txtSearch.tokenInput("add", makeSupervisorSearch());
			if(arg == "false"){
				$searchForm.find('.token-input-token[data-type="supervisor"]').find(".search-item-checkbox").attr("checked", false);
			}
		}else if(argName == "sc"){
			if(arg == "all"){
				touched = true;
				$txtSearch.tokenInput("add", {value: "sc", helpText: "sc", type: "sc", displayType: "text"});
				$searchForm.find('.token-input-token[data-type="sc"]').find(".search-item-input-field").tokenInput("add", {value: "all"});
			}
		}else if(argName == "page"){
			page = arg;
		}else if(argName == "amount"){
			amount = arg;
		}
	}
	if(!touched){
		$txtSearch.tokenInput("add", makeTagSearch());
	}
	
	search(Number(page), Number(amount));
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