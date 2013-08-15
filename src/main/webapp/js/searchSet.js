function configureAdvancedSearchExpand() {
	$(document).on('click', '#advanced-search-expand', function() {
		var duration = 1000;
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
		
		var $form = $(this).parents('form');
		var $button = $(this);
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
			},
			success: function(data) {
				applyTemplate($('#question-set-list .panels'), 'questions.view.set.listsets', data);
				router.navigate("sets?"+$form.serialize());
			},
			error: function(data) {
				console.log(data);
			}
		});
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
	// TODO
	// 1. populate search fields
	// 2. make search request & insert page numbers and populate sets-list (call setSearch())
	//     -> remove call to .multiple in questions.view.set.list; this is now handled by JS here.
	displayPageNumbersSetSearch(1,1,1);
}
function setSearch(){
	// TODO
	// insert page numbers & populate sets-list here
}
function displayPageNumbersSetSearch(page, totalAmount, amountPerPage){
	var maxPage = Math.floor(totalAmount / amountPerPage);
	if(totalAmount % amountPerPage > 1){maxPage = Number(maxPage) + Number(1);}
	insertPageNumbers($(".page-numbers"), "set-search-page-number", page, maxPage, amountPerPage);
}