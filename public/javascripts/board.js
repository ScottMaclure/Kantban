// Fix the width of the dynamic lanes
function resizeLanes() {
    var nLanes=$(".lane").size();
	log(1, "Have " + (nLanes - 2) + " to do in " + window.innerWidth + " pixels");
    var fixedWidth = 0;
    var nFixedLanes = 0;
    $(".lane.fixed").each(function(index){
    	fixedWidth += $(this).outerWidth(true);
    	nFixedLanes++;
    });
    log(1, "Have " + fixedWidth + " fixed pixels in " + nFixedLanes + " lanes");
    var dynamicWidth = Math.floor((window.innerWidth - fixedWidth)/(nLanes - nFixedLanes));
    $(".lane.dynamic").each(function(index) {
    	// Fixme what about padding? This width is the outer width for the td
    	var p = $(this).padding();
    	var b = $(this).border();
    	var m = $(this).margin();
    	$(this).attr("width", dynamicWidth - p.left - p.right - b.left - b.right - m.left - m.right);
    });

	var height = window.innerHeight - $("#swim-lanes").offset().top;
    $("#swim-lanes").each(function(index) {
    	var p = $(this).padding();
    	var b = $(this).border();
    	var m = $(this).margin();
    	$(this).attr("height", height - p.top - p.bottom - b.top - b.bottom - m.top - m.bottom);
    });
}
$(window).resize(function() {
	resizeLanes();
});
$(function() {
	$(".story-list").sortable({
        revert: true,
        handle: ".story-header",
        placeholder: "story ui-state-highlight",
        forcePlaceholderSize: true,
        tolerance: 'pointer',
        scroll: true,
        connectWith: ".story-list",
        containment: $("#swim-lanes").length ? "#swim-lanes" : "document",
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
    
    resizeLanes();
});
