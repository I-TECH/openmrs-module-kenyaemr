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
		twoColumnPanelFormatter: function(data, values) {
			var ret = '<div class="panel">';
			ret += '<table width="100%"><tr valign="top"><td width="50%">';
			ret += '<span class="icon">' + formatHelper(data, values.icon) + '</span>';
			ret += '<span class="leftText">';
			ret += '<span class="title">' + formatHelper(data, values.title) + '</span>';
			ret += '<span class="leftDetails">' + formatHelper(data, values.leftDetails) + '</span>';
			ret += '</span>';
			ret += '</td><td align="right" width="50%">';
			ret += formatHelper(data, values.right);
			ret += '</td></tr></table>';
			ret += '</div>';
			return ret;
		},
		
		/*
		 * values may specify (as function(data) or static text): icon, title, leftDetails, center, right
		 */
		threeColumnPanelFormatter: function(data, values) {
			var ret = '<div class="panel">';
			ret += '<table width="100%"><tr valign="top"><td width="40%">';
			ret += '<span class="icon">' + formatHelper(data, values.icon) + '</span>';
			ret += '<span class="leftText">';
			ret += '<span class="title">' + formatHelper(data, values.title) + '</span>';
			ret += '<span class="leftDetails">' + formatHelper(data, values.leftDetails) + '</span>';
			ret += '</span>';
			ret += '</td><td align="center width="30%">';
			ret += formatHelper(data, values.center);
			ret += '</td><td align="right" width="30%">';
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