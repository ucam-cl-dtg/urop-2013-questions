function reloadMathJax() {
	var elems = $('.latex-content').get();
	
	function loop(n) {
		if (n < elems.length) {
			MathJax.Hub.Typeset(elems[n], function() { loop(n+1); });
		}
	}
	loop(0);
}

function reloadMarkDown() {
	var converter = new Showdown.converter();

	$('.markdown-content').each(function() {
		$(this).html(converter.makeHtml($(this).html()));
		
		var links = $(this).find("a");
		links.attr("data-bypass", true);
		links.attr("target", "_blank");
	});
}

function reloadData() {
	reloadMarkDown();
	reloadMathJax();
}

function configureDataRenderer() {
	//reloadMarkDown();
	
	MathJax.Hub.Config({
		skipStartupTypeset: true,
		tex2jax: {
			inlineMath: [ ['$','$'], ["\\(","\\)"] ],
		    displayMath: [ ['$$','$$'], ["\\[","\\]"] ],
		    processEscapes: true
		}
	});
	
	MathJax.Hub.Configured();
	reloadData();
}