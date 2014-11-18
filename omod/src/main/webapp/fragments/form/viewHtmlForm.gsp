<script type="text/javascript" src="/${ contextPath }/moduleResources/htmlformentry/htmlFormEntry.js"></script>
<link href="/${ contextPath }/moduleResources/htmlformentry/htmlFormEntry.css" type="text/css" rel="stylesheet" />

<script type="text/javascript">
	\$j = jQuery;

	// These might be used by in-form scripts
	var ke_formMode = 'VIEW';
	var propertyAccessorInfo = new Array();
	var beforeSubmit = new Array();

	/**
	 * Overrides standard HFE method to work in view mode
	 */
	function getField(elementAndProperty) {
	 	return jq(); // Don't return anything (no fields in view mode)
	}

	/**
	 * Overrides standard HFE method to work in view mode
	 */
	function getValue(elementAndProperty) {
		var fieldId = elementAndProperty.split(".")[0];
		var fieldValue = jq('#' + fieldId + ' span.value').map(function() {
			return jq(this).text();
		}).get().join(' '); // Value from span.value texts

		if (fieldId == 'encounter-date') {
			return jq.datepicker.formatDate(jq.datepicker.W3C, new Date(fieldValue)); // Re-format date
		}

		return fieldValue;
	}

	/**
	 * Overrides standard HFE method to work in view mode
	 */
	function setValue(elementAndProperty, value) {
		var fieldId = elementAndProperty.split(".")[0];
		jq('#' + fieldId + ' span.value').text(value);
	}
</script>

<div>
	${ formHtml }

	<fieldset>
		<legend>Change history</legend>

		<% changeHistory.each { change -> %>
		${ kenyaui.formatDateTime(change.timestamp) } by ${ kenyaui.formatUser(change.user) } : ${ change.description }<br />
		<% } %>
	</fieldset>
</div>