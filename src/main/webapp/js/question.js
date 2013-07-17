$(document).ready(function() {
	
	$(".main").on("click", ".use-question", function() {
		//alert(document.URL);
		if($(this).siblings().hasClass('use-dialog')) {
			$(this).siblings('.questions').slideToggle();
		} else {
			var newDialog = document.createElement("div");
			$(newDialog).hide().addClass("use-dialog");
			
			$.getJSON("/sets/mysets", function(data) {
				
			});
		}
		
		//alert($(this).attr('qid'));
		return false;
	});
	
});