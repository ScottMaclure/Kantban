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
       		//doAjax(url);
        }
    });
    $("#state-list").disableSelection();
    
	$(".state-delete").button({
        icons: { primary: "ui-icon-circle-close" }
	});
	$(".state-edit").button({
        icons: { primary: "ui-icon-pencil" }
	});
	
	$(".state-footer button").hide();
	$(".state-item").hover(
		function() {
			$(this).find("button").fadeIn();
		},
		function() {
			$(this).find("button").fadeOut();
		}
	);
});
