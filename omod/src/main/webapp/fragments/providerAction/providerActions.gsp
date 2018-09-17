<%
	//ui.decorateWith("kenyaui", "panel", [ heading: "Provider Actions" ])
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
					<i class="icon-check-in float-left"></i>
					Patient Overview
				</a>
			</li>

			<li class="float-left">
				<a href="javascript:openPatientSummary()" class="float-left">
					<i class="icon-plus float-left"></i>
					Patient Summary
				</a>
			</li>

			<li class="float-left">
			<a href="${ ui.pageLink("kenyaemr", "registration/registrationSearch") }" class="float-left">
					<i class="icon-search float-left"></i>
					Patient Search/Registration
				</a>
			</li>

			<li class="float-left">
				<a href="${ ui.pageLink("kenyaemrpsmart", "kenyaemrpsmarthome", [patientId: currentPatient.patientId]) }" class="float-left">
					<i class="icon-plus-sign-alt float-left"></i>
					P-Smart Data
				</a>
			</li>

			<li class="float-left">
				<a href="${ ui.pageLink("hivtestingservices", "patientContactList", [patientId: currentPatient.patientId]) }" class="float-left">
					<i class="icon-remove float-left"></i>
					Patient Contact Listing
				</a>
			</li>

			<li class="float-left">
				<a href="${ ui.pageLink("orderentryui", "drugOrders", [patient: currentPatient]) }" class="float-left">
					<i class="icon-remove float-left"></i>
					Drug Orders
				</a>
			</li>

			<li class="float-left">
				<a href="${ ui.pageLink("orderentryui", "labOrders", [patient: currentPatient]) }" class="float-left">
					<i class="icon-remove float-left"></i>
					Lab Orders
				</a>
			</li>

		</ul>


	</div>
</div>
