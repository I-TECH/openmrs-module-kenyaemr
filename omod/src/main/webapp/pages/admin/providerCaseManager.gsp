<%

	ui.decorateWith("kenyaemr", "standardPage", [layout: "sidebar"])
	def menuItems = [
			[label: "Back", iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back to home", href: ui.pageLink("kenyaemr", "userHome")],
	]

	ui.includeJavascript("kenyaemrorderentry", "jquery.twbsPagination.min.js")

	ui.includeJavascript("afyastat", "bootstrap/bootstrap.bundle.min.js")
	ui.includeCss("afyastat", "bootstrap/bootstrap-iso.css")
%>
<style>
.simple-table {
	border: solid 1px #DDEEEE;
	border-collapse: collapse;
	border-spacing: 0;
	font: normal 13px Arial, sans-serif;
}
.simple-table thead th {

	border: solid 1px #DDEEEE;
	color: #336B6B;
	padding: 10px;
	text-align: left;
	text-shadow: 1px 1px 1px #fff;
}
.simple-table td {
	border: solid 1px #DDEEEE;
	color: #333;
	padding: 5px;
	text-shadow: 1px 1px 1px #fff;
}
table {
	width: 95%;
}
th, td {
	padding: 5px;
	text-align: left;
	height: 30px;
	border-bottom: 1px solid #ddd;
}
tr:nth-child(even) {background-color: #f2f2f2;}
#pager li{
	display: inline-block;
}

#queue-pager li{
	display: inline-block;
}
#verifiedPager li{
	display: inline-block;
}

#queue-pager li{
	display: inline-block;
}
#chk-select-all {
	display: block;
	margin-left: auto;
	margin-right: auto;
}
.selectElement {
	display: block;
	margin-left: auto;
	margin-right: auto;
}
.nameColumn {
	width: 260px;
}
.cccNumberColumn {
	width: 150px;
}
.dateRequestColumn {
	width: 120px;
}
.clientNameColumn {
	width: 120px;
}
.selectColumn {
	width: 40px;
	padding-left: 5px;
}
.actionColumn {
	width: 350px;
}
.sampleStatusColumn {
	width: 150px;
}
.sampleTypeColumn {
	width: 100px;
}

.pagination-sm .page-link {
	padding: .25rem .5rem;
	font-size: .875rem;
}
.page-link {
	position: relative;
	display: block;
	padding: .5rem .75rem;
	margin-left: -1px;
	line-height: 1.25;
	color: #0275d8;
	background-color: #fff;
	border: 1px solid #ddd;
}

.verifyButton {
	background-color: cadetblue;
	color: white;
	margin-right: 5px;
	margin-left: 5px;
}

.verifyButton:hover {
	background-color: steelblue;
	color: white;
}
.wait-loading {
	margin-right: 5px;
	margin-left: 5px;
}
.page-content{
	background: #eee;
	display: inline-block;
	padding: 10px;
	max-width: 660px;
	font-weight: bold;
}
@media screen and (min-width: 676px) {
	.modal-dialog {
		max-width: 600px; /* New width for default modal */
	}
}
</style>

<div class="ke-page-sidebar">
	${ui.includeFragment("kenyaui", "widget/panelMenu", [heading: "Back", items: menuItems])}
    <br/>
	${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Provider", value: personLink ]) }
</div>

<div class="ke-page-content">

	<div id="program-tabs" class="ke-tabs">
		<div class="ke-tabmenu">
			<div class="ke-tabmenu-item" data-tabid="patient_queue">Patient list</div>
		</div>
		<div class="ke-tab" data-tabid="patient_queue">
			<table id="queue-data" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td style="width: 99%; vertical-align: top">
						<div class="ke-panel-frame">
							<div class="ke-panel-heading">Patients for case manager</div>

							<div class="ke-panel-content">
								<fieldset>
									<legend></legend>
									<table class="simple-table" width="100%">
										<thead>

										<tr>
											<th class="clientFirstNameColumn">First Name</th>
											<th class="clientMiddleNameColumn">Middle Name</th>
											<th class="clientLastNameColumn">Last Name</th>
											<th class="RelationshipStartDate">Start Date</th>
                                            <th class="RelationshipEndDate">End Date</th>
										</tr>
										</thead>
										<tbody id="patient-list">

										</tbody>

									</table>

									<div id="pager">
										<ul id="patientPagination" class="pagination-sm"></ul>
									</div>
								</fieldset>
							</div>
						</div>
					</td>
				</tr>
			</table>
		</div>
	</div>


</div>

<script type="text/javascript">
    var loadingImageURL = ui.resourceLink("kenyaemr", "images/loading.gif");
    var showLoadingImage = '<span style="padding:2px; display:inline-block;"> <img src="' + loadingImageURL + '" /> </span>';
    //On ready
    jq = jQuery;
    jq(function () {

        jQuery("#pull-msgBox").hide();

        // apply pagination
        var patientPaginationDiv = jq('#patientPagination');
        var queuePaginationDiv = jq('#queuePagination');

        var patientListDisplayArea = jq('#patient-list');

        var numberOfPatientRecords = ${ patientsForThisProviderSize };

        var patientRecords = ${ patientsForThisProviderList };

        var patientDataDisplayRecords = [];

        var recPerPage = 10;
        var patientStartPage = 1;
        var totalPatientPages = Math.ceil(numberOfPatientRecords / recPerPage);

        var visiblePatientPages = 1;
        var visibleQueuePages = 1;

        var payloadEditor = {};

        if (totalPatientPages <= 5) {
            visiblePatientPages = totalPatientPages;
        } else {
            visiblePatientPages = 5;
        }
        if (numberOfPatientRecords > 0) {
            apply_pagination(patientPaginationDiv, patientListDisplayArea, totalPatientPages, visiblePatientPages, patientRecords, patientDataDisplayRecords, 'relationship', patientStartPage); // records for patients
        }

        function apply_pagination(paginationDiv, recordsDisplayArea, totalPages, visiblePages, allRecords, recordsToDisplay, tableId, page) {
            paginationDiv.twbsPagination({
                totalPages: totalPages,
                visiblePages: visiblePages,
                onPageClick: function (event, page) {
                    displayRecordsIndex = Math.max(page - 1, 0) * recPerPage;
                    endRec = (displayRecordsIndex) + recPerPage;
                    //jq('#page-content').text('Page ' + page);
                    recordsToDisplay = allRecords.slice(displayRecordsIndex, endRec);
                    generate_table(recordsToDisplay, recordsDisplayArea, tableId);
                }
            });
        }
    });

    function generate_table(displayRecords, displayObject, tableId) {
        var tr;
        displayObject.html('');
        for (var i = 0; i < displayRecords.length; i++) {

            tr = jq('<tr/>');
            tr.append("<td>" + displayRecords[i].givenName + "</td>");
            tr.append("<td>" + displayRecords[i].middleName + "</td>");
            tr.append("<td>" + displayRecords[i].familyName + "</td>");
            tr.append("<td>" + displayRecords[i].startDate + "</td>");
            tr.append("<td>" + displayRecords[i].endDate + "</td>");
            var actionTd = jq('<td/>');
            tr.append(actionTd);
            displayObject.append(tr);
        }
    }


</script>