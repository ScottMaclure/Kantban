$(function() {
	$("#state-list").sortable({
        revert: true,
        placeholder: "state-item ui-state-highlight",
        //forcePlaceholderSize: true,
        //tolerance: 'pointer',
        handle: ".state-info",
        //scroll: true,
        //containment: $( "#state-list" ).length ? "#state-list" : "document",
        cancel: ".fixed",
        update: function(event, ui) {
        	var state = ui.item.attr('id').split('-')[1];
       		var url = routes.moveState({state: state, index: ui.item.index()});
       		log(2, "Calling Ajax: " + url);
       		doAjax(url);
        }
    });
    $("#state-list").disableSelection();
    
	$(".state-delete").button({
        icons: { primary: "ui-icon-circle-close" }
	}).click(function() {
		var state = $(this).parents(".state-item").attr('id');
		alert(state);
		state = $(this).parents(".state-item").attr('id').split('-')[1];
   		var url = routes.deleteState({state: state});
   		log(2, "Calling Ajax: " + url);
   		doAjax(url, function() {
   			// location.reload()
   		});
	});
	
	$(".state-edit").button({
        icons: { primary: "ui-icon-pencil" }
	});
	$(".fixed .state-delete").button("option", "disabled", true);
	
	$(".state-footer button").hide();
	$(".state-item").hover(
		function() {
			$(this).find("button").show();
		},
		function() {
			$(this).find("button").hide();
		}
	);
	
	
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
	       		doAjax(url, function() { 
	       			//location.reload 
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
