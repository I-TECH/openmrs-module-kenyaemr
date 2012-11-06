<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage")
%>

<style type="text/css">
.first-column, .second-column {
	float: left;
}
.first-column {
	width: 38%;
}
.second-column {
	width: 59%;
}
#end-of-day {
	display: none;
}
#end-of-day .spaced {
	padding: 0.3em;
}
</style>

<div class="first-column">
	<h1>Registration App</h1>
	<h3>Welcome!</h3>
	
	${ ui.includeFragment("uilibrary", "widget/button", [ icon: "buttons/patient_search.png", iconProvider: "kenyaemr", label: "Find a Patient", href: ui.pageLink("kenyaemr", "registrationSearch") ]) }
</div>

<div class="second-column">
<!--
	<h2>Checked In Patients</h2>
	${ ui.includeFragment("kenyaemr", "patientList", [ id: "checkedInPatients", page: "registrationViewPatient" ]) }
-->
	
	<h2>Today's Schedule <a href="${ ui.pageLink("kenyaemr", "dailySchedule") }" style="font-size: 0.6em;">(calendar)</a></h2>
	${ ui.includeFragment("kenyaemr", "dailySchedule", [ id: "todaysSchedule", page: "registrationViewPatient" ]) }
	
	<br/>
	<div id="end-of-day">
		<h3>End-of-Day Tasks</h3>
		Close all open visits of the following types:
		<form method="post" action="${ ui.actionLink("kenyaemr", "registrationUtil", "closeActiveVisits") }">
			<div class="form-data">
			</div>
			<input type="submit" value="Close Visits"/>
		</form>
	</div>
</div>


<script type="text/javascript">
	getJsonAsEvent(actionLink('kenyemr', 'patientSearch', 'withActiveVisits'), 'checkedInPatients/show');
	jq.getJSON(actionLink('registrationUtil', 'activeVisitTypes'), function(result) {
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