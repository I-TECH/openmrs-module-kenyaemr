<%
	ui.includeCss("kenyaemr", "referenceapplication.css", 100)
%>
<%
    def homePage = ui.pageLink("kenyaemr", "userHome")
	def url = "kenyaemr/userHome.page"
	ui.decorateWith("kenyaemr", "standardPage", [ patient: currentPatient, closeChartUrl: ui.pageLink("kenyaemr", "home") ])
	def onClick = "ui.navigate('/" + contextPath + "/" + url + "')"
%>
<style>
	.alignLeft {
		text-align: left;
		width: 50%;
	}
	.table-width {
		width: 50%;
	}
</style>
<div class="ke-page-content">

	<div>
		<fieldset>
			<legend>HIV Care</legend>
				<table class="alignLeft table-width">
					<tr>
						<td colspan="4" class="heading2"><strong>Reporting Period:</strong>  ${reportPeriod}</td>
					</tr>
					<tr>
						<th>Total Patients</th>
						<th>Current in Care</th>
						<th>Current on ART</th>
						<th>New on ART</th>
					</tr>
					<tr>
						<td>${allPatients}</td>
						<td>${inCare}</td>
						<td>${onArt}</td>
						<td>${newOnArt}</td>
					</tr>
				</table>
		</fieldset>
	</div>
	<br/>
	<br/>
	<br/>
	<br/>

	<div style="align: center; margin: 5px;" >
		<button type="button" class="ke-app" onclick="${ onClick }"><img src="${ ui.resourceLink("kenyaemr", "images/continue.png") }" />Continue</button>
	</div>

</div>