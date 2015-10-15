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
			var form = jQuery('#${ config.id }');
			var errors = form.find('.error').filter(':visible');

			if (errors.length > 0) {
				return;
			}

			kenyaui.closeDialog();

			var params = {};
			form.find('[name^="param"]').each(function() {
				var field = jQuery(this);
				params[field.attr('name')] = field.val();
			});
			var periodValue = parseInt(jQuery('#report-period').val());
			if(periodValue == null) {
				periodValue = 0;
			}
			var datePicked = jQuery('#date_value').val();
			if(datePicked == null){
				${ config.onRequestCallback }(params);
			}
			var pickedDate = datePicked.split("-");
			var date = new Date();
			date.setDate(pickedDate[2]);
			date.setMonth(pickedDate[1]-1);
			date.setYear(pickedDate[0]);
			var results = date.setMonth(date.getMonth() + periodValue);
			var today = new Date().getTime();
			if(periodValue > 6 && results > today && periodValue <= 60) {
				kenyaui.openAlertDialog({
					heading: 'Run report',
					message: 'Insufficient follow-up time to run this report '
				});
			}
			else {
				${ config.onRequestCallback }(params);
			}
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

			${ ui.includeFragment("kenyaemr", "field/reportPeriod", [ pastMonths: 65 ]) }
		</div>
		<% } %>
		<% params.each { name, param -> %>
		<div class="ke-field-label">${ param.label }</div>
		<div class="ke-field-content">
			${ ui.includeFragment("kenyaui", "widget/field", [
					formFieldName: "param[" + param.name + "]",
					class: param.type,
					required: true,
					initialValue: param.defaultValue
			]) }
		</div>
		<% } %>
	</form>
</div>
 <div class="ke-panel-footer">
	<button type="button" id="${ config.id }_btn"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/start.png") }" /> Request</button>
	<button type="button" onclick="kenyaui.closeDialog();"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/cancel.png") }" /> Cancel</button>
</div>
<input type="hidden"  id="report-period" name="period-report" value="${ period }"/>
