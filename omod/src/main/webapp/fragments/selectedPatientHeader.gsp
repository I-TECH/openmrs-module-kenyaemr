<%
	def closeChartUrl = config.closeChartUrl ?: appHomepageUrl
%>
<style type="text/css">
#selected-patient-header {
	border-bottom: 1px #444 solid;
	padding: 3px;
	background-color: #858580;
	overflow: auto;
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

#selected-patient-header #selected-patient-header-close {
	float: right;
	padding-left: 0.4em;
	margin-left: 0.4em;
	position: relative;
	top: 3;
}

#selected-patient-header #active-visit {
	width: 50%;
	float: right;
	overflow: auto;
	text-align: right;
}

.header-identifier-type {
	font-weight: normal;
}

#active-visit-time {
	font-weight: bold;
}

.glowing {
	text-shadow: 0 0 1px #FFD;
}
</style>
<script type="text/javascript">
	jq(function() {
		jq('#selected-patient-header-close-link').hover(function() {
			jq('#selected-patient-header-close-text').addClass('glowing');
		}, function() {
			jq('#selected-patient-header-close-text').removeClass('glowing');
		});
	});
</script>

<div id="selected-patient-header">
	<div class="demographics">
		<div class="patient-icon">
			<img width="32" height="32" src="${ ui.resourceLink("kenyaemr", "images/patient_" + patient.gender.toLowerCase() + ".png") }"/>
		</div>
		${ ui.includeFragment("kenyaemr", "personName", [ name: patient.personName ]) }<br/>
		${ patient.gender == 'M' ? "Male" : patient.gender == 'F' ? 'Female' : patient.gender },
		${ ui.includeFragment("kenyaemr", "personAgeAndBirthdate", [ person: patient ]) }
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
		<div id="selected-patient-header-close">
			<small id="selected-patient-header-close-text">Close chart </small>
			<a href="${ closeChartUrl }" id="selected-patient-header-close-link"><img title="Close Chart" style="vertical-align: middle" src="${ ui.resourceLink("kenyaemr", "images/buttons/patient_close.png") }"/></a>
		</div>
	<% } %>

	<div style="clear: both; height: 5px;"></div>

	${ ui.includeFragment("kenyaemr", "clinicalAlerts") }

	<div id="active-visit">
		<small>Current visit</small>
		<% if (activeVisit) {
			def visitStartStr = activeVisitStartedToday ? kenyaUi.formatTime(activeVisit.startDatetime) : kenyaUi.formatDate(activeVisit.startDatetime);

			%><span class="active-visit">${ ui.format(activeVisit.visitType) } since <span id="active-visit-time">${ visitStartStr }</span></span><%
		} else {
			%><span style="font-style: italic">${ ui.message("general.none") }</span><%
		}
		%>
	</div>
</div>