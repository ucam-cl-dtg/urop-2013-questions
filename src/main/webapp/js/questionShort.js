function questionShortSetup() {
	
	$(".main").on("click", ".use-question", function() {
		//alert(document.URL);
		//console.log($(this).siblings());
		
		if($(this).siblings().hasClass('use-dialog')) {
			$(this).siblings('.use-dialog').slideToggle();
		} else {
			var newDialog = document.createElement("div");
			$(newDialog).hide().addClass("use-dialog");
			$(this).parent().append(newDialog);
			
			/*$.getJSON("/sets/mysets?contains=" + $(this).attr('data-qid'), function(data) {
				console.log(data);
				soy.renderElement(newDialog, shared.question.useDialog,data);
				$(newDialog).slideToggle();
			});*/
			
			loadModule($(newDialog), "sets/mysets?contains=" + $(this).attr('data-qid'),
					"shared.question.useDialog", function(){
				$(newDialog).slideToggle();
			});
			
		}
		
		//alert($(this).attr('qid'));
		return false;
	});
	
	$(".main").on("click", ".select-set", function() {
		//alert($(this).attr('contains'));
		var qid = $(this.parentNode.parentNode.parentNode.parentNode.parentNode).siblings('.use-question').attr('data-qid');
		var sid = $(this).attr('data-sid');
		
		function toggleComplete(type, successful){
			if(!successful){
				alert("Error while trying to " + type + " a question.");
				$(this).parent().css('background-color', '#FF9900');
			}
		}
		
		if($(this).attr('contains') == "true"){
			
			$(this).parent().css('background-color', '#D8D8D8');
			$(this).attr('contains', 'false');
			
			$.getJSON("/sets/remove?sid=" + sid + "&qid=" + qid, function(successful) {
				toggleComplete("remove", successful);
			});
			
		} else {
			
			$(this).parent().css('background-color', '#B4FF9C');
			$(this).attr('contains', 'true');
			
			$.getJSON("/sets/add?sid=" + sid + "&qid=" + qid, function(successful) {
				toggleComplete("add", successful);
			});
		}
		
		return false;
	});
	
}