<%
	def closeChartUrl = config.closeChartUrl ?: appHomepageUrl
%>
<script type="text/javascript">
	jq(function() {
		jq('#selected-patient-header-close-link').hover(function() {
			jq('#selected-patient-header-close-text').addClass('ke-glowing');
		}, function() {
			jq('#selected-patient-header-close-text').removeClass('ke-glowing');
		});
	});
</script>

<div class="ke-patientheader">
	<div style="float: left; width: 35%;">
		<div style="float: left; padding-right: 5px">
			<img width="32" height="32" src="${ ui.resourceLink("kenyaui", "images/patient_" + patient.gender.toLowerCase() + ".png") }"/>
		</div>
		<span class="ke-patient-name">${ ui.includeFragment("kenyaemr", "personName", [ name: patient.personName ]) }</span><br/>
		<span class="ke-patient-gender">${ patient.gender == 'M' ? "Male" : patient.gender == 'F' ? 'Female' : patient.gender }</span>,
		<span class="ke-patient-age">${ ui.includeFragment("kenyaemr", "personAgeAndBirthdate", [ person: patient ]) }</span>
	</div>
	
	<div style="float: left; width: 30%; text-align: center">
		<% idsToShow.each { %>
			<span class="ke-identifier-type">${ it.identifierType.name }: </span>
			<span class="ke-identifier-value">${ it.identifier }</span>
			<br/>
		<% } %>
	</div>
	
	<% if (closeChartUrl) { %>
		<div style="float: right">
			<small id="selected-patient-header-close-text">Close chart </small>
			<a href="${ closeChartUrl }" id="selected-patient-header-close-link"><img title="Close Chart" style="vertical-align: middle" src="${ ui.resourceLink("kenyaui", "images/buttons/patient_close.png") }"/></a>
		</div>
	<% } %>

	<div style="clear: both; height: 5px;"></div>

	${ ui.includeFragment("kenyaemr", "clinicalAlerts") }

	<div id="active-visit">
		<small>Current visit</small>
		<% if (activeVisit) {
			def visitStartStr = activeVisitStartedToday ? kenyaUi.formatTime(activeVisit.startDatetime) : kenyaUi.formatDate(activeVisit.startDatetime);

			%><span class="ke-tag ke-visittag">${ ui.format(activeVisit.visitType) } since <b>${ visitStartStr }</b></span><%
		} else {
			%><span style="font-style: italic">${ ui.message("general.none") }</span><%
		}
		%>
	</div>
</div>