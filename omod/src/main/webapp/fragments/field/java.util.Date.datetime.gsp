<%
	ui.includeJavascript("kenyaui", "coreFragments.js")

	def initialHour = config.initialValue ? config.initialValue.hours : null
	def initialMinute = config.initialValue ? config.initialValue.minutes : null
%>

<script>
	jq(function() {
		jq('#${ config.id }_date').datepicker({
			dateFormat: 'dd-M-yy',
			changeMonth: true,
			changeYear: true,
			showButtonPanel: true,
			yearRange: '-110:+5',
			autoSize: true
			<% if (config.required) { %>
			, onClose: function(dateText, inst) { clearErrors('${ config.id }-error'); validateRequired(dateText, '${ config.id }-error'); }
			<% } %>
			<% if (config.maxDate) { %>
			, maxDate: '${ config.maxDate }'
			<% } %>
			<% if (config.minDate) { %>
			, minDate: '${ config.minDate }'
			<% } %>
		});

		jq('#${ config.id }_date, #${ config.id }_hour, #${ config.id }_minute').change(function() {
			kenyaemr.updateDateTimeFromDisplay('${ config.id }');
		});
	});
</script>

<input id="${ config.id }" type="hidden" name="${ config.formFieldName }" <% if (config.initialValue) { %>value="${ ui.dateToString(config.initialValue) }"<% } %>/>
<input id="${ config.id }_date" type="text" <% if (config.initialValue) { %>value="${ kenyaUi.formatDate(config.initialValue) }"<% } %>/>
<select id="${ config.id }_hour"><% for (def h in 0..23) { %><option ${ initialHour == h ? "selected" : "" }>${ String.format('%02d', h) }</option><% } %></select>:<select id="${ config.id }_minute"><% for (def m in 0..59) { %><option ${ initialMinute == m ? "selected" : "" }>${ String.format('%02d', m) }</option><% } %></select>
<span id="${ config.id }-error" class="error" style="display: none"></span>

<% if (config.parentFormId) { %>
<script type="text/javascript">
	jq(function() {
		// Save default input values
		jq('#${ config.id }').data('default-value', jq('#${ config.id }').val());
		jq('#${ config.id }_date').data('default-value', jq('#${ config.id }_date').val());
		jq('#${ config.id }_hour').data('default-value', jq('#${ config.id }_hour').val());
		jq('#${ config.id }_minute').data('default-value', jq('#${ config.id }_minute').val());

		subscribe('${ config.parentFormId }.reset', function() {
			jq('#${ config.id }').val(jq('#${ config.id }').data('default-value'));
			jq('#${ config.id }_date').val(jq('#${ config.id }_date').data('default-value'));
			jq('#${ config.id }_hour').val(jq('#${ config.id }_hour').data('default-value'));
			jq('#${ config.id }_minute').val(jq('#${ config.id }_minute').data('default-value'));
			jq('#${ config.id }-error').html("").hide();
		});

		subscribe('${ config.parentFormId }.clear-errors', function() {
			jq('#${ config.id }-error').html("").hide();
		});

		subscribe('${ config.parentFormId }/${ config.formFieldName }.show-errors', function(message, payload) {
			FieldUtils.showErrorList('${ config.id }-error', payload);
		});

		jq('#${ config.id }_date, #${ config.id }_hour, #${ config.id }_minute').change(function() {
			publish('${ config.parentFormId }/changed');
		});
	});
</script>
<% } %>