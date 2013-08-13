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