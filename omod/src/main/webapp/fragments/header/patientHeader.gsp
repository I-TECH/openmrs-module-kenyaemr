<%
	def closeChartUrl = config.closeChartUrl ?: appHomepageUrl
%>
<script type="text/javascript">
	function ke_closeChart() {
		kenyaui.openConfirmDialog({ heading: 'Patient chart', message: 'Close this patient chart?', okCallback: function() {
			ui.navigate('${ ui.escapeJs(closeChartUrl) }');
		}});
	}
</script>

<div class="ke-patientheader">
	<div style="float: left; width: 35%;">
		<div style="float: left; padding-right: 5px">
			<img width="32" height="32" src="${ ui.resourceLink("kenyaui", "images/buttons/patient_" + patient.gender.toLowerCase() + ".png") }"/>
		</div>
		<span class="ke-patient-name">${ kenyaui.formatPersonName(patient) }</span><br/>
		<span class="ke-patient-gender">${ kenyaui.formatPersonGender(patient) }</span>,
		<span class="ke-patient-age">${ kenyaui.formatPersonAge(patient) } <small>(${ kenyaui.formatPersonBirthdate(patient) })</small></span>
	</div>
	
	<div style="float: left; width: 30%">
		<% idsToShow.each { %>
			<div style="text-align: center"><span class="ke-identifier-type">${ it.identifierType.name }</span> <span class="ke-identifier-value">${ it.identifier }</span></div>
		<% } %>
	</div>

	<div style="float: right">
		<button class="ke-compact" title="Close this patient chart" onclick="ke_closeChart()"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/close.png") }"/> Close</button>
	</div>

	<div style="clear: both; height: 5px;"></div>

	<div style="width: 50%; float: left; overflow: auto; text-align: left">
	<% if (patient.dead) { %>
		<span class="ke-tag" style="background-color: #FF5153; color: #000">Deceased since <strong>${ kenyaui.formatDate(patient.deathDate) }</strong></span>
	<% } else if (patient.voided) { %>
		<span class="ke-tag" style="background-color: #000; color: #FFF">Voided since <strong>${ kenyaui.formatDate(patient.dateVoided) }</strong></span>
	<% } else { %>
		${ ui.includeFragment("kenyaemr", "header/patientFlags", [ patient: patient ]) }
	<% } %>
	</div>

	<div style="width: 50%; float: right; overflow: auto; text-align: right">
		<span class="ke-tip">Current visit</span>
		<% if (visit) {
			def visitStartStr = visitStartedToday ? kenyaui.formatTime(visit.startDatetime) : kenyaui.formatDate(visit.startDatetime);

			%><span class="ke-visittag">${ ui.format(visit.visitType) } since <b>${ visitStartStr }</b></span><%
		} else {
			%><span style="font-style: italic">${ ui.message("general.none") }</span><%
		}
		%>
	</div>
</div>