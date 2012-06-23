var divCanvas;

function initTakeshi(div) {
	divCanvas = div;
	$('#toolbar .add').click(_addNodeClickHandler);
}

function _addNodeClickHandler(event) {
	event.preventDefault();
	_addNode();
}

function _addNode() {
	var node = $('<div class="node">');
	node.offset({
		top : 10,
		left : 30
	});
	node.draggable();
	divCanvas.append(node);
}