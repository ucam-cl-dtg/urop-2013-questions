function reloadMathJax() {
	var elems = $('.latex-content').get();
	if (elems.length > 0) {
		MathJax.Hub.Typeset(elems);
	}
}

function reloadMarkDown() {
	var converter = new Showdown.converter();

	$('.markdown-content').each(function() {
		$(this).html(converter.makeHtml($(this).html()));
	});
}

function reloadData() {
	reloadMarkDown();
	reloadMathJax();
}

function configureDataRenderer() {
	reloadMarkDown();
	
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