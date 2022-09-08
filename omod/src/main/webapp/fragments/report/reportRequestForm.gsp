<%
	config.require("definition")
	config.require("onRequestCallback")

	// Organise parameters by name (for Groovy 1.7.9+ this could be replaced with .collectEntries)
	def params = [:]
	config.definition.parameters.each{ param -> params[param.name] = param }

	def useMonthBasedPeriodField = params.containsKey("startDate") && params.containsKey("endDate")
	
	def useYearBasedPeriodField = params.containsKey("startDate") && params.containsKey("endDate") && params.containsKey("yearBasedReporting")

	def useDateBasedPeriodField = params.containsKey("startDate") && params.containsKey("endDate") && params.containsKey("dateBasedReporting")

	def defaultOption = countyList.size() == 1 ? countyList.toArray()[0] : null;

	def countyListOptions = []

	countyList.each { county ->
		countyListOptions.add([ value: county, label: county ])
	}

	if (useMonthBasedPeriodField || useYearBasedPeriodField) {
		params.remove("startDate")
		params.remove("endDate")
	}
	
	if (useYearBasedPeriodField || useDateBasedPeriodField) {
		useMonthBasedPeriodField = false;
	}
%>
<script type="text/javascript">
	jQuery(function() {
		//Hide all elements whose name is "dateBasedReorting or yearBasedReporting" and set a default value of -1
		//This ensures that additional parameters sent to this form for the purpose of determining how this UI displays are not shown
		var form = jQuery('#${ config.id }');
		form.find('[name^="param["][name\$="BasedReporting]"]').each(function() {
			var field = jQuery(this);
			field.val('-1');
			field.parent().hide();
		});

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
			if(results > today && periodValue <= 60) {
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
		<% if (useMonthBasedPeriodField) { %>
		<div class="ke-field-label">Month</div>
		<div class="ke-field-content">

			${ ui.includeFragment("kenyaemr", "field/reportPeriod", [ pastMonths: 180, pastYears: 0 ]) }
		</div>
		<% } %>
		
		<% if (useYearBasedPeriodField) { %>
		<div class="ke-field-label">Year</div>
		<div class="ke-field-content">

			${ ui.includeFragment("kenyaemr", "field/reportPeriod", [ pastMonths: 0, pastYears: 6 ]) }
		</div>
		<% } %>
		<% if (useDateBasedPeriodField) { %>
		<div class="ke-field-label">Date Range</div>
		<div class="ke-field-content">

			${ ui.includeFragment("kenyaemr", "field/reportDates") }
		</div>
		<% } %>

		<% params.each { name, param -> %>
		<div class="ke-field-label">${ param.label }</div>
		<div class="ke-field-content">
			<% def configOptions = (param.name == "county") ? [ options: countyListOptions ] : [] %>
			${ ui.includeFragment("kenyaui", "widget/field", [
					formFieldName: "param[" + param.name + "]",
					class: param.type,
					required: true,
					initialValue: defaultOption == null ? param.defaultValue : defaultOption,
					config: configOptions
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
