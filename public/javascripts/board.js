$(function() {

	$(".story-list").sortable({
        revert: true,
        handle: ".story-header",
        placeholder: "story ui-state-highlight",
        forcePlaceholderSize: true,
        tolerance: 'pointer',
        scroll: true,
        connectWith: ".story-list",
        containment: $( "#swim-lanes" ).length ? "#swim-lanes" : "document",
        update: function(event, ui) {
        	var state = $(this).attr('id').split('-')[1];
        	var story = ui.item.attr('id').split('-')[1];
        	if (ui.sender != null) {
        		// Move a story from one lane to another
        		alert("Moving story " + story + " to state " + state + " (" + ui.item.index() + ")");
        	} else if ($(this).find("#" + ui.item.attr('id')).length) {
        		// Change the position of a story in this lane
        		alert("Updating story " + story + " in state " + state + " (" + ui.item.index() + ")");
        		$.getJSON("/ajax/moveStory?storyId=" + story + "&index=" + ui.item.index(), function(data) {
        			if (!data.success) {
        				alert("response: " + data.messages);
        			}
        		});
            }
        },
    });
    $(".story-list").disableSelection();
});
