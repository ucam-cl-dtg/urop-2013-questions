function searchSetup() {
	//alert("reload");
	$("#questions-searchform").on("submit", function(){
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
		permalink = permalink + searchTerms; 
		router.navigate(permalink);
		
		loadModule($(newList), "q/search" + searchTerms, "questions.search.results");
		
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
			
		});
	});
	
	var $searchField = $("#txtSearch");
	$searchField.tokenInput("/q/search/autocomplete", {
		method: "post",
        queryParam: "st",
        tokenValue: "value",
        propertyToSearch: "value",
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
	
	var $dateField = $criteria.find(".search-item-date");
	$dateField.datepicker({dateFormat: "dd/mm/yy"});
	
	return $criteria;
}