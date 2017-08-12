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
</style>
<div class="ke-page-content">
	<div style="font-size: 18px; color: #006056; font-style: normal; font-weight: bold">Facility Dashboard</div>
			<table cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td style="width: 50%; vertical-align: top">
						<div class="ke-panel-frame">
							<div class="ke-panel-heading">Summary of Care and Treatment Statistics</div>
							<div class="ke-panel-content">
								<table class="alignLeft">
									<tr>
										<td colspan="3" class="heading2"><strong>Reporting Period: ${reportPeriod} </strong></td>
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
							</div>
						</div>
					</td>
					<td style="width: 50%; vertical-align: top; padding-left: 5px">
						<div class="ke-panel-frame">
							<div class="ke-panel-heading">Summary of HTS Statistics</div>
							<div class="ke-panel-content">
								<table class="alignLeft">
									<tr>
										<td colspan="3" class="heading2"><strong>Reporting Period: Today</strong></td>
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
							</div>
						</div>
					</td>
				</tr>
			</table>
	<br/>
	<br/>
	<br/>
	<div style="text-align: center; margin: 5px;" >
		<button  type="button" class="ke-app" onclick="${ onClick }"><img src="${ ui.resourceLink("kenyaemr", "images/forward.png") }" />Proceed to Home Page</button>
	</div>
</div>



