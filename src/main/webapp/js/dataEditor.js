function configureDataEditor() {
	$('.data-editor-type').on('change', function() {
		var $custom = $(this).siblings('.custom.data-editor-type');
		var type = $custom.children('.current').text();
		var $editor = $(this).siblings('.data-editor');
		if (type === "Plain text") {
			showNotification("Plain text");
		} else if (type === "Markdown") {
			showNotification("Markdown");
		} else {
			errorNotification("Invalid data type");
		}
	});
}
