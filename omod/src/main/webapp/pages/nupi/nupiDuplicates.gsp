<%
    ui.decorateWith("kenyaemr", "standardPage", [layout: "sidebar"])
    def menuItems = [
            [label: "Back to NUPI Home", iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back to NUPI Home", href: ui.pageLink("kenyaemr", "nupi/nupiVerificationHome")],           
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
    width: 100%;
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
    width: 240px;
}
.identifiersColumn {
    width: 240px;
}
.facilityNamesColumn {
    width: 240px;
}
.genderColumn {
    width: 26px;
}
.dobColumn {
    width: 80px;
}
.nupiColumn {
    width: 100px;
}
.upiColumn {
    width: 100px;
}
.facilitiesColumn {
    width: 240px;
}
.totalSitesColumn {
    width: 90px;
}
.selectColumn {
    width: 40px;
    padding-left: 5px;
}
.actionColumn {
    width: 90px;
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

.resolveButton {
    background-color: cadetblue;
    color: white;
    margin-right: 5px;
    margin-left: 5px;
}

.resolveButton:hover {
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
</div>

<div class="ke-page-content">

    <div>
        <fieldset>
            <legend>NUPI Duplicates Summary</legend>
            <div>
                <table class="simple-table" width="100%">
                    <thead>
                    </thead>
                    <tbody>
                    <tr>
                        <td width="15%">Total Duplicates</td>
                        <td>${duplicatesCount}</td>

                        <td width="15%"></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td width="15%"></td>
                        <td></td>

                        <td width="15%"></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td width="15%"> <button id="pullDuplicates">Pull NDWH NUPI Duplicates</button></td>
                        <td> <div class="wait-loading"></div> <div class="text-wrap" align="center" id="pull-msgBox"></div></td>

                        <td width="15%"></td>
                        <td></td>

                    </tr>
                    </tbody>
                </table>
            </div>
        </fieldset>
    </div>

    <div id="program-tabs" class="ke-tabs">
        <div class="ke-tabmenu">
                <div class="ke-tabmenu-item" data-tabid="duplicates_queue">Patient list</div>
        </div>


        <div class="ke-tab" data-tabid="duplicates_queue">
            <table id="error-queue-data" cellspacing="0" cellpadding="0" width="100%">
                <tr>
                    <td style="width: 99%; vertical-align: top">
                        <div class="ke-panel-frame">
                            <div class="ke-panel-heading">NDWH NUPI Duplicates</div>

                            <div class="ke-panel-content">
                                <fieldset>
                                    <legend></legend>
                                    <table class="simple-table" width="100%">
                                        <thead>
                                            <tr>
                                                <th class="clientNameColumn">Full Name</th>
                                                <th class="identifiersColumn">Identifiers</th>
                                                <th class="genderColumn">Sex</th>
                                                <th class="dobColumn">DOB</th>
                                                <th class="nupiColumn">NUPI</th>
                                                <th class="upiColumn">CCC</th>
                                                <th class="facilityNamesColumn">Active Facilities</th>
                                                <th class="totalSitesColumn">Total Sites</th>
                                                <th class="actionColumn">
                                                    Action
                                                </th>
                                            </tr>
                                        </thead>
                                        <tbody id="duplicates-list">

                                        </tbody>

                                    </table>

                                    <div id="pager">
                                        <ul id="errorPagination" class="pagination-sm"></ul>
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
    var selectedErrors = [];
    //On ready
    jq = jQuery;
    jq(function () {

        jQuery("#pull-msgBox").hide();

        // apply pagination
        var errorPaginationDiv = jq('#errorPagination');
        var queuePaginationDiv = jq('#queuePagination');

        var errorListDisplayArea = jq('#duplicates-list');

        var numberOfErrorRecords = ${ duplicatesCount };

        var errorRecords = ${ patientDuplicatesList };

        var errorDataDisplayRecords = [];

        var recPerPage = 10;
        var errorStartPage = 1;
        var totalErrorPages = Math.ceil(numberOfErrorRecords / recPerPage);

        var visibleErrorPages = 1;
        var visibleQueuePages = 1;

        var payloadEditor = {};

        if (totalErrorPages <= 5) {
            visibleErrorPages = totalErrorPages;
        } else {
            visibleErrorPages = 5;
        }
        if (numberOfErrorRecords > 0) {
            apply_pagination(errorPaginationDiv, errorListDisplayArea, totalErrorPages, visibleErrorPages, errorRecords, errorDataDisplayRecords, 'error', errorStartPage); // records in error
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

        jq(document).on('click','.resolveButton',function(){
            ui.navigate('kenyaemr', 'registration/editPatient', { patientId: jq(this).val(),  returnUrl: location.href });
        });

        // handle click event of the fetch NUPI duplicates
       jq("#pullDuplicates").click( function() {
            //Run the fetch task
            jQuery("#pull-msgBox").hide();
            console.log('Starting the fetch task!');
            // show spinner
            display_loading_validate_identifier(true);
            jQuery.getJSON('${ ui.actionLink("kenyaemr", "nupi/nupiDataExchange", "pullDuplicatesFromNDWH")}')
               .success(function (data) {
                    if(data.success == true) {
                       // Hide spinner
                       display_loading_validate_identifier(false);
                       jQuery("#pull-msgBox").text("Successfully pulled NUPI duplicates");
                       jQuery("#pull-msgBox").show();
                   } else {
                       display_loading_validate_identifier(false);
                       jQuery("#pull-msgBox").text("Error pulling NUPI duplicates: Check your network");
                       jQuery("#pull-msgBox").show();
                   }
               })
               .fail(function (err) {
                    console.log("Error fetching NUPI duplicates: " + JSON.stringify(err));
                    // Hide spinner
                    display_loading_validate_identifier(false);
                    jQuery("#pull-msgBox").text("Could not pull NUPI duplicates");
                    jQuery("#pull-msgBox").show();

                })
        });

    });

    function generate_table(displayRecords, displayObject, tableId) {
        var tr;
        displayObject.html('');
        for (var i = 0; i < displayRecords.length; i++) {

            tr = jq('<tr/>');
            tr.append("<td>" + displayRecords[i].fullName + "</td>");
            tr.append("<td>" + displayRecords[i].identifiers + "</td>");
            tr.append("<td>" + displayRecords[i].gender + "</td>");
            tr.append("<td>" + displayRecords[i].birthdate + "</td>");
            tr.append("<td>" + displayRecords[i].nupi + "</td>");
            tr.append("<td>" + displayRecords[i].ccc + "</td>");
            tr.append("<td>" + displayRecords[i].facilityNames + "</td>");
            tr.append("<td>" + displayRecords[i].totalFacilities + "</td>");

            var actionTd = jq('<td/>');

            var btnView = jq('<button/>', {
                text: 'View',
                class: 'resolveButton',
                value: displayRecords[i].id
            });

            actionTd.append(btnView);

            tr.append(actionTd);
            displayObject.append(tr);
        }
    }

    function display_loading_validate_identifier(status) {
        if(status) {
            jq('.wait-loading').empty();
            jq('.wait-loading').append(showLoadingImage);
        } else {
            jq('.wait-loading').empty();
        }
    }

</script>