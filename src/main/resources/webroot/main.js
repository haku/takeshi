var divCanvas;
var nextNodeId = 0;

function initCastleList(div) {
	var menu = $('<div class="menu">');
	div.append(menu);
	$.getJSON('/data', function(data) {
		var castles = [];
		$.each(data, function(key, val) {
			var link = $('<a href="">');
			link.text('Castle ' + val.id + ': ' + val.name);
			link.click(function(event) {
				event.preventDefault();
				initTakeshi(div, val.id);
			});
			menu.append(link);
		});
	});
}

function initTakeshi(div, id) {
	divCanvas = div;
	divCanvas.empty();
	$('#toolbar .add').click(_addNodeClickHandler);
	divCanvas.dblclick(_canvasDblClickHandler);
	$('#toolbar .save').click(_saveClickHandler);
	if (id) {
		$.getJSON('/data?id=' + id, function(data) {
			console.log('l', data.nodes);
			$.each(data.nodes, function (index, node) {
				console.log('n', node);
				_addNode(node.pos.left, node.pos.top, node.label);
			});
		});
	}
}

function _addNodeClickHandler(event) {
	event.preventDefault();
	_addNode();
}

function _canvasDblClickHandler(event) {
	event.preventDefault();
	_addNode(event.pageX, event.pageY);
}

function _addNode(x, y, label) {
	var id = nextNodeId++;
	var text = $('<p class="text">');
	text.text(label ? label : id);
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
	var nodes = [];
	$('.node', divCanvas).each(function() {
		var node = $(this);
		var id = node.attr('id');
		var pos = node.position();
		var label = $('.text', node).text();
		nodes.push({
			id : id,
			pos : pos,
			label : label
		});
	});
	var castle = {
		nodes : nodes
	};
	$.ajax({
		type : 'POST',
		url : '/data',
		data : {
			json : JSON.stringify(castle)
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