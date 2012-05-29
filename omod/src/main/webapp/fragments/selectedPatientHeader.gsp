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
</style>

<div id="selected-patient-header">
	<div class="demographics">
		<div class="icon">
			<img width="32" height="32" src="${ ui.resourceLink("uilibrary", "images/patient_" + patient.gender + ".gif") }"/>
		</div>
		${ patient.personName } (${ patient.gender }, ${ patient.age }y)
	</div>
	
	<div class="identifiers">
		<% patient.activeIdentifiers.each { %>
			<span class="identifier-type">
				${ it.identifierType.name }:
			</span>
			<span class="identifier-value">
				${ it.identifier }
			</span>
			<br/>
		<% } %>
	</div>
	
	<div class="checked-in">
		<% if (activeVisits) { %>
			<img src="${ ui.resourceLink("kenyaemr", "images/checked_in_16.png") }"/><br/>
			<%= activeVisits.collect { ui.format(it.visitType) }.join(", ") %>
		<% } %>
	</div>
	
	<div style="clear: both"> </div>
</div>