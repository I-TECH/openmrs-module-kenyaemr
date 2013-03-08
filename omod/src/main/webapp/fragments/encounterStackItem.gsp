<%
	config.require("encounter")

	def encounter = config.encounter
	def title = ui.format(encounter.form ?: encounter.encounterType)

	if (config.showEncounterDate) {
		title += " (" + ui.format(encounter.encounterDatetime) + ")"
	}

	def providers = encounter.providersByRoles.values().collectAll { ui.format(it) }.flatten().join(", ")

	def form = encounter.form ? kenyaUi.simpleForm(encounter.form, ui) : [ iconProvider : "kenyaemr", icon : "forms/generic.png" ]
%>

<div class="stack-item clickable encounter-item">
	<div style="float: left; margin: 3px">
		${ ui.includeFragment("kenyaemr", "widget/icon", [ iconProvider: form.iconProvider, icon: form.icon, useViewOverlay: true, tooltip: "View Encounter" ]) }
	</div>
	<input type="hidden" name="encounterId" value="${ encounter.encounterId }"/>
	<b>${ title }</b> by ${ providers }<br/>
	<span style="color: gray">
		Entered by ${ ui.format(encounter.creator) } on ${ ui.format(encounter.dateCreated) }<% if (encounter.dateChanged) { %>, last edit by ${ ui.format(encounter.changedBy) } on ${ ui.format(encounter.dateChanged) }<% } %>
	</span>
	<div style="clear: both"></div>
</div>