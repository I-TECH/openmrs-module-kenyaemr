var kenyaemr = (function($) {

	var formatHelper = function(data, formatter) {
		if (data === null || typeof formatter === 'undefined') {
			return "";
		} else if (typeof formatter === 'function') {
			return formatter(data);
		} else {
			return formatter;
		}
	};
	
	return {

		/*
		 * values may specify (as function(data) or static text): icon, title, leftDetails, center, right
		 */
		panelFormatter: function(data, values) {
			var ret = '<div class="panel">';
			ret += '<table width="100%"><tr valign="top"><td>';
			ret += '<span class="icon">' + formatHelper(data, values.icon) + '</span>';
			ret += '<span class="title">' + formatHelper(data, values.title) + '</span>';
			ret += '<span class="leftDetails">' + formatHelper(data, values.leftDetails) + '</span>';
			ret += '</td><td align="center">';
			ret += formatHelper(data, values.center);
			ret += '</td><td align="right">';
			ret += formatHelper(data, values.right);
			ret += '</td></tr></table>';
			ret += '</div>';
			return ret;
		},
		
		/*
		 * returns "# result(s)"
		 */
		defaultNumResultsFormatter: function(listOfItems) {
			return typeof listOfItems.length === 'number' ? (listOfItems.length + ' result(s)') : '';
		}
		
	};

})(jQuery);