function configureMathJax() {
	MathJax.Hub.Config({
		tex2jax: {
			inlineMath: [ ['$','$'], ["\\(","\\)"] ],
		    displayMath: [ ['$$','$$'], ["\\[","\\]"] ],
		    elements: $('.latex-content').get(),
		    processEscapes: true
		}
	});
	
	if ($('.latex-content').get().length > 0) {
		MathJax.Hub.Configured();
	}
	
}

function configureMarkDown() {
	var converter = new Showdown.converter();

	$('.markdown-content').each(function() {
		$(this).html(converter.makeHtml($(this).html()));
	});
}