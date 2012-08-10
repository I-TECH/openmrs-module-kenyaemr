<%
config.require("page")
%>

<% if (!scheduled) { %>
	None
<% } %>
<% scheduled.each { %>
	<div class="panel clickable scheduled-visit">
		<input type="hidden" name="clickUrl" value="${ ui.pageLink(config.page, [ patientId: it.patient.id ]) }"/>
		<table width="100%">
			<tr>
				<td width="40%">
					<span class="icon">
						<img width="32" height="32" src="${ ui.resourceLink("uilibrary", "images/patient_" + it.patient.gender + ".gif") }"/>
					</span>
					
					<span class="leftText">
						<span class="title">
							${ ui.includeFragment("kenyaemrPersonName", [ name: it.patient.personName]) }
						</span>
						<span class="leftDetails">
							${ ui.includeFragment("kenyaemrPersonAgeAndBirthdate", [ person: it.patient ]) }
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
							<img src="${ ui.resourceLink("kenyaemr", "images/checked_in_16.png") }"/>
							<small>Seen Today</small>
						</u>
						<br/>
						<% it.visits.each { v -> %>
							<div class="encounter-panel">
								<input type="hidden" name="clickUrl" value="${ ui.pageLink(config.page, [ patientId: it.patient.id, visitId: v.id ]) }"/>
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

<script>
	jq(function() {
		jq('.scheduled-visit').click(function(evt) {
			var url = jq(this).find('input[name=clickUrl]').val();
			location.href = url;
		});
	});
</script>