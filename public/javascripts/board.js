$(function() {
	$(".story-list").droppable({
        accept: ".story",
        // hoverClass: "ui-state-active",
        drop: function(event, ui) {
        	var state = $(this).attr('id');
        	var story = ui.draggable.attr('id');
//            alert("Dropped story " + story + " on " + state);
        }
    })

    $(".story-list").sortable({
        revert: true,
        handle: ".story-header",
        placeholder: "story ui-state-highlight",
        forcePlaceholderSize: true,
        tolerance: 'pointer',
        scroll: true,
        connectWith: ".story-list",
        containment: $( "#swim-lanes" ).length ? "#swim-lanes" : "document",
    });
    $(".story-list").disableSelection();
});
