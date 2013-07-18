$(document).ready(function() {
	//alert("reload");
	$(".main").on("click", ".toggle-question", function() {
		//alert(document.URL);
		alert($(this).attr('foobar'));
		return false;
	});
	$(".main").on('submit', "#questions-searchform", function(){
		var qlist = $("#questionList");
		qlist.empty();
		var newList = document.createElement("div");
		
		var searchTerms = "?";
		searchTerms = searchTerms + "tags=" + $("#tags").val();
		var permalink = window.location.hash;
		if(permalink.indexOf("?") > 0) {
			permalink = permalink.substring(0, window.location.hash.indexOf("?"));
		}
		permalink = permalink + searchTerms; 
		//alert(window.location.hash.indexOf("?"));
		
		$.getJSON("/q/search" + searchTerms, function(data) {
			soy.renderElement(newList, search.results, 
				{questions: data.questions, permalink: "/app/" + permalink}
			);
		});
		
		//window.location.hash = permalink;
		
		
		//window.location.search = searchTerms;
		
		qlist.append(newList);
		return false;
	});
	
	
});