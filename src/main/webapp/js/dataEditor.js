function configureDataEditor() {
	$('.custom.data-editor-type').on('change', function() {
		var type = $(this).children('.current').get(0).text();
		console.log(type);
	});
}
