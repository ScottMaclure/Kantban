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
        	var story = ui.item.attr('id').split('-')[1];
        	var state = $(this).attr('id').split('-')[1];
        	if (ui.sender != null) {
        		// Move a story from one lane to another
        		var url = routes.moveStoryToState({story: story, state: state, index: ui.item.index()});
        		doAjax(url);
        	} else if ($(this).find("#" + ui.item.attr('id')).length) {
        		// Change the position of a story in this lane
        		var url = routes.moveStory({story: story, index: ui.item.index()});
        		doAjax(url);
            }
        },
    });
    $(".story-list").disableSelection();
});
