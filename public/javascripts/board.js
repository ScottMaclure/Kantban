// Fix the width of the dynamic lanes
function resizeLanes() {
	
	// Get some information about the lanes
    var nLanes=$(".lane").size();
	log(2, "Have " + (nLanes - 2) + " to do in " + window.innerWidth + " pixels");
    var fixedWidth = 0;
    var nFixedLanes = 0;
    $(".lane.fixed").each(function(index){
    	var width = parseInt($(this).css("width"));
    	if (!width) {
    		width = $(this).outerWidth(true);   	
    	}
    	fixedWidth += width;
    	nFixedLanes++;
    });
    log(2, "Have " + fixedWidth + " fixed pixels in " + nFixedLanes + " lanes");
    
    // Now change the lane widths
    var dynamicWidth = Math.floor((window.innerWidth - fixedWidth)/(nLanes - nFixedLanes));
    $(".lane.dynamic").each(function(index) {
    	var p = $(this).padding();
    	var b = $(this).border();
    	var m = $(this).margin();
    	$(this).css("width", dynamicWidth - p.left - p.right - b.left - b.right - m.left - m.right);
    });

    // And the height of the lane table
	var height = window.innerHeight - $("#swim-lanes").offset().top;
    $("#swim-lanes").each(function(index) {
    	var p = $(this).padding();
    	var b = $(this).border();
    	var m = $(this).margin();
    	$(this).css("height", height - p.top - p.bottom - b.top - b.bottom - m.top - m.bottom);
    });
}

$(function() {
	
	// Deal with the 'hidden' fixed lanes
	// TODO fix the dependency on size. Maybe use toggleClass instead?
    $(".open").hide();
    $(".closed .arrow").click(function() {
    	var lane = $(this).parents(".lane");
    	lane.css("width", 250);
    	lane.find(".closed").hide();
    	lane.find(".open").show();
    	resizeLanes();
    });
    $(".open .arrow").click(function() {
    	var lane = $(this).parents(".lane");
    	lane.css("width", 10);
    	lane.find(".open").hide();
    	lane.find(".closed").show();
    	resizeLanes();
    });

    // Make lists sortable/droppable
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
    
    // make sure we recalculate on load and when the window size changes
    resizeLanes();
    $(window).resize(function() { 
    	resizeLanes(); 
    });
});
