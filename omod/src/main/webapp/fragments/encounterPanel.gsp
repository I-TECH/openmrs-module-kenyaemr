<%
	config.require("encounter")

	def title = (config.encounter.visit ? ui.format(config.encounter.visit.visitType) + " - " : "") + ui.format(config.encounter.form ?: config.encounter.encounterType)
	def providers = config.encounter.providersByRoles.values().collectAll { ui.format(it) }.flatten().join(", ")
%>

<div class="stack-item clickable encounter-item">
	<img src="${ ui.resourceLink("kenyaemr", "images/buttons/form_view.png") }" style="float: left; margin: 2px" alt="View Encounter" />
	<input type="hidden" name="encounterId" value="${ config.encounter.encounterId }"/>
	<input type="hidden" name="title" value="${ ui.escapeAttribute(title) }"/>
	<b>${ ui.format(config.encounter.form ?: config.encounter.encounterType) }</b>
	by ${ providers }
	<br/>
	<span style="color: gray">
		entered by ${ ui.format(config.encounter.creator) } on ${ ui.format(config.encounter.dateCreated) }<% if (config.encounter.dateChanged) { %>, last edit by ${ ui.format(config.encounter.changedBy) } on ${ ui.format(config.encounter.dateChanged) }<% } %>
	</span>
</div>