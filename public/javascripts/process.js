$(function() {
	// Enable drag, drop and sort on the list of states
	$("#state-list").sortable({
        revert: true,
        placeholder: "state-item ui-state-highlight",
        handle: ".state-info",
        scroll: true,
        cancel: ".fixed", // do not include the 'fixed' states
        update: function(event, ui) {
        	var state = ui.item.attr('id').split('-')[1];
       		var url = routes.moveState({state: state, index: ui.item.index()});
       		log(2, "Calling Ajax: " + url);
       		doAjax(url);
        }
    });
    $("#state-list").disableSelection();
    
    // Create the delete buttons on the states
	$(".state-delete").button({
        icons: { primary: "ui-icon-circle-close" }
	}).click(function() {
		var state = $(this).parents(".state-item").attr('id');
		state = $(this).parents(".state-item").attr('id').split('-')[1];
   		var url = routes.deleteState({state: state});
   		log(2, "Calling Ajax: " + url);
   		doAjax(url, function() {
   			location.reload(true)
   		});
	});
	// 'fixed' states cannot be deleted
	$(".fixed .state-delete").button("option", "disabled", true);
	
	// Create the edit buttons on the states
	$(".state-edit").button({
        icons: { primary: "ui-icon-pencil" }
	});
	
	// hide the buttons, unless the mouse hovers over the note
	$(".state-footer button").hide();
	$(".state-item").hover(
		function() {
			$(this).find("button").show();
		},
		function() {
			$(this).find("button").hide();
		}
	);
	
	// Create a popup from, and tie it to the button
	$("#state-create-form").dialog({
		autoOpen: false,
		height: 300,
		width: 350,
		modal: true,
		buttons: {
			"Add state": function() {
	        	var project = $(this).attr('project-id');
	        	var name = $("#name").val();
	        	var description = $("#description").val();
	        	var limit = $("#limit").val();
	       		var url = routes.addState({
	       			project: project, 
	       			name: name, 
	       			description: description,
	       			limit: limit
	       		});
	       		log(2, "Calling Ajax: " + url);
				$(this).dialog("close");
	       		doAjax(url, function() { 
	       			location.reload(true);
	       		});
	       		
			},
			Cancel: function() {
				$(this).dialog("close");
			}
		},
	});
	$("#state-create-button").button()
		.click(function() {
			$( "#state-create-form" ).dialog( "open" );
		});
});
