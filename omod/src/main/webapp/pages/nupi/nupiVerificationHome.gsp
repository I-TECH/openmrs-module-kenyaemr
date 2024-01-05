<%
    ui.decorateWith("kenyaemr", "standardPage", [layout: "sidebar"])
    def menuItems = [
            [label: "Back", iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back to home", href: ui.pageLink("kenyaemr", "userHome")],           
    ]

    def dataManagementItems = [
            [label: "NUPI Duplicates", iconProvider: "vdot", icon: "icons/Nimeconfirm_sync_38_3.png", label: "NUPI Duplicates", href: ui.pageLink("kenyaemr", "nupi/nupiDuplicates")]
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

    <div class="ke-panel-frame">
        ${ui.includeFragment("kenyaui", "widget/panelMenu", [heading: "Data Management Actions", items: dataManagementItems])}
    </div>
</div>

<div class="ke-page-content">

    <div>
        <fieldset>
            <legend>Client verification summary</legend>
            <div>
                <table class="simple-table" width="100%">
                    <thead>
                    </thead>
                    <tbody>
                    <tr>
                        <td width="15%">Total attempted verification</td>
                        <td>${totalAttemptedVerification}</td>

                        <td width="15%">Total verified</td>
                        <td>${patientVerifiedListSize}</td>
                    </tr>
                    <tr>
                        <td width="15%">Verified currently on ART</td>
                        <td>${patientVerifiedOnARTListSize}</td>

                        <td width="15%">Total pending verification</td>
                        <td>${patientPendingListSize}</td>
                    </tr>
                    <tr>
                        <td width="15%"> <button id="pullVerificationErrors">Pull Verification Errors</button></td>
                        <td> <div class="wait-loading"></div> <div class="text-wrap" align="center" id="pull-msgBox"></div></td>

                        <td width="15%">Failed IPRS verification</td>
                        <td>${numberOfVerificationErrorSize}</td>

                    </tr>
                    </tbody>
                </table>
            </div>
        </fieldset>
    </div>

    <div id="program-tabs" class="ke-tabs">
        <div class="ke-tabmenu">

                <div class="ke-tabmenu-item" data-tabid="pending_queue">Patient list</div>

                <div class="ke-tabmenu-item" data-tabid="verified_with_errors_queue">Verified with Errors</div>

        </div>


        <div class="ke-tab" data-tabid="pending_queue">
            <table id="error-queue-data" cellspacing="0" cellpadding="0" width="100%">
                <tr>
                    <td style="width: 99%; vertical-align: top">
                        <div class="ke-panel-frame">
                            <div class="ke-panel-heading">Pending verification</div>

                            <div class="ke-panel-content">
                                <fieldset>
                                    <legend></legend>
                                    <table class="simple-table" width="100%">
                                        <thead>

                                        <tr>
                                            <th class="clientNameColumn">First Name</th>
                                            <th class="cccNumberColumn">Middle Name</th>
                                            <th class="sampleTypeColumn">Last Name</th>
                                            <th class="dateRequestColumn">Sex</th>
                                            <th class="sampleStatusColumn">DOB</th>
                                            <th class="sampleStatusColumn">Error</th>
                                            <th class="actionColumn">
                                                Action
                                            </th>
                                        </tr>
                                        </thead>
                                        <tbody id="error-list">

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

        <div class="ke-tab" data-tabid="verified_with_errors_queue">
            <table id="verified-with-errors-queue-data" cellspacing="0" cellpadding="0" width="100%">
                <tr>
                    <td style="width: 99%; vertical-align: top">
                        <div class="ke-panel-frame">
                            <div class="ke-panel-heading">Verification errors</div>

                            <div class="ke-panel-content">
                                <fieldset>
                                    <legend></legend>
                                    <table class="simple-table" width="100%">
                                        <thead>

                                        <tr>
                                            <th class="clientNameColumn">First Name</th>
                                            <th class="cccNumberColumn">Middle Name</th>
                                            <th class="sampleTypeColumn">Last Name</th>
                                            <th class="dateRequestColumn">Sex</th>
                                            <th class="sampleStatusColumn">DOB</th>
                                            <th class="sampleStatusColumn">IPRS Error</th>
                                            <th class="actionColumn">
                                                Action
                                            </th>
                                        </tr>
                                        </thead>
                                        <tbody id="verifiedErrorList">

                                        </tbody>

                                    </table>

                                    <div id="verifiedPager">
                                        <ul id="verifiedErrorPagination" class="pagination-sm"></ul>
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

        var errorListDisplayArea = jq('#error-list');

        var numberOfErrorRecords = ${ patientPendingListSize };

        var errorRecords = ${ patientPendingList };

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

        // apply pagination for verification errors
         var verificationErrorPaginationDiv = jq('#verifiedErrorPagination');
         var verificationQueuePaginationDiv = jq('#verificationQueuePagination');
         var verificationErrorListDisplayArea = jq('#verifiedErrorList');

        var numberOfVerificationErrorRecords = ${ numberOfVerificationErrorSize };
        var verificationErrorRecords = ${ patientVerifiedWithErrorsList };

         var verifiedErrorDataDisplayRecords = [];

         var verifiedErrorRecPerPage = 10;
         var verifiedErrorStartPage = 1;
         var verificationTotalErrorPages = Math.ceil(numberOfVerificationErrorRecords / verifiedErrorRecPerPage);

         var visibleVerificationErrorPages = 1;
         var visibleQueuePages = 1;

         var payloadEditor = {};

          if (verificationTotalErrorPages <= 5) {
              visibleVerificationErrorPages = verificationTotalErrorPages;
                } else {
              visibleVerificationErrorPages = 5;
               }

            if (numberOfVerificationErrorRecords > 0) {
                apply_pagination(verificationErrorPaginationDiv, verificationErrorListDisplayArea, verificationTotalErrorPages, visibleVerificationErrorPages, verificationErrorRecords, verifiedErrorDataDisplayRecords, 'error', verifiedErrorStartPage); // records in verification error
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

        jq(document).on('click','.verifyButton',function(){
            ui.navigate('kenyaemr', 'registration/editPatient', { patientId: jq(this).val(),  returnUrl: location.href });
        });

        // handle click event of the fetch IPRS verification errors
       jq("#pullVerificationErrors").click( function() {
            //Run the fetch task
           jQuery("#pull-msgBox").hide();
            console.log('Starting the fetch task!');
           // show spinner
           display_loading_validate_identifier(true);
           jQuery.getJSON('${ ui.actionLink("kenyaemr", "nupi/nupiDataExchange", "pullVerificationErrorsFromCR")}')
               .success(function (data) {
                    if(data.success === "true") {
                       // Hide spinner
                       display_loading_validate_identifier(false);
                       jQuery("#pull-msgBox").text("Successfully pulled error records");
                       jQuery("#pull-msgBox").show();
                   }else{
                       display_loading_validate_identifier(false);
                       jQuery("#pull-msgBox").text("Error pulling verification errors: Check your network");
                       jQuery("#pull-msgBox").show();
                   }
               })
               .fail(function (err) {
                       // Hide spinner
                       console.log("Error fetching verification errors: " + JSON.stringify(err));
                   // Hide spinner
                   display_loading_validate_identifier(false);
                   jQuery("#pull-msgBox").text("Could not pull error records");
                   jQuery("#pull-msgBox").show();

                   }
               )
        });

    });

    function generate_table(displayRecords, displayObject, tableId) {
        var tr;
        displayObject.html('');
        for (var i = 0; i < displayRecords.length; i++) {

            tr = jq('<tr/>');
            tr.append("<td>" + displayRecords[i].givenName + "</td>");

            tr.append("<td>" + displayRecords[i].middleName + "</td>");
            tr.append("<td>" + displayRecords[i].familyName + "</td>");
            tr.append("<td>" + displayRecords[i].gender + "</td>");
            tr.append("<td>" + displayRecords[i].birthdate + "</td>");
            tr.append("<td>" + displayRecords[i].error + "</td>");

            var actionTd = jq('<td/>');

            var btnView = jq('<button/>', {
                text: 'Verify client',
                class: 'verifyButton',
                value: displayRecords[i].id
            });

            actionTd.append(btnView);

            tr.append(actionTd);
            displayObject.append(tr);
        }
    }

    function generate_verification_errors_table(displayRecords, displayObject, tableId) {
        var tr;
        displayObject.html('');
        for (var i = 0; i < displayRecords.length; i++) {

            tr = jq('<tr/>');
            tr.append("<td>" + displayRecords[i].givenName + "</td>");

            tr.append("<td>" + displayRecords[i].middleName + "</td>");
            tr.append("<td>" + displayRecords[i].familyName + "</td>");
            tr.append("<td>" + displayRecords[i].gender + "</td>");
            tr.append("<td>" + displayRecords[i].birthdate + "</td>");
            tr.append("<td>" + displayRecords[i].error + "</td>");

            var actionTd = jq('<td/>');

            var btnView = jq('<button/>', {
                text: 'Verify client',
                class: 'verifyButton',
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