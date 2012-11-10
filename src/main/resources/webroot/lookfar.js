var LOOKFAR_REFRESH_MILLIS = 300000; // 5 min.
var divCanvas;

function initLookfar(div) {
	divCanvas = div;
	$('#toolbar .lookfar').click(_lookfarClickHandler);
	setInterval(function() {
		_refreshLookfarData();
	}, LOOKFAR_REFRESH_MILLIS);
}

function _lookfarClickHandler(event) {
	event.preventDefault();
	_refreshLookfarData();
}

function _refreshLookfarData() {
	$.getJSON('/status', function(data) {
		_applyData(data);
	});
}

function _applyData(data) {
	var stati = _summariseNodes(data);
	$('.node', divCanvas).each(function(index, node) {
		var nodeName = $('.text', node).text();
		var status = stati[nodeName];
		if (status) {
			var statE = $('.status', node);
			if (statE.size() < 1) {
				var statE = $('<p class="status">');
				$(node).append(statE);
			}
			statE.text(status);
		}
	});
}

function _summariseNodes(data) {
	var nodes = {};
	$.each(data, function(index, datum) {
		var oldFlag = nodes[datum.node];
		if (!oldFlag || _getFlagValue(oldFlag) < _getFlagValue(datum.flag)) {
			nodes[datum.node] = datum.flag;
		}
	});
	return nodes;
}

function _getFlagValue(flag) {
	var v = FLAG_VALUE[flag]
	return v ? v : 100;
}

var FLAG_VALUE = {
	'OK' : 0,
	'EXPIRED' : 1,
	'WARNING' : 2,
	'INVALID' : 3
}