var divCanvas;
var nextNodeId = 0;

function initTakeshi(div) {
	divCanvas = div;
	$('#toolbar .add').click(_addNodeClickHandler);
	divCanvas.dblclick(_canvasDblClickHandler);
	$('#toolbar .save').click(_saveClickHandler);
}

function _addNodeClickHandler(event) {
	event.preventDefault();
	_addNode();
}

function _canvasDblClickHandler(event) {
	event.preventDefault();
	_addNode(event.pageX, event.pageY);
}

function _addNode(x, y) {
	var id = nextNodeId++;
	var text = $('<p class="text">');
	text.text(id);
	var node = $('<div class="node">');
	node.attr('id', 'node' + id);
	node.append(text);
	node.draggable({
		containment : "parent",
		grid : [20, 20],
		stack : ".node"
	});
	node.click(_nodeClickHandler);
	divCanvas.append(node);
	if (x && y) {
		node.offset({
			left : x,
			top : y
		});
	}
	else {
		node.position({
			my : 'center',
			at : 'center',
			of : divCanvas
		});
	}
}

function _nodeClickHandler(event) {
	event.preventDefault();
	var p = $('.text', this);
	var text = prompt("Label", p.text());
	if (text) {
		p.text(text)
	}
}

function _saveClickHandler(event) {
	event.preventDefault();
	var jsonObj = [];
	$('.node', divCanvas).each(function() {
		var node = $(this);
		var id = node.attr('id');
		var pos = node.position();
		var label = $('.text', node).text();
		jsonObj.push({
			id : id,
			pos : pos,
			label : label
		});
	});
	$.ajax({
		type : 'POST',
		url : '/data',
		data : {
			json : JSON.stringify(jsonObj)
		},
		beforeSend : function() {
			console.log('Saving...');
		},
		success : function(response) {
			console.log('Save successful.');
		},
		error : function(xhr) {
			console.log('Save failed.', xhr);
		},
		complete : function(jqXHR, textStatus) {
			console.log('Save complete.', jqXHR, textStatus);
			alert('Save result: ' + textStatus);
		}
	});
}

/* http://api.jquery.com/category/events/event-object/
 * http://jqueryui.com/demos/draggable/
 */