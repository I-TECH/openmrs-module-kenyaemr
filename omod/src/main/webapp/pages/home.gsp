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
	}
	.table-width-half {
		width: 46%;
		/*display: inline-block;*/
	}
	.table-width-70 {
		width: 50%;
		/*float:left;*/
	}
</style>
<div class="ke-page-content">

	<div>
		<fieldset>
			<legend>Facility Statistics</legend>
				<table class="alignLeft table-width-70">
					<tr>
						<td colspan="4" class="heading2"><strong>Summary of Care and Treatment Statistics (${reportPeriod} Period)</strong></td>
					</tr>
					<tr>
						<th>Total Patients</th>
						<th>Current in Care</th>
						<th>Current on ART</th>
						<th>New on ART</th>
						<th>Total with Valid viral loads <br/>(in last 12 months)</th>
						<th>Total suppressed </th>
					</tr>
					<tr>
						<td>${allPatients}</td>
						<td>${inCare}</td>
						<td>${onArt}</td>
						<td>${newOnArt}</td>
						<td>${vlResults}</td>
						<td>${suppressedVl}</td>
					</tr>
				</table>
			<br/>
			<br/>
			<br/>
			<br/>

			<table class="alignLeft table-width-half">
				<tr>
					<td colspan="3" class="heading2"><strong>Summary of HTS Statistics</strong></td>
				</tr>
				<tr>
					<th>&nbsp;</th>
					<th>Total Tested</th>
					<th>Total Positive</th>
					<th>Total Enrolled </th>
				</tr>
				<tr>
					<td><b>Total Contacts</b></td>
					<td>0</td>
					<td>0</td>
					<td>0</td>
				</tr>
				<tr>
					<td><b>Family Members</b></td>
					<td>0</td>
					<td>0</td>
					<td>0</td>
				</tr>
				<tr>
					<td><b>Sexual Partner</b></td>
					<td>0</td>
					<td>0</td>
					<td>0</td>
				</tr>
			</table>
		</fieldset>

	</div>
	<br/>
	<br/>
	<br/>
	<br/>

	<div style="align: center; margin: 5px;" >
		<button style="float: right" type="button" class="ke-app" onclick="${ onClick }"><img src="${ ui.resourceLink("kenyaemr", "images/continue.png") }" />Proceed to Home Page</button>
	</div>

</div>