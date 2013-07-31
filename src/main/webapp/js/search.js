function searchSetup() {
	//alert("reload");
	$("#questions-searchform").on("submit", function(){
		var $qlist = $("#questionList");
		$qlist.empty();
		var newList = document.createElement("div");
		
		var searchTerms = "?";
		searchTerms = searchTerms + "tags=" + $("#txtSearch").val();
		var permalink = window.location.hash;
		if(permalink.indexOf("?") > 0) {
			permalink = permalink.substring(0, window.location.hash.indexOf("?"));
		}
		permalink = permalink + searchTerms; 
		//alert(window.location.hash.indexOf("?"));
		/*
		$.getJSON("/q/search" + searchTerms, function(data) {
			soy.renderElement(newList, search.results, 
				{questions: data.questions, permalink: "/app/" + permalink}
			);
		});*/
		router.navigate(permalink);
		
		loadModule($(newList), "q/search" + searchTerms, "questions.search.results");
		//window.location.hash = permalink;
		
		
		//window.location.search = searchTerms;
		
		$qlist.append(newList);
		return false;
	});
	
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
			console.log();
		});
	});
	
	var $searchField = $("#txtSearch");
	$searchField.tokenInput("/q/search/autocomplete", {
		method: "post",
        queryParam: "st",
        tokenValue: "value",
        propertyToSearch: "value",
        theme: "facebook",
        minChars: 1,
        hintText: "Begin typing your search terms...",
        noResultsText: "No results found",
        resultsLimit: 10,
        preventDuplicates: true,
        
        resultsFormatter: function(item){ return "<li><div class='st-value'>" + item.value + "</div></li>"; },
        tokenFormatter: getTokenFormatter
	});
	
}

function getTokenFormatter(item){
	var $criteria = $(questions.search.searchCriteria({item: item}));
	var $inputField = $criteria.find(".search-item-input-field");
	$inputField.tokenInput("/q/search/autocomplete", {
		method: "post",
        queryParam: $inputField.attr("data-type"),
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
	return $criteria;
}