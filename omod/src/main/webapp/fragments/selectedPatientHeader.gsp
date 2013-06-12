<%
	def closeChartUrl = config.closeChartUrl ?: appHomepageUrl
%>
<script type="text/javascript">
	jq(function() {
		jq('#patient-record-close-link').hover(function() {
			jq('#patient-record-close-text').addClass('ke-glowing');
		}, function() {
			jq('#patient-record-close-text').removeClass('ke-glowing');
		});
	});
</script>

<div class="ke-patientheader">
	<div style="float: left; width: 35%;">
		<div style="float: left; padding-right: 5px">
			<img width="32" height="32" src="${ ui.resourceLink("kenyaui", "images/patient_" + patient.gender.toLowerCase() + ".png") }"/>
		</div>
		<span class="ke-patient-name">${ kenyaEmrUi.formatPersonName(patient.personName) }</span><br/>
		<span class="ke-patient-gender">${ patient.gender == 'M' ? "Male" : patient.gender == 'F' ? 'Female' : patient.gender }</span>,
		<span class="ke-patient-age">${ kenyaEmrUi.formatPersonAge(patient) } <small>(${ kenyaEmrUi.formatPersonBirthdate(patient) })</small></span>
	</div>
	
	<div style="float: left; width: 30%">
		<% idsToShow.each { %>
			<div style="text-align: center"><span class="ke-identifier-type">${ it.identifierType.name }</span> <span class="ke-identifier-value">${ it.identifier }</span></div>
		<% } %>
	</div>
	
	<% if (closeChartUrl) { %>
		<div style="float: right">
			<span id="patient-record-close-text" class="ke-tip">Close chart </span>
			<a href="${ closeChartUrl }" id="patient-record-close-link"><img title="Close this patient chart" style="vertical-align: middle" src="${ ui.resourceLink("kenyaui", "images/buttons/close.png") }"/></a>
		</div>
	<% } %>

	<div style="clear: both; height: 5px;"></div>

	${ ui.includeFragment("kenyaemr", "clinicalAlerts") }

	<div id="active-visit">
		<span class="ke-tip">Current visit</span>
		<% if (activeVisit) {
			def visitStartStr = activeVisitStartedToday ? kenyaUi.formatTime(activeVisit.startDatetime) : kenyaUi.formatDate(activeVisit.startDatetime);

			%><span class="ke-tag ke-visittag">${ ui.format(activeVisit.visitType) } since <b>${ visitStartStr }</b></span><%
		} else {
			%><span style="font-style: italic">${ ui.message("general.none") }</span><%
		}
		%>
	</div>
</div>