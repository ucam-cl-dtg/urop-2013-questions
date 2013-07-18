$(document).ready(function() {
	
	$(".main").on("click", ".use-question", function() {
		//alert(document.URL);
		//console.log($(this).siblings());
		if($(this).siblings().hasClass('use-dialog')) {
			$(this).siblings('.use-dialog').slideToggle();
		} else {
			var newDialog = document.createElement("div");
			$(newDialog).hide().addClass("use-dialog");
			$(this).parent().append(newDialog);
			
			$.getJSON("/sets/mysets?contains=" + $(this).attr('qid'), function(data) {
				console.log(data);
				soy.renderElement(newDialog, shared.question.useDialog,data);
				$(newDialog).slideToggle()
			});
		}
		
		//alert($(this).attr('qid'));
		return false;
	});
	
	$(".main").on("click", "select-set", function() {
		
	});
	
});