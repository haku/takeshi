var LOOKFAR_HOST = 'https://lookfar.herokuapp.com';

var divCanvas;

function initLookfar(div) {
	divCanvas = div;
	$('#toolbar .lookfar').click(_lookfarClickHandler);
}

function _lookfarClickHandler(event) {
	event.preventDefault();
	$.ajax({
		url : LOOKFAR_HOST + '/update',
		cache : false,
		xhrFields : {
			withCredentials : true
		},
		dataType : 'json',
		success : function(data, textStatus, jqXHR) {
			//console.log('success', data, textStatus, jqXHR);
			_applyData(data);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			console.log('error', jqXHR, textStatus, errorThrown);
		}
	});
}

function _applyData(data) {
	var stati = _summariseNodes(data);
	//console.log('stati', stati);
	$('.node', divCanvas).each(function(index, node) {
		var nodeName = $('.text', node).text();
		var status = stati[nodeName];
		if (status) {
			var statE = $('<p class="status">');
			statE.text(status);
			$(node).append(statE);
		}
	});
}

function _summariseNodes(data) {
	var nodes = {};
	$.each(data, function(index, datum) {
		//console.log('datum', datum);
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