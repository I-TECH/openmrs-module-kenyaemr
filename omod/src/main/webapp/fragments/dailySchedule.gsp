<%
config.require("page")
/* TODO redo this to look like patientList */
%>

<% if (!scheduled) { %>
	None
<% } %>
<% scheduled.each { %>
	<div class="panel clickable scheduled-visit">
		<input type="hidden" name="clickUrl" value="${ ui.pageLink(config.page, [ patientId: it.patient.id ]) }"/>
		<table width="100%">
			<tr>
				<td>
					<span class="title">
						${ ui.includeFragment("kenyaemrPersonName", [ name: it.patient.personName]) }
					</span>
					<span class="leftDetails">
						${ it.patient.gender }, ${ it.patient.age } year(s) old
					</span>
				</td>
				<td valign="right" style="padding-left: 1em">
					<% if (it.visits) { %>
						<% it.visits.each { v -> %>
							<div class="encounter-panel">
								<input type="hidden" name="clickUrl" value="${ ui.pageLink(config.page, [ patientId: it.patient.id, visitId: v.id ]) }"/>
								<u><b>${ ui.format(v.visitType) } visit</b></u><br/>
								<span style="color: gray">
									<%= v.encounters.collect { ui.format(it.form ?: it.encounterType) } .join(", ") %>
								</span>
							</div>
						<% } %>
					<% } else { %>
						No visit.
						<img src="${ ui.resourceLink("uilibrary", "images/close_32.png") }"/>
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