/**
 * Configure search types
 */
kenyaui.configureSearch('location', {
	searchProvider: 'kenyaemr',
	searchFragment: 'search',
	format: function(object) { return object.name + ' <span style="color: #999">' + object.code + '</span>'; }
});

/**
 * Page initialization tasks
 */
jq(function() {
	/**
	 * Clicking on an encounter-item should display the encounter as a form in a dialog
	 */
	jq('.encounter-item').click(function(event) {
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
var kenyaemr = (function(jq) {

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

			var ret = '<div class="ke-stack-item ke-navigable">';
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

			var ret = '<div class="ke-stack-item ke-navigable">';
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

		/**
		 * Updates the value of a regimen field from its displayed controls
		 * @param fieldId the regimen field id
		 */
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
		},

		/**
		 * Creates a dynamic obs field
		 * @param parentId the container element id
		 * @param fieldName the field name
		 * @param conceptId the concept id
		 * @param initialValue the initial field value (may be null)
		 * @param readOnly true if control should be read only
		 */
		dynamicObsField: function(parentId, fieldName, conceptId, initialValue, readOnly) {
			var placeHolderId = kenyaui.generateId();
			jq('#' + parentId).append('<div id="' + placeHolderId + '" class="ke-loading ke-form-dynamic-field">&nbsp;</div>');
			jq.get('/' + CONTEXT_PATH + '/kenyaemr/generateField.htm', { name: fieldName, conceptId: conceptId, initialValue: initialValue, readOnly : readOnly })
			.done(function (html) {
				jq('#' + placeHolderId).removeClass('ke-loading');
				jq('#' + placeHolderId).html(html);
			});
		}
	};

})(jQuery);