var text;
var desc;
var $file;

function configureDataEditor() {
	
	$('form').on('change', '.data-editor-type', function(e) {
		var $editor = $(this).siblings('.data-editor');
		var type = e.target.value;
		var name = $editor.attr('data-name');
		
		if($editor.children('textarea[name='+name+'_text]').get().length > 0) {
			text = $editor.children('textarea').text();
		}
		
		if($editor.children('input[type=file]').get().length > 0) {
			$file = $editor.children('input[type=file]');
			desc = $editor.children('textarea[name='+name+'_desc]').val();
		}
		
		var data = {data: {type: type, data: text, description: (desc ? desc : "")}, name: name};
		$editor.html(soy.renderAsFragment(shared.data.editor, data));
		
		if (type == "FILE" && $file) {
			$editor.children('input[type=file]').remove();
			$editor.prepend($file);
		}
	});
	
	$('form').on('change', 'input[type=file]', function(e) {
		var name = $(this).parents('.data-editor').attr('data-name');
		
		var filename = e.target.value.split('\\').slice(-1)[0];
		var extension = filename.split('.').slice(-1)[0];
		if ($(this).siblings('textarea[name='+name+'_desc]').val() == "") {
			$(this).siblings('textarea[name='+name+'_desc]').val(filename);
		}
		$(this).siblings('input[name='+name+'_ext]').attr('value', extension);
		
	});
	
}
