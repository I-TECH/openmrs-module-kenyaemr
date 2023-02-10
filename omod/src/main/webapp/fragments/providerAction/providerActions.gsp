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
<script type="text/javascript">
    jq = jQuery;
	function getO3ServiceQueuesURL() {
		var getUrl = window.location;
        var baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
        baseUrl = baseUrl + "/spa/outpatient/home"
        var spaUrl = new URL(baseUrl);
        window.location.replace(spaUrl);
		return(spaUrl);
    }

    function getO3AppointmentsURL() {
        var getUrl = window.location;
        var baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
        baseUrl = baseUrl + "/spa/appointments"
        var spaUrl = new URL(baseUrl);
        window.location.replace(spaUrl);
        return(spaUrl);
    }
    function getO3URL() {
    		var patientID = "${ currentPatient.id }";
            var patientUUID = "${ currentPatient.uuid }";
    		var getUrl = window.location;
            var baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
            baseUrl = baseUrl + "/spa"
            if(isEmpty(patientUUID) == false) {
                // Sample: http://localhost:8080/openmrs/spa/patient/49ceb938-bac0-4514-b712-7452121a8c24/chart/Patient%20Summary
                baseUrl = baseUrl + "/patient/" + patientUUID + "/chart/Patient%20Summary"
            }
            var spaUrl = new URL(baseUrl);
            window.location.replace(spaUrl);
            function isEmpty(value) {
                return (value === null || value === undefined || value.length === 0);
            }
    		return(spaUrl);
    }
</script>
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
			<li class="float-left" style="margin-top: 7px">
				<a href="javascript:getO3URL()" class="float-left">
				  <i class="fa fa fa-cog fa-2x"></i>
					3.x New Patient Chart
				</a>
			</li>
			<li class="float-left" style="margin-top: 7px">
				<a href="javascript:getO3ServiceQueuesURL()" class="float-left">
					<i class="fa fa fa-cog fa-2x"></i>
					3.x New Service Queue Module
				</a>
			</li>
			<li class="float-left" style="margin-top: 7px">
				  <a href="javascript:getO3AppointmentsURL()" class="float-left">
					  <i class="fa fa fa-cog fa-2x"></i>
					  3.x New Appointment Module
				  </a>
			</li>
		</ul>
	</div>

</div>
