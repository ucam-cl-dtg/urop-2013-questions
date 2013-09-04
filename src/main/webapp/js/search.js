function searchSetup() {
	$('#search-results').on('click', '.add-question-to-set', function() {
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
		
		var $form = $('#add-questions-to-set-form');
		
		$form.ajaxSubmit({
			beforeSubmit: function(data, $form, options) {
				options.url = prepareURL($form.attr('action'));
			},
			success: function(data) {
				if (data.success) {
					successNotification("Questions added successfully to a set");
				} else {
					errorNotification(data.error);
				}
			},
			error: function(data) {
				errorNotification("Something went wrong");
			}
		});
	});
	
	$("#search-results").on("click", "a.page-number", function(e){
		e.preventDefault();
		var p = $(this).attr("data-p");
		var rpp = $(this).parent().siblings(".results-per-page-select").val();
		search(Number(p), Number(rpp));
	});
	
	$("#search-results").on("change", ".results-per-page-select", function(e){
		var spp = $(this).val();
		search(1, Number(spp));
	});
	
	$("#question-search-button").on("click", function(e){
		e.preventDefault();
		var resultsPerPage = $("#search-results").find(".results-per-page-select").val();
		search(1, Number(resultsPerPage));
	});
}

var lastSearchURL;
function search(page, amount){
	if(!(page >=  1)){
		page = 1;
	}
	if(!(amount >= 1)){
		amount = 25;
	}
	
	var $button = $('#question-search-button');
	var $form = $button.parents('form');
	var $questionList = $('#search-results');
	
	applyTemplate($questionList, 'shared.util.loading', {});
	
	$form.ajaxSubmit({
		beforeSerialize: function($form, options) {
			if ( ! $button.parents('.search-buttons').hasClass('expanded')) {
				$form.find('input')
					.not('[type=radio]').not('[name=tags]').not('[type=submit]')
					.val('');
				
				$('.advanced-search input[name="authors"]').tokenInput("clear");
				$form.find('input[type=radio]').removeAttr('checked');
				$form.find('input[type=radio][value=DONT_CARE]').attr('checked','');
			}
		},
		beforeSubmit: function(data, $form, options) {
			options.url = prepareURL($form.attr('action'));
			data.push({name: "page", value: page});
			data.push({name: "amount", value: amount});
		},
		success: function(data) {
			applyTemplate($questionList, function(data) { console.log(data); return 'questions.search.results'} , data);
			if (data.form.totalAmount > 0) {
				displayPageNumbers(page, Number(data.form.totalAmount), amount);
			}
			router.navigate("q/search?"+$form.serialize() + "&page=" + page + "&amount=" + amount);
			lastSearchURL = "q/search?"+$form.serialize();
			reloadData();
		},
		error: function(data) {
			
		}
	});
}

function displayPageNumbers(page, totalAmount, amount){
	if(Number(totalAmount) <= 0){return;}
	var maxPage = Math.ceil(totalAmount / amount);
	insertPageNumbers($("#search-results").find(".page-numbers"), "search-page-number", page, maxPage, amount);
}

function displayResults(json){
	var $qlist = $("#questionList");
	var $pNumSections = $("#search-results").find(".page-numbers");
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

function configureAutoCompleteBasic(){
	$('.basic-search input[name="tags"]').tokenInput(prepareURL("tags/autocomplete/10"), {
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
		
		resultsFormatter: function(item){ return "<li><div class='st-value'>" + item.value + "</div></li>"; },
		tokenFormatter: function(item) { return "<li>" + item.value + "</li>"; }
    
	});
}

function configureAutoCompleteAdv(){
	$('.advanced-search input[name="authors"]').tokenInput(prepareURL("users/autocomplete/10"), {
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

function populateSearchFields(){
	if (!initialRequestData) {
		return false;
	}
	
	data = initialRequestData.form;
	var tags = data.tags.split(",");
	var authors = data.authors.split(",");
	var tmp;
	var adv = false;
	
	for(var i = 0; i < tags.length; i++){
		tmp = $.trim(tags[i]);
		if(tmp.length > 0){
			$('.basic-search input[name="tags"]').tokenInput("add", {value: tmp});
		}
	}
	
	for(var i = 0; i < authors.length; i++){
		tmp = $.trim(authors[i]);
		if(tmp.length > 0){
			adv = true;
			$.post(prepareURL("users/autocomplete/1"), {q: tmp}, function(json){
				$('.advanced-search input[name="authors"]').tokenInput("add", {crsid: json[0].crsid, name: json[0].name, value: json[0].value});
			});
		}
	}
	
	if(adv || data.minDuration.length > 0 || data.maxDuration.length > 0
			|| data.after.length > 0 || data.before.length > 0
			|| data.starred != "DONT_CARE" || data.supervisor != "DONT_CARE") {
		toggleSearchMode();
	}
	
}