<%
	config.require("definition")
	config.require("onRequestCallback")

	// Organise parameters by name (for Groovy 1.7.9+ this could be replaced with .collectEntries)
	def params = [:]
	config.definition.parameters.each{ param -> params[param.name] = param }

	def usePeriodField = params.containsKey("startDate") && params.containsKey("endDate")

	if (usePeriodField) {
		params.remove("startDate")
		params.remove("endDate")
	}
%>
<script type="text/javascript">
	jQuery(function() {
		jQuery('#${ config.id }_btn').click(function() {
			kenyaui.closeDialog();

			var params = {};
			jQuery('#${ config.id }').find('[name^="param"]').each(function() {
				var field = jQuery(this);
				params[field.attr('name')] = field.val();
			});

			${ config.onRequestCallback }(params);
		});
	});
</script>

<div class="ke-panel-content">
	<form id="${ config.id }">
		<div class="ke-field-label">Report</div>
		<div class="ke-field-content">${ config.definition.name }</div>
		<% if (usePeriodField) { %>
		<div class="ke-field-label">Month</div>
		<div class="ke-field-content">

			${ ui.includeFragment("kenyaemr", "field/reportPeriod", [ pastMonths: 7 ]) }
		</div>
		<% } %>
		<% params.each { name, param -> %>
		<div class="ke-field-label">${ param.label }</div>
		<div class="ke-field-content">
			${ ui.includeFragment("kenyaemr", "field/reportParameter", [ parameter: param ]) }
		</div>
		<% } %>
	</form>
</div>
<div class="ke-panel-footer">
	<button type="button" id="${ config.id }_btn"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/start.png") }" /> Request</button>
	<button type="button" onclick="kenyaui.closeDialog();"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/cancel.png") }" /> Cancel</button>
</div>