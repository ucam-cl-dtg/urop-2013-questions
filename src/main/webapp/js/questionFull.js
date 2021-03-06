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
	
	var $newtags = $(document.createElement("div"));
	var $addTagsButton = $("#add-tags");
	var $tagList = $("#question-tags-list");
	$addTagsButton.click(function(){

		if($inputField.val().trim().length < 1){
			errorNotification("Please do not try to add empty tags.");
			return false;
		}
		
		$.post(prepareURL("q/addtags"), {"qid": $tagList.attr("data-qid"), "tags": $inputField.val()})
			.done(function(data){
				if(data.success){
					if(data.amount > 0){
						applyTemplate($newtags, "questions.view.question.tab.overview.tags", data);
						$newtags.children('.tag-small').each(function(i,tag) {
							$(tag).hide();
							$tagList.append(tag);
							$(tag).fadeIn();
						});
						$tagList.find('.add-tag-small')
							.detach()
							.appendTo($tagList)
							.fadeIn();
						$('.tag-search-panel').slideUp();
						successNotification("Successfully added " + data.amount + " tag(s)");
					} else {
						showNotification("No tags were added. This questions was probably already associated with these tags.");
					}
					$inputField.tokenInput("clear");
				} else {
					errorNotification(data.error);
				}
			});
		
		return false;
	});
	
	$("#question-tab-overview").on("click", ".delete-tag", function() {
		$.post(prepareURL("q/deltag"), {"qid": $tagList.attr("data-qid"), "tag": $(this).attr("data-name")});
		$(this).parent().fadeOut(function() {
			$(this).remove();
		});
		return false;
	});
	
	$("#question-tab-overview").on("click", ".add-tag-small", function() {
		$(this).closest('.row').siblings('.tag-search-panel').slideDown();
		$(this).fadeOut();
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
		if ( ! $setListDiv.children().hasClass("set-list-to-edit")) {
			populateSetListToEdit($setListDiv);
		} else {
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
		
		$("#overview-section").attr("data-needsupdate", "true");
		
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
	
	populateSetListToEdit($('#set-div-to-edit'));
}

function loadMoreHistory(depth, $button){
	if($button.hasClass("disabled")) {
		return false;
	}
	$button.addClass("disabled");
	
	var $historyList = $(".main").find("#history-list");
	var $newQuestions = $("<div></div>");
	var exhausted = false;
	
	loadModule($newQuestions,
			"q/parents?qid=" + $historyList.attr("data-qid") + "&depth=" + depth,
			function(json) {
				
				if(json.exhausted) {
					$button.text("No more previous versions");
					exhausted = true;
				}
				$historyList.attr("data-qid", json.last);
				
				json.showAddButton = false;
				return "shared.question.multiple";
			}, 
			function() {
				$historyList.append($newQuestions.find(".panels").children());
				if ( ! exhausted) {
					$button.removeClass("disabled");
				}
			});
	 
}

function loadMoreForks(amount, $button){
	if($button.hasClass("disabled")) {
		return false;
	}
	$button.addClass("disabled");
		
	var $forksList = $(".main").find("#forks-list");
	var $newQuestions = $(document.createElement("div"));
	var exhausted = false;
	
	loadModule($newQuestions,
			"q/forks?qid=" + $forksList.attr("data-qid") + "&disp=" + $forksList.attr("data-disp") + "&amount=" + amount,
			function(json) {
				if(json.exhausted) {
					$button.text("No more forks");
					exhausted = true;
				}
				$forksList.attr("data-disp", json.disp);
				
				json.showAddButton = false;
				return "shared.question.multiple";
			},
			function() {
				$forksList.append($newQuestions.find(".panels").children());
				if ( ! exhausted) {
					$button.removeClass("disabled");
				}
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
	var $newSets = $(document.createElement("div"));
	var $setsList = $("#sets-list");
	var $exportTabContent = $("#question-tab-export");
	
	$exportTabContent.children(".loading").show();
	$exportTabContent.children(".row:not(.loading)").hide();

	loadModule($newSets,
			"sets/mysets/limited?page=" + page + "&amount=" + amount + "&contains=" + $setsList.attr("data-qid"),
			"shared.set.multipleHighlight",
			function() {
				$setsList.empty();
				$setsList.append($newSets.find(".panels").children());
				$exportTabContent.children(".row:not(.loading)").show();
				$exportTabContent.children(".loading").hide();
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
	applyTemplate($("#question-tab-overview"), "questions.view.question.tab.overview.full", {question: json.question});
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
	var $newList = $(document.createElement("div"));
	var $loading = $("#question-tab-edit .loading");
	
	$loading.slideDown();
	$setListDiv.empty();

	loadModule($newList,
			"sets/mysets/qlimited?qid=" + $("#form-qid").val(),
			"shared.set.setSelectionQuestionEdit",
			function(){
				$newList.find(".panels").hide();
				$newList.find(".panels").addClass("set-list-to-edit");
				$setListDiv.empty();
				$setListDiv.append($newList.children());
				$setListDiv.find(".panels").slideDown();
				$loading.slideUp();
			});
}