<%
    ui.decorateWith("kenyaemr", "standardPage", [ patient: currentPatient, closeChartUrl: ui.pageLink("kenyaemr", "home") ])
%>

<script type="text/javascript">
    //On ready
    jq = jQuery;
    jq(function () {
        // Redirect to O3
        var patient = ${ patientDetails };
        var patientId = (patient.patientExists == true) ? patient.patientId : "";
        var patientUUID = (patient.patientExists == true) ? patient.patientUUID : "";
        var getUrl = window.location;
        var baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
        baseUrl = baseUrl + "/spa"
        homeBaseUrl = baseUrl + "/home"
        if(isEmpty(patientUUID) == false) {
            // Sample: http://localhost:8080/openmrs/spa/patient/49ceb938-bac0-4514-b712-7452121a8c24/chart/Patient%20Summary
            baseUrl = baseUrl + "/patient/" + patientUUID + "/chart/Patient%20Summary"
            var spaChartUrl = new URL(baseUrl);
            window.location.replace(spaChartUrl);
        } else {
            var spaUrl = new URL(homeBaseUrl);
            window.location.replace(spaUrl);
        }

        function isEmpty(value) {
            return (value === null || value === undefined || value.length === 0);
        }
    });
</script>