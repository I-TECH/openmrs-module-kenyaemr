<%
	def heading = "Scheduled for "
	if (isToday)
		heading += "Today"
	else if (isTomorrow)
		heading += "Tomorrow"
	else if (isYesterday)
		heading += "Yesterday"
	else
		heading += kenyaUi.formatDate(date)

	ui.decorateWith("kenyaui", "panel", [ heading: heading ])

	config.require("page")
%>

<% if (!scheduled) { %>
	No visits
<% } %>
<% scheduled.each { %>
	<div class="ke-stack-item ke-clickable">
		<input type="hidden" name="clickUrl" value="${ ui.pageLink("kenyaemr", config.page, [ patientId: it.patient.id ]) }"/>
		<table width="100%">
			<tr>
				<td align="left" width="40%" valign="top">
					<span class="ke-icon">
						<img width="32" height="32" src="${ ui.resourceLink("kenyaui", "images/patient_" + it.patient.gender.toLowerCase() + ".png") }" alt="" />
					</span>
					
					<b>${ it.patient.name }</b><br />
					${ it.patient.age } <small>(${ it.patient.birthdate })</small>
				</td>
				<td align="left" width="30%" valign="top">
					<% it.patient.identifiers.each { %>
						<div class="ke-identifier-type">${ ui.format(it.identifierType) }:</div>
						<div class="ke-identifier-value">${ it.identifier }</div>
					<% } %>
				</td>
				<td align="right" width="30%" valign="top">
					<% if (it.visits) { %>
						<u><small>Seen Today</small></u>
						<br/>
						<% it.visits.each { v -> %>
							<div class="ke-tag ke-visittag">
								<input type="hidden" name="clickUrl" value="${ ui.pageLink("kenyaemr", config.page, [ patientId: it.patient.patientId, visitId: v.id ]) }"/>
								${ ui.format(v.visitType) } visit<br/>
								<span style="color: gray">
									<%= v.encounters.collect { ui.format(it.form ?: it.encounterType) } .join(", ") %>
								</span>
							</div>
						<% } %>
					<% } else { %>
						<i>Not seen today</i>
					<% } %>
				</td>
			</tr>
		</table>
	</div>
<% } %>