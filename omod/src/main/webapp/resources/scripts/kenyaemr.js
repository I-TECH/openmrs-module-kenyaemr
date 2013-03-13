/**
 * Page initialization tasks
 */
$(function() {
	/**
	 * Clicking on an encounter-item should display the encounter as a form in a dialog
	 */
	$('.encounter-item').click(function(event) {
		var encId = $(this).find('input[name=encounterId]').val();
		var title = $(this).find('input[name=title]').val();
		publish('showHtmlForm/showEncounter', { encounterId: encId, editButtonLabel: 'Edit', deleteButtonLabel: 'Delete' });
		showDivAsDialog('#showHtmlForm', title);
		return false;
	});
});

/**
 * Utility methods
 */
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

			var ret = '<div class="ke-stack-item ke-clickable">';
			if (clickUrl) {
				ret += '<input type="hidden" name="clickUrl" value="' + clickUrl + '" />'
			}
			ret += '<table width="100%"><tr valign="top"><td width="50%">';
			ret += '  <span class="ke-icon">' + formatHelper(data, values.icon) + '</span>';
			ret += '  <b>' + formatHelper(data, values.title) + '</b><br />' + formatHelper(data, values.leftDetails);
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

			var ret = '<div class="ke-stack-item ke-clickable">';
			if (clickUrl) {
				ret += '<input type="hidden" name="clickUrl" value="' + clickUrl + '" />'
			}
			ret += '<table width="100%"><tr valign="top"><td width="40%">';
			ret += '  <span class="ke-icon">' + formatHelper(data, values.icon) + '</span>';
			ret += '  <b>' + formatHelper(data, values.title) + '</b><br />' + formatHelper(data, values.leftDetails);
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
		},

		updateRegimenFromDisplay: function(fieldId) {
			var regimenStr = '';

			$('#' + fieldId +  '-container .regimen-component').each(function() {
				var drug = jq(this).find('.regimen-component-drug').val();
				var dose = jq(this).find('.regimen-component-dose').val();
				var units = jq(this).find('.regimen-component-units').val();
				var frequency = jq(this).find('.regimen-component-frequency').val();

				if (drug || dose) {
					regimenStr += (drug + '|' + dose + '|' + units + '|' + frequency + '|');
				}
			});

			$('#' + fieldId).val(regimenStr);
		}
	};

})(jQuery);