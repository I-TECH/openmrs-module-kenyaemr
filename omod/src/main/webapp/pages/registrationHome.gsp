<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ layout: "sidebar" ])
%>

<style type="text/css">
#end-of-day {
	display: none;
}
#end-of-day .spaced {
	padding: 0.3em;
}
#calendar .ui-widget-content {
	border: 0;
	background: inherit;
	padding: 0;
}
</style>

<div id="content-side">
	<div class="panel-frame">
		<div class="panel-heading">Tasks</div>

		${ ui.includeFragment("kenyaemr", "widget/panelMenuItem", [
				iconProvider: "kenyaemr",
				icon: "buttons/patient_search.png",
				label: "Search for a Patient",
				href: ui.pageLink("kenyaemr", "registrationSearch")
		]) }
	</div>

	<div class="panel-frame">
		<div class="panel-heading">Select Day to View</div>
		<div class="panel-content">
			<div id="calendar"></div>
		</div>
	</div>

	<div class="panel-frame" id="end-of-day">
		<div class="panel-heading">End of Day</div>

		<div class="panel-content">
			Close all open visits of the following types:
			<form id="close-visits-form" method="post" action="${ ui.actionLink("kenyaemr", "registrationUtil", "closeActiveVisits") }">
				<div class="form-data"></div>
				<input type="submit" value="Close Visits" />
				<div class="global-error-container" style="display: none">
					${ ui.message("fix.error.plain") }
					<ul class="global-error-content"></ul>
				</div>
			</form>
		</div>
	</div>
</div>

<div id="content-main">

	${ ui.includeFragment("kenyaemr", "dailySchedule", [ id: "schedule", page: "registrationViewPatient", date: scheduleDate ]) }

</div>

<script type="text/javascript">
	jq(function() {
		jq('#calendar').datepicker({
			dateFormat: 'yy-mm-dd',
			defaultDate: '${ new java.text.SimpleDateFormat("yyyy-MM-dd").format(scheduleDate) }',
			gotoCurrent: true,
			onSelect: function(dateText) {
				location.href = ui.pageLink('kenyaemr', 'registrationHome', { scheduleDate: dateText });
			}
		});

		ui.setupAjaxPost('#close-visits-form', {
			onSuccess: function (result) {
				loadActiveVisitTypes();
				notifySuccess(result.message);
			}
		});

		loadActiveVisitTypes();
	});

	function loadActiveVisitTypes() {
		jq.getJSON(ui.fragmentActionLink('kenyaemr', 'registrationUtil', 'activeVisitTypes'), function(result) {
			if (result.length == 0) {
				jq('#end-of-day').hide();
				return;
			}
			else {
				var str = "";
				for (var i = 0; i < result.length; ++i) {
					var r = result[i];
					str += '<div class="spaced"><input type="checkbox" name="visitType" value="' + r.visitTypeId + '"/> ' + r.name + ' (' + r.count + ')</div>';
				}
				jq('#end-of-day .form-data').html(str);
				jq('#end-of-day').show();
			}
		});
	}
</script>