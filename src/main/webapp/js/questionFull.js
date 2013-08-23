function configureInputField() {
	var $inputField = $("#tags-input");
	$inputField.tokenInput(prepareURL("q/tagsnotin/" + $inputField.attr("data-qid")), {
		method: "post",
        queryParam: "q",
        tokenValue: "name",
        propertyToSearch: "name",
        theme: "facebook",
        minChars: 1,
        hintText: "Begin typing tags to add here!",
        noResultsText: "No tags found.",
        resultsLimit: 10,
        preventDuplicates: true,
        allowFreeTagging: true,
        
        resultsFormatter: function(item){ return "<li><div style='display: inline-block; padding-left: 10px;'><div class='tag_name'>" + item.name + "</div></li>"; },
        tokenFormatter: function(item) { return "<li>" + item.name + "</li>"; }         
	});
	
	var $addTagsButton = $("#add-tags");
	var $tagList = $(".main").find(".tags");
	$addTagsButton.click(function(){
		var $newtags = $("<div></div>");
		
		if($inputField.val().trim().length < 1){
			errorNotification("Please do not try to add empty tags.");
			return false;
		}
		
		$.post(prepareURL("q/addtags"), {"qid": $tagList.attr("data-qid"), "tags": $inputField.val()})
			.done(function(data){
				if(data.success){
					if(data.amount > 0){
						applyTemplate($newtags, "questions.view.tags", data);
						$tagList.append($newtags.children());
						successNotification("Successfully added " + data.amount + " tag(s)");
					} else {
						showNotification("No tags were added. This questions was probably already associated with these tags.");
					}
					$('li.token-input-token-facebook').remove();
				} else {
					errorNotification(data.error);
				}
			});
		
		return false;
	});
	
	$(".tags-cloud").on("click", ".delete-tag", function(){
		$.post(prepareURL("q/deltag"), {"qid": $tagList.attr("data-qid"), "tag": $(this).attr("data-name")});
		$(this).parent().remove();
		return false;
	});
	
	$("#show-more-history").click(function(){
		loadMoreHistory(5, $(this));
		return false;
	});
	loadMoreHistory(5, $("#show-more-history"));
	
	$("#show-more-forks").click(function(){
		loadMoreForks(5, $(this));
		return false;
	});
	loadMoreForks(5, $("#show-more-forks"));
	
	loadSetTabPageNumbers(1, 10);
	loadSetTabPage(1, 10);
	$(".page-numbers-export").on("click", "a.page-number", function(){
		var p = $(this).attr("data-p");
		var spp = $(this).parent().siblings(".results-per-page-select").val();
		loadSetTabPageNumbers(Number(p), Number(spp));
		loadSetTabPage(Number(p), Number(spp));
		return false;
	});
	$(".page-numbers-export").on("change", ".results-per-page-select", function(){
		var spp = $(this).val();
		loadSetTabPageNumbers(1, Number(spp));
		loadSetTabPage(1, Number(spp));
	});
	
	$("#sets-list").on("click", ".list-panel.set-list a", function(e){
		e.stopPropagation();
	});
	
	$("#sets-list").on("click", ".list-panel.set-list", function(){
		var $this = $(this);
		$this.toggleClass("success");
		$this.toggleClass("modified");
	});
	
	$("#save-export-state-button").click(function(){
		if($(this).hasClass("disabled")) return false;
		
		var setsToModify = [];
		$modifiedSets = $(".list-panel.set-list.modified"); 
		if($modifiedSets.length < 1){
			showNotification("Please select one or more sets in which to add or remove this question.");
			return false;
		}
		$modifiedSets.each(function(){
			setsToModify.push({sid: Number($(this).attr("data-sid")), useQuestion: $(this).hasClass("success")});
		});
		
		$.post(prepareURL("sets/addremove"), {
			qid: $inputField.attr("data-qid"),
			sets: JSON.stringify(setsToModify)
		}).done(function(json){
			$modifiedSets.removeClass("modified");
			updateEditTab();
			if(json.success){
				successNotification("Successfully added/removed question to/from set(s)!");
			} else {
				errorNotification("Error while trying to add/remove question(s).\n" +
					json.error);
				$modifiedSets.removeClass("success");
				$modifiedSets.addClass("delete");
				$(this).addClass("disabled");
			}
		});
		
		return false;
	});
	
	$("#edit-minor").change(function(data) {
		//var $checkBox = $(this);
		var $setListDiv = $(".main").find("#set-div-to-edit");
		
		if(data.target.value){
			
			if($setListDiv.children().hasClass("set-list-to-edit")){
				$setListDiv.children(".set-list-to-edit").slideToggle();
			}else{
				populateSetListToEdit($setListDiv);
			}
			
		}else{
			$setListDiv.children(".set-list-to-edit").slideToggle();
		}
	});
	
	$("#set-div-to-edit").on("click", ".list-panel.qedit-set-list a", function(e){
		e.stopPropagation();
	});
	$("#set-div-to-edit").on("click", ".list-panel.qedit-set-list.unused", function(){
		var $this = $(this);
		$this.removeClass("unused");
		$this.addClass("success");
		return false;
	});
	$("#set-div-to-edit").on("click", ".list-panel.qedit-set-list.success", function(){
		var $this = $(this);
		$this.removeClass("success");
		$this.addClass("unused");
		return false;
	});
	
	$("#revisions-submit-form").on("submit", function(e) {
		e.preventDefault();
		//var isMinor = $(this).find("#minor").val();
		var sets = [];
		$(".qedit-set-list.success").each(function(){
			sets.push($(this).attr("data-sid"));
		});
		
		/*if(isMinor == "false" && i == 0){
			errorNotification("Please either choose a set to edit this question in or only apply a minor change!");
			return false;
		}*/
		
		$("#content-section").attr("data-needsupdate", "true");
		
		$(this).ajaxSubmit({
			beforeSubmit: function(data, $form, opts) {
				data.push({
					name: "setId",
					required: true,
					type: "hidden",
					value: -1
				});
				data.push({
					name: "sets",
					required: true,
					type: "hidden", 
					value: sets.toString()
				});
				opts.url = prepareURL($form.attr('action'));
			},
			success: function(data) {
				if (data.success) {
					successNotification("Successfully edited question " + data.question.id);
					var curQuestionId = $("#form-qid").val();
					if(curQuestionId != data.question.id) router.navigate("/q/" + data.question.id, {trigger: true});;
					updateEditTab();
					updateContentTab(data);
				} else {
					errorNotification(data.error);
				}
			},
			error: function (data) {
				errorNotification("Something went wrong");
				console.log(data);
			}
		});
	});	
}

function loadMoreHistory(depth, $button){
	if($button.hasClass("disabled")) {
		return false;
	}
	$button.addClass("disabled");
	
	var $historyList = $(".main").find("#history-list");
	var $newQuestions = $("<div></div>");
	
	loadModule($newQuestions,
			"q/parents?qid=" + $historyList.attr("data-qid") + "&depth=" + depth,
			function(json) {
				
				if(json.exhausted) {
					$button.parent().append($("<p align='center'><i>--- End of History ---</i></p>"));
					$button.remove();
				}
				$historyList.attr("data-qid", json.last);
				
				json.showAddButton = false;
				return "shared.question.multiple";
			}, 
			function() {
				$historyList.append($newQuestions.find(".panels").children());
				$button.removeClass("disabled");
			});
	 
}

function loadMoreForks(amount, $button){
	if($button.hasClass("disabled")) {
		return false;
	}
	$button.addClass("disabled");
		
	var $forksList = $(".main").find("#forks-list");
	var $newQuestions = $("<div></div>");
	
	loadModule($newQuestions,
			"q/forks?qid=" + $forksList.attr("data-qid") + "&disp=" + $forksList.attr("data-disp") + "&amount=" + amount,
			function(json) {
				if(json.exhausted) {
					$button.parent().append($("<p align='center'><i>--- End of forks list ---</i></p>"));
					$button.remove();
				}
				$forksList.attr("data-disp", json.disp);
				
				json.showAddButton = false;
				return "shared.question.multiple";
			},
			function() {
				$forksList.append($newQuestions.find(".panels").children());
				$button.removeClass("disabled");
			});
}


function loadSetTabPageNumbers(curPage, setsPerPage){
	var $pageNums = $(".page-numbers-export");
	var maxPage;
	
	curPage = Number(curPage);
	
	$.get(prepareURL("sets/mysets/amount"), function(json){
		maxPage = Math.floor(json.amount / setsPerPage);
		if(json.amount % setsPerPage > 1){maxPage = Number(maxPage) + Number(1);}
		
		insertPageNumbers($pageNums, "set-page", curPage, maxPage, setsPerPage);
	});
}
function loadSetTabPage(page, amount){
	var $newSets = $("<div></div>");
	var $setsList = $(".main").find("#sets-list");
	$setsList.empty();
	$setsList.append("<div class='columns large-12 small-12'><i>Loading...</i></div>");
	loadModule($newSets,
			"sets/mysets/limited?page=" + page + "&amount=" + amount + "&contains=" + $setsList.attr("data-qid"),
			"shared.set.multipleHighlight",
			function() {
				$setsList.empty();
				$setsList.append($newSets.find(".panels").children());
			});
}

function configureQuestionStarToggler() {
	$('.star-question-button').on('click', function() {
		var $star = $(this);
		var data = {id: $star.attr('data-question-id')};
		$.post(prepareURL('q/togglestar'), data, function(data) {
			if (data.success) {
				if (data.starred) {
					$star.find('i')
						.addClass('icon-star')
						.removeClass('icon-star_empty');
				} else {
					$star.find('i')
						.removeClass('icon-star')
						.addClass('icon-star_empty');
				}
			} else {
				errorNotification(data.error);
			}
		}).fail(function(data) {
			errorNotification("Something went wrong");
			console.log(data);
		});
	});
}

function updateContentTab(json){
	applyTemplate($("#question-content"), "shared.data.display", {data: json.question.content});
	applyTemplate($("#question-notes"), "shared.data.display", {data: json.question.notes});
	applyTemplate($("#content-tab-expected-duration"), "questions.view.expDur", {expdur: json.question.expectedDuration});
	reloadData();
	
}
function updateEditTab(){
	$setListDiv = $("#set-div-to-edit");
	
	if($setListDiv.children().hasClass("set-list-to-edit")){
		$setListDiv.empty();
		if(!($("#edit-minor").is(":checked"))){
			populateSetListToEdit($setListDiv);
		}
	}
}

function populateSetListToEdit($setListDiv){
	var $newList = $("<div></div>");
	$setListDiv.empty();
	$setListDiv.append("<div class='columns large-12 small-12'><i>Loading...</i></div>");
	loadModule($newList,
			"sets/mysets/qlimited?qid=" + $("#form-qid").val(),
			"shared.set.setSelectionQuestionEdit",
			function(){
				$newList.find(".panels").hide();
				$newList.find(".panels").addClass("set-list-to-edit");
				$setListDiv.empty();
				$setListDiv.append($newList.children());
				$setListDiv.find(".panels").slideToggle();
			});
}