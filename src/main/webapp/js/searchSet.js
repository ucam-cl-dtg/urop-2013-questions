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