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
		twoColumnStackItemFormatter: function(data, values) {
			var clickUrl = formatHelper(data, values.clickUrl);

			var ret = '<div class="stack-item clickable">';
			if (clickUrl) {
				ret += '<input type="hidden" name="clickUrl" value="' + clickUrl + '" />'
			}
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
		threeColumnStackItemFormatter: function(data, values) {
			var clickUrl = formatHelper(data, values.clickUrl);

			var ret = '<div class="stack-item clickable">';
			if (clickUrl) {
				ret += '<input type="hidden" name="clickUrl" value="' + clickUrl + '" />'
			}
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

$(function() {
	/**
	 * Clicking anywhere on a panel-menuitem should direct you to the target of it's <a> tag
	 */
	$('.panel-menuitem').click(function() {
		var a = $(this).find('a').first();
		var href = (a.length > 0) ? a.attr('href') : null;
		if (href)
			location.href = href;
	});

	/**
	 * Clicking on a stack-item should direct you to the URL specified in the clickUrl hidden input
	 */
	jq('.stack-item').click(function(evt) {
		var clickUrl = jq(this).find('input[name=clickUrl]').first();
		var url = (clickUrl.length > 0) ? clickUrl.val() : null;
		if (url) {
			location.href = url;
		}
	});
});