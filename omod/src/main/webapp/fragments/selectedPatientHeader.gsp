<%
	def closeChartUrl = config.closeChartUrl ?: appHomepageUrl
%>
<style type="text/css">
#selected-patient-header {
	border-bottom: 1px #444 solid;
	padding: 3px;
	background-color: #858580;
	color: white;
}

#selected-patient-header > .demographics {
	float: left;
	font-weight: bold;
	width: 35%;
}

#selected-patient-header .patient-icon {
	float: left;
	padding-right: 1em;
}

#selected-patient-header > .identifiers {
	float: left;
	font-weight: bold;
	width: 30%;
	text-align: center;
}

#selected-patient-header > .identifiers {
	float: left;
	font-weight: bold;
	width: 30%;
	text-align: center;
}

#selected-patient-header > .close-patient {
	float: right;
	padding-left: 0.4em;
	margin-left: 0.4em;
	position: relative;
	top: 3;
}

.header-identifier-type {
	font-weight: normal;
}
</style>

<div id="selected-patient-header">
	<div class="demographics">
		<div class="patient-icon">
			<img width="32" height="32" src="${ ui.resourceLink("kenyaemr", "images/patient_" + patient.gender.toLowerCase() + ".png") }"/>
		</div>
		${ ui.includeFragment("kenyaemrPersonName", [ name: patient.personName ]) }<br/>
		${ patient.gender == 'M' ? "Male" : patient.gender == 'F' ? 'Female' : patient.gender }
		-
		${ ui.includeFragment("kenyaemrPersonAge", [ person: patient ]) }
	</div>
	
	<div class="identifiers">
		<% idsToShow.each { %>
			<span class="header-identifier-type">
				${ it.identifierType.name }:
			</span>
			<span class="header-identifier-value">
				${ it.identifier }
			</span>
			<br/>
		<% } %>
	</div>
	
	<% if (closeChartUrl) { %>
		<div class="close-patient">
			<small>Close chart </small> <a href="${ closeChartUrl }"><img title="Close Chart" style="vertical-align: middle" src="${ ui.resourceLink("kenyaemr", "images/close_patient.png") }"/></a>
		</div>
	<% } %>

	<div style="clear: both; height: 5px;"></div>

	${ ui.includeFragment("clinicalAlerts") }
	${ ui.includeFragment("activeVisits") }

	<div style="clear: both"></div>
</div>