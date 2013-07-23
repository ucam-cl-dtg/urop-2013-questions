function searchSetup() {
	//alert("reload");
	$(".main").on('submit', "#questions-searchform", function(){
		var $qlist = $("#questionList");
		$qlist.empty();
		var newList = document.createElement("div");
		
		var searchTerms = "?";
		searchTerms = searchTerms + "tags=" + $("#tags").val();
		var permalink = window.location.hash;
		if(permalink.indexOf("?") > 0) {
			permalink = permalink.substring(0, window.location.hash.indexOf("?"));
		}
		permalink = permalink + searchTerms; 
		//alert(window.location.hash.indexOf("?"));
		/*
		$.getJSON("/q/search" + searchTerms, function(data) {
			soy.renderElement(newList, search.results, 
				{questions: data.questions, permalink: "/app/" + permalink}
			);
		});*/
		loadModule($(newList), "q/search" + searchTerms, function(json){
			console.log(json);
			json.permalink = permalink;	
			return "questions.search.results";
		});
		//window.location.hash = permalink;
		
		
		//window.location.search = searchTerms;
		
		$qlist.append(newList);
		return false;
	});
	
	
}