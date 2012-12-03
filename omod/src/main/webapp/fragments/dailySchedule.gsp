<%
	def heading = "Schedule for "
	if (isToday)
		heading += "Today"
	else if (isTomorrow)
		heading += "Tomorrow"
	else if (isYesterday)
		heading += "Yesterday"
	else
		heading += kenyaEmrUi.formatDateNoTime(date)

	ui.decorateWith("kenyaemr", "panel", [ heading: heading ])

	config.require("page")
%>

<% if (!scheduled) { %>
	No visits
<% } %>
<% scheduled.each { %>
	<div class="panel clickable scheduled-visit">
		<input type="hidden" name="clickUrl" value="${ ui.pageLink("kenyaemr", config.page, [ patientId: it.patient.id ]) }"/>
		<table width="100%">
			<tr>
				<td width="40%">
					<span class="icon">
						<img width="32" height="32" src="${ ui.resourceLink("kenyaemr", "images/patient_" + it.patient.gender + ".gif") }"/>
					</span>
					
					<span class="leftText">
						<span class="title">
							${ ui.includeFragment("kenyaemr", "kenyaemrPersonName", [ name: it.patient.personName]) }
						</span>
						<span class="leftDetails">
							${ ui.includeFragment("kenyaemr", "kenyaemrPersonAgeAndBirthdate", [ person: it.patient ]) }
						</span>
					</span>
				</td>
				<td align="center width="30%">
					<% it.patient.activeIdentifiers.each { %>
						<span class="identifier-label">${ ui.format(it.identifierType) }:</span><br/>
						<span class="identifier-value">${ it.identifier }</span><br/>
					<% } %>
				</td>
				<td align="right" width="30%">
					<% if (it.visits) { %>

						<u>
							<img src="${ ui.resourceLink("kenyaemr", "images/visit.png") }"/>
							<small>Seen Today</small>
						</u>
						<br/>
						<% it.visits.each { v -> %>
							<div class="encounter-panel">
								<input type="hidden" name="clickUrl" value="${ ui.pageLink("kenyaemr", config.page, [ patientId: it.patient.id, visitId: v.id ]) }"/>
								${ ui.format(v.visitType) } visit<br/>
								<span style="color: gray">
									<%= v.encounters.collect { ui.format(it.form ?: it.encounterType) } .join(", ") %>
								</span>
							</div>
						<% } %>

					<% } else { %>

						<i>
							Not seen today
							<img src="${ ui.resourceLink("uilibrary", "images/close_32.png") }"/>
						</i>
						
					<% } %>
				</td>
			</tr>
		</table>
	</div>
<% } %>
</ul>

<script type="text/javascript">
	jq(function() {
		jq('.scheduled-visit').click(function(evt) {
			var url = jq(this).find('input[name=clickUrl]').val();
			location.href = url;
		});
	});
</script>