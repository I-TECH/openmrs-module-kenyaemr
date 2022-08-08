<%
	ui.includeCss("kenyaemrorderentry", "font-awesome.css")
	ui.includeCss("kenyaemrorderentry", "font-awesome.min.css")
	ui.includeCss("kenyaemrorderentry", "font-awesome.css.map")
	ui.includeCss("kenyaemrorderentry", "fontawesome-webfont.svg")
%>
<style>
.action-container {
	display: inline;
	float: left;
	width: 99.9%;
	margin: 0 1.04167%;
}
.action-section {
	margin-top: 2px;
	background: white;
	border: 1px solid #dddddd;
}
.float-left {
	float: left;
	clear: left;
	width: 97.91666%;
	color: white;
}

.action-section a:link {
	color: white;!important;
}

.action-section a:hover {
	color: white;
}

.action-section a:visited {
	color: white;
}
.action-section h3 {
	margin: 0;
	color: white;
	border-bottom: 1px solid white;
	margin-bottom: 5px;
	font-size: 1.5em;
	margin-top: 5px;
}
.action-section ul {
	background: #7f7b72;
	color: white;
	padding: 5px;
}

.action-section li {
	font-size: 1.1em;
}
.action-section i {
	font-size: 1.1em;
	margin-left: 8px;
}
</style>
<div class="action-container">
	<div class="action-section">


		<ul class="float-left">
			<h3>Provider Actions</h3>

			<li class="float-left">
				<a href="javascript:openPatientChart()"  class="float-left">
					<i class="fa fa-list-alt fa-2x" style="margin-top: 7px"></i>
					Patient Overview
				</a>
			</li>

			<li class="float-left">
				<a href="javascript:openPatientSummary()" class="float-left">
					<i class="fa fa-file-text-o fa-4x" style="margin-top: 7px"></i>
					Patient Summary
				</a>
			</li>

			<li class="float-left" style="margin-top: 7px">
			<a href="${ ui.pageLink("kenyaemr", "registration/registrationSearch") }" class="float-left">
					<i class="fa fa-search fa-2x"></i>
					Find/Create Patient
				</a>
			</li>

			<li class="float-left" style="margin-top: 7px">
				<a href="${ ui.pageLink("hivtestingservices", "patientContactList", [patientId: currentPatient.patientId]) }" class="float-left">
					<i class="fa fa-list-ul fa-2x"></i>
					Contact Listing
				</a>
			</li>

			<li class="float-left" style="margin-top: 7px">
				<a href="${ ui.pageLink("kenyaemrorderentry", "orders/drugOrderHome", [patientId: currentPatient]) }" class="float-left">
					<i class="fa fa-medkit fa-2x"></i>
					Drug Orders
				</a>
			</li>

			<li class="float-left" style="margin-top: 7px">
				<a href="${ ui.pageLink("kenyaemrorderentry", "orders/labOrderHome", [patientId: currentPatient]) }" class="float-left">
					<i class="fa fa-flask fa-2x"></i>
					Lab Orders
				</a>
			</li>
			<li class="float-left" style="margin-top: 7px">
				<a href="${ ui.pageLink("covid19", "covidHome", [patientId: currentPatient]) }" class="float-left">
					<i class="fa fa fa-cog fa-2x"></i>
					Covid-19
				</a>
			</li>

		</ul>



	</div>

</div>
