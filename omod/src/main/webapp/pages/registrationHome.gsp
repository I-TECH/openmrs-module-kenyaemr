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

	<div class="panel-frame" id="end-of-day">
		<div class="panel-heading">End of Day</div>

		<div class="panel-content">
			Close all open visits of the following types:
			<form method="post" action="${ ui.actionLink("kenyaemr", "registrationUtil", "closeActiveVisits") }">
				<div class="form-data">
				</div>
				<input type="submit" value="Close Visits"/>
			</form>
		</div>
	</div>
</div>

<div id="content-main">
	<h2>Today's Schedule <a href="${ ui.pageLink("kenyaemr", "dailySchedule") }" style="font-size: 0.6em;">(calendar)</a></h2>

	${ ui.includeFragment("kenyaemr", "dailySchedule", [ id: "todaysSchedule", page: "registrationViewPatient" ]) }
</div>

<script type="text/javascript">
	getJsonAsEvent(ui.fragmentActionLink('kenyaemr', 'patientSearch', 'withActiveVisits'), 'checkedInPatients/show');
	jq.getJSON(ui.fragmentActionLink('kenyaemr', 'registrationUtil', 'activeVisitTypes'), function(result) {
		if (result.length == 0) {
			return;
		}
		var str = "";
		for (var i = 0; i < result.length; ++i) {
			var r = result[i];
			str += '<div class="spaced"><input type="checkbox" name="visitType" value="' + r.visitTypeId + '"/> ' + r.name + ' (' + r.count + ')</div>';
		}
		jq('#end-of-day .form-data').html(str);
		jq('#end-of-day').show();
	});
</script>