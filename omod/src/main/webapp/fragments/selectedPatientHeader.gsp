<%
	def closeChartUrl = config.closeChartUrl ?: appHomepageUrl
%>
<style>
	#selected-patient-header {
		border-bottom: 1px black solid;
		background-color: #808080;
		color: white;
	}
	
	#selected-patient-header > .demographics {
		float: left;
		font-weight: bold;
		width: 35%;
	}
	
	#selected-patient-header .icon {
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
	
	#selected-patient-header > .checked-in {
		float: right;
		text-align: right;
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
		<div class="icon">
			<img width="32" height="32" src="${ ui.resourceLink("uilibrary", "images/patient_" + patient.gender + ".gif") }"/>
		</div>
		${ ui.includeFragment("kenyaemrPersonName", [ name: patient.personName ]) }<br/>
		${ patient.gender == 'M' ? "Male" : patient.gender == 'F' ? 'Female' : patient.gender } - ${ patient.age }y
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
		<div class="close-patient selected-visit">
			<a href="${ closeChartUrl }"><img title="Close Chart" src="${ ui.resourceLink("uilibrary", "images/folder_close_32.png") }"/></a>
		</div>
	<% } %>

	<div class="checked-in">
		<% if (activeVisits) { %>
			<img src="${ ui.resourceLink("kenyaemr", "images/checked_in_16.png") }"/><br/>
			<%= activeVisits.collect { ui.format(it.visitType) }.join(", ") %>
		<% } %>
	</div>
	
	<div style="clear: both"></div>
	${ ui.includeFragment("clinicalAlerts") }
</div>