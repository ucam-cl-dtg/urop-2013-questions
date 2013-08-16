function configureAdvancedSearchExpand() {
	$(document).on('click', '#advanced-search-expand', function() {
		var duration = 400;
		$(this).toggleClass('expanded');
		if ($(this).hasClass('expanded')) {
			$(this).text('Basic search...');
		} else {
			$(this).text('Advanced search...');
		}
		
		$('.search-tags-label').slideToggle(duration);
		$('.search').toggleClass('expanded', duration);
		$('.advanced-search').slideToggle({duration: duration, queue: false});
		
		if ($(this).hasClass('expanded')) {
			$(this).parents('.columns')
				.toggleClass('large-4', {duration: duration/2, easing: 'easeInQuad'})
				.toggleClass('large-10', {duration: duration/2, easing: 'easeOutQuad'});
		} else {
			$(this).parents('.columns')
				.toggleClass('large-10', {duration: duration/2, easing: 'easeInQuad'})
				.toggleClass('large-4', {duration: duration/2, easing: 'easeOutQuad'});
		}
	});
}

function configureSetSearchButton() {
	$(document).on('click', '#set-search-button', function(e) {
		e.preventDefault();
		var amount = Number($(".page-numbers").find("select.results-per-page-select").val());
		if(amount == Number("NaN") || amount < 1) amount = 25;
		setSearch(1, 25);
	});
}

function configureSetSearchFields() {
	$('#set-search-field-after').datepicker({
		onClose: function(selectedDate) {
			$('#set-search-field-before').datepicker('option', 'minDate', selectedDate);
		},
		dateFormat: "dd/mm/yy"
	});
	
	$('#set-search-field-before').datepicker({
		onClose: function(selectedDate) {
			$('#set-search-field-after').datepicker('option', 'maxDate', selectedDate);
		},
		dateFormat: "dd/mm/yy"
	});
}

function configureSetSearchPages() {
	var page = Number($("div.search.basic-search").attr("data-page"));
	var amount = Number($("div.search.basic-search").attr("data-amount"));
	var totalAmount = Number($("div.search.basic-search").attr("data-totalAmount"));
	if(page == Number("NaN")) page = 1;
	if(amount == Number("NaN")) amount == 25;
	if(totalAmount == Number("NaN")){
		errorNotification("There was an error while trying to retrieve the total amount of setes!");
		totalAmount == -1;
	}
	
	displayPageNumbersSetSearch(page, totalAmount, amount);
	
	$(".page-numbers").on("click", "a.page-number", function(){
		var p = $(this).attr("data-p");
		var spp = $(this).parent().siblings(".results-per-page-select").val();
		setSearch(p, spp);
		return false;
	});
	$(".page-numbers").on("change", ".results-per-page-select", function(){
		var spp = $(this).val();
		setSearch(1, spp);
		return false;
	});
}

function setSearch(page, amount){
	var $setList = $("#question-set-list");
	
	var $form = $("#set-search-form");
	var $button = $("#set-search-button");
	var datasent;
	
	$setList.empty();
	$setList.append("<div class='columns large-12 small-12'><i>Loading...</i></div>");
	
	$form.ajaxSubmit({
		beforeSerialize: function($form, options) {
			if ( ! $button.parents('.search-buttons').hasClass('expanded')) {
				$form.find('input')
					.not('[type=radio]').not('[name=tags]').not('[type=submit]')
					.val('');
				
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
			if(Number(data.form.totalAmount) > 0){
				$setList.empty();
				applyTemplate($setList, 'questions.view.set.listsets', data);
				displayPageNumbersSetSearch(data.form.page, data.form.totalAmount, data.form.amount);
			} else {
				$setList.empty();
				$(".page-numbers").empty();
				$setList.append($("<div class='columns large-12 small-12'><i>No results found with these search terms.</i></div>"));
			}
			router.navigate("sets?"+$form.serialize() + "&page=" + page + "&amount=" + amount);
		},
		error: function(data) {
			errorNotification("An error occurred!");
			console.log(data);
		}
	});
}

function displayPageNumbersSetSearch(page, totalAmount, amountPerPage){
	if(Number(totalAmount) <= 0){return;}
	var maxPage = Math.ceil(totalAmount / amountPerPage);
	insertPageNumbers($(".page-numbers"), "set-search-page-number", page, maxPage, amountPerPage);
}