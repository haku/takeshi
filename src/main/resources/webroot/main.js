var divCanvas;
var pStatus;
var nextNodeId = 0;
var castleId;

function _reset() {
	nextNodeId = 0;
	delete castleId;
}

function initCastleList(canvas, status) {
	divCanvas = canvas;
	pStatus = status;
	_loadMenu();
}

function _loadMenu() {
	divCanvas.empty();
	var menu = $('<div class="menu">');
	divCanvas.append(menu);

	var newLink = $('<a href="">');
	newLink.text('New Castle');
	newLink.click(_newCastleClickHandler);
	menu.append($('<p>').append(newLink));

	$.getJSON('/data', function(data) {
		var castles = [];
		$.each(data, function(key, val) {
			var link = $('<a href="">');
			link.text('Castle ' + val.name + ' (' + val.id + ')');
			link.click(function(event) {
				event.preventDefault();
				_loadCastle(val.id);
			});
			menu.append($('<p>').append(link));
		});
	});
	pStatus.text('Castle list refreshed.');
}

function _loadCastle(id) {
	_reset();
	divCanvas.empty();
	$('#toolbar .add').click(_addNodeClickHandler);
	divCanvas.dblclick(_canvasDblClickHandler);
	$('#toolbar .save').click(_saveClickHandler);
	$('#toolbar .name').click(_nameClickHandler);
	if (id) {
		castleId = id;
		console.log('castleId', castleId);
		$.getJSON('/data?id=' + id, function(data) {
			$('#toolbar .name').val(data.name);
			if (data.nodes) {
				$.each(data.nodes, function(index, node) {
					_addNode(node.pos.left, node.pos.top, node.label);
				});
			}
		});
		pStatus.text('Loaded castle ' + castleId + '.');
	}
}

function _newCastleClickHandler(event) {
	event.preventDefault();
	var name = prompt('Name', 'New Castle');
	if (name) {
		$.post('/data', {
			'new' : name
		}, function(data) {
			_loadMenu();
		});
	}
}

function _nameClickHandler(event) {
	event.preventDefault();
	var name = prompt("Name", $(this).val());
	if (name) {
		$(this).val(name);
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
	text.text( label ? label : id);
	var node = $('<div class="node">');
	node.attr('id', 'node' + id);
	node.append(text);
	node.draggable({
		containment : "parent",
		//grid : [20, 20],
		stack : ".node"
	});
	node.click(_nodeClickHandler);
	divCanvas.append(node);
	if (isNumber(x) && isNumber(y)) {
		node.position({
			my : 'left top',
			at : 'left top',
			of : divCanvas,
			collision : 'none',
			offset : x + ' ' + y
		});
		//console.log('drift', x, '=', node.position().left, y, '=', node.position().top);
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
	if (text != null) {
		if (text == "") {
			$(this).remove();
		}
		else {
			p.text(text)
		}
	}
}

function _saveClickHandler(event) {
	event.preventDefault();
	var castleName = $('#toolbar .name').val();
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
		_id : castleId,
		name : castleName,
		nodes : nodes
	};
	$.ajax({
		type : 'POST',
		url : '/data',
		data : {
			json : JSON.stringify(castle)
		},
		beforeSend : function() {
			pStatus.text('Saving...');
		},
		success : function(response) {
			console.log('Save successful.');
		},
		error : function(xhr) {
			console.log('Save failed.', xhr);
		},
		complete : function(jqXHR, textStatus) {
			console.log('Save complete.', jqXHR, textStatus);
			pStatus.text('Save result: ' + textStatus);
		}
	});
}

function isNumber(n) {
	return !isNaN(parseFloat(n)) && isFinite(n);
}

/* http://api.jquery.com/category/events/event-object/
 * http://jqueryui.com/demos/draggable/
 */