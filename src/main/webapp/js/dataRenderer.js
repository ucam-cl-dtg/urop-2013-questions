function reloadMathJax() {
	var i = 0;
	var curId;
	$(".latex-content").each(function(){
		curId = "latex-content-" + i;
		$(this).attr("id", curId);
		MathJax.Hub.Queue(["Typeset",MathJax.Hub,curId]);
		i++;
	});
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