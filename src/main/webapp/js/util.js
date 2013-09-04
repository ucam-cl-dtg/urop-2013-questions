function insertPageNumbers($container, className, page, maxPage, amount){
	var $newNumbers = $("<div></div>");
	$container.empty();
	
	var data = {curPage: Number(page), maxPage: Number(maxPage), resultsPerPage: Number(amount), className: className};
	
	applyTemplate($newNumbers, "shared.util.pageNumbers", data);
	$container.append($newNumbers.children());
}

var initialRequestData;
function saveJSON(template) {
	return function(data) {
		initialRequestData = data;
		return template;
	};
}

function configureExpanders() {
	$(document).on('click', '.expander', function(e) {
		$(this).siblings('#'+$(this).attr('for')).slideToggle();
		console.log($(this).attr('for'));
	});
}