var divCanvas;
var nextNodeId = 0;

function initTakeshi(div) {
	divCanvas = div;
	$('#toolbar .add').click(_addNodeClickHandler);
}

function _addNodeClickHandler(event) {
	event.preventDefault();
	_addNode();
}

function _addNode() {
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
	node.position({
		my : 'center',
		at : 'center',
		of : divCanvas
	});
}

function _nodeClickHandler(event) {
	event.preventDefault();
	var p = $('.text', this);
	var text = prompt("Label", p.text());
	if (text) p.text(text)
}
