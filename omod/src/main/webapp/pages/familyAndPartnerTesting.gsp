<%
	ui.decorateWith("kenyaemr", "standardPage", [ patient: currentPatient, layout: "sidebar" ])
%>
<style>
div.grid      { display:block; }
div.grid div  { float: left; height: 30px; }
div.patient-name    { width: 300px; }
div.patient-age    { width: 70px; }
div.test-date    { width: 120px; }
div.test-result      { width: 120px; }
div.date-enrolled       { width: 120px; }
div.patient-no       { width: 200px; }
div.alive       { width: 50px; }
div.clear     { clear: both; }
.col-header {font-weight: bold; font-size: 14px;}
</style>
<script type="text/javascript">

</script>

<div class="ke-page-sidebar">
	<div class="ke-panel-frame">
		${ ui.includeFragment("kenyaui", "widget/panelMenuItem", [ iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back", href: returnUrl ]) }
	</div>
</div>

<div class="ke-page-content">
	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Family/Partner Tree </div>
		<div class="ke-panel-content">
        <label>People in the facility</label>
			<% if (relationships) { %>
            <div class="grid">
                <div class="patient-name">&nbsp;</div>
                <div class="patient-age col-header">Age</div>
                <div class="test-date col-header">Test Date</div>
                <div class="test-result col-header">Test Result</div>
                <div class="date-enrolled col-header">Date Enrolled</div>
                <div class="patient-no col-header">ART No</div>
                <div class="alive col-header">Status</div>
            </div>
            <div class="clear"></div>
			<% relationships.each { rel -> %>
			<div class="ke-stack-item">
                <div class="grid">
                    <div class="patient-name">
                        ${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: ui.format(rel.type), value: rel.personLink ]) }
                    </div>
                    <div class="patient-age">${rel.age}</div>
                    <div class="test-date">Test Date</div>
                    <div class="test-result">Test Result</div>
                    <div class="date-enrolled">Date Enrolled</div>
                    <div class="patient-no">${rel.art_no}</div>
                    <div class="alive">${rel.status}</div>
                </div>
                <div class="clear"></div>


			</div>
			<% } } else {%>
            No record was found
			<% } %>
		</div>

        <div class="ke-panel-content">
            <label>People not registered in the facility</label>
            <% if (otherContacts) { %>
            <div class="grid">
                <div class="patient-name">&nbsp;</div>
                <div class="patient-age col-header">Age</div>
                <div class="test-date col-header">Baseline HIV Status</div>
                <div class="test-result col-header">Test Date</div>
                <div class="date-enrolled col-header">Test Result</div>
                <div class="patient-no col-header">In Care</div>
                <div class="alive col-header">ART Number</div>
            </div>
            <div class="clear"></div>
            <% otherContacts.each { rel -> %>
            <div class="ke-stack-item">
                <div class="grid">
                    <div class="patient-name">
                        ${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: ui.format(rel.relType), value: rel.contactName ]) }
                    </div>
                    <div class="patient-age">${rel.age}</div>
                    <div class="test-date">${rel.baselineStatus}</div>
                    <div class="test-result">${rel.nextTestDate}</div>
                    <div class="date-enrolled">${rel.hivResult}</div>
                    <div class="patient-no">${rel.inCare}</div>
                    <div class="alive">${rel.art_no}</div>
                </div>
                <div class="clear"></div>

            </div>
            <% } } else {%>
            No record was found
            <% } %>
        </div>

	</div>
</div>