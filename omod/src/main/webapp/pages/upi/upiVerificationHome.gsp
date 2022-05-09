<%
    ui.decorateWith("kenyaemr", "standardPage", [layout: "sidebar"])
    ui.includeJavascript("kenyaemrorderentry", "jquery.twbsPagination.min.js")

    def menuItems = [
            [label: "Back", iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back", href: ui.pageLink("kenyaemr", "userHome")]
    ]
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
.manifest-status {
    font-weight: bold;font-size: 14px;
}
.collect-new-sample {
    color: darkred;
    font-style: italic;
}
.missing-physical-sample {
    color: firebrick;
    font-style: italic;
}
.require-manual-updates {
    color: orangered;
    font-style: italic;
}
.result-not-ready {
    font-style: italic;
}
.viewButton {
    background-color: cadetblue;
    color: white;
}
.editButton {
    background-color: cadetblue;
    color: white;
}
.viewButton:hover {
    background-color: steelblue;
    color: white;
}
.editButton:hover {
    background-color: steelblue;
    color: white;
}
.page-content{
    background: #eee;
    display: inline-block;
    padding: 10px;
    max-width: 660px;
    font-weight: bold;
}
</style>

<div class="ke-page-sidebar">
    ${ui.includeFragment("kenyaui", "widget/panelMenu", [heading: "Back", items: menuItems])}
</div>

<div class="ke-page-content">
    <div align="left">

        <h2>Unique Identifiers</h2>
        <div>
            <button type="button"
                    onclick="ui.navigate('${ ui.pageLink("kenyaemrorderentry", "manifest/createManifest", [ returnUrl: ui.thisUrl() ])}')">
                <img src="${ui.resourceLink("kenyaui", "images/buttons/report_queue.png")}"/>
                Sync Identifiers
            </button>
        </div>
        <br/>
        <br/>
        <div id="page-content" class="page-content">Page 1</div>

        <table class="simple-table">
            <thead>
            <tr>
                <th>Name</th>
                <th>National ID Number</th>
                <th>Birth Certificate Number</th>
                <th>Passport Number</th>
                <th>Huduma Number</th>
                <th>Alien ID Number</th>
                <th>Driving License Number</th>
                <th>UPI</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody id="manifest-list">

            </tbody>
        </table>

        <div id="pager">
            <ul id="pagination" class="pagination-sm"></ul>
        </div>

    </div>

</div>

<script type="text/javascript">

    //On ready
    jq = jQuery;
    jq(function () {
        jq('#generateManifest').click(function () {
            jq.getJSON('${ ui.actionLink("kenyaemrorderentry", "patientdashboard/generalLabOrders", "generateViralLoadPayload") }')
                .success(function (data) {
                    jq('#msgBox').html("Successfully generated payload");
                })
                .error(function (xhr, status, err) {
                    jq('#msgBox').html("Could not generate payload for lab");
                })
        });


        var pagination = jq('#pagination');
        var displayRecords = [];
        var recPerPage = 10;
        var page = 1;
       // var totalPages = Math.ceil(totalRecords / recPerPage);
        var visiblePages = 1;
        if (totalPages <= 5) {
            visiblePages = totalPages;
        } else {
            visiblePages = 5;
        }

        apply_pagination();

        function apply_pagination() {
            pagination.twbsPagination({
                totalPages: totalPages,
                visiblePages: visiblePages,
                onPageClick: function (event, page) {
                    displayRecordsIndex = Math.max(page - 1, 0) * recPerPage;
                    endRec = (displayRecordsIndex) + recPerPage;
                    jq('#page-content').text('Page ' + page);
                    displayRecords = records.slice(displayRecordsIndex, endRec);
                    generate_table(displayRecords);
                }
            });
        }

        jq(document).on('click','.viewButton',function(){
            ui.navigate('kenyaemrorderentry', 'orders/manifestOrdersHome', { manifest: jq(this).val(),  returnUrl: location.href });
        });

        jq(document).on('click','.editButton',function(){
            ui.navigate('kenyaemrorderentry', 'manifest/createManifest', { manifestId: jq(this).val(),  returnUrl: location.href });
        });
    });

    function generate_table(displayRecords) {
        var tr;
        jq('#manifest-list').html('');
        for (var i = 0; i < displayRecords.length; i++) {
            var manifestID = displayRecords[i].manifest.id;
            tr = jq('<tr/>');
            tr.append("<td>" + displayRecords[i].manifest.startDate + "</td>");
            tr.append("<td>" + displayRecords[i].manifest.endDate + "</td>");
            tr.append("<td>" + displayRecords[i].manifest.courier + "</td>");
            tr.append("<td>" + displayRecords[i].manifest.county + "/" + displayRecords[i].manifest.subCounty + "</td>");
            tr.append("<td>" + displayRecords[i].manifest.facilityEmail + "</td>");
            tr.append("<td>" + displayRecords[i].manifest.facilityPhoneContact + "</td>");
            tr.append("<td>" + displayRecords[i].manifest.clinicianName + "</td>");
            tr.append("<td>" + displayRecords[i].manifest.clinicianPhoneContact + "</td>");
            tr.append("<td>" + displayRecords[i].manifest.labPocPhoneNumber + "</td>");
            var tdStatus = jq('<td/>');
            var spanStatus = jq('<span/>', {
                class: 'manifest-status',
                text: displayRecords[i].manifest.status
            });
            tdStatus.append(spanStatus);

            var spanTotalSamples = jq('<span/>', {
                class: 'result-not-ready',
                text: 'Total samples: ' + displayRecords[i].totalSamples
            });
            tdStatus.append(jq('<br/>'));
            tdStatus.append(spanTotalSamples);

            // add alert for new sample
            if (displayRecords[i].collectNewSample > 0) {
                var spanSampleRequired = jq('<span/>', {
                    class: 'collect-new-sample',
                    text: 'Collect new sample: ' + displayRecords[i].collectNewSample
                });
                tdStatus.append(jq('<br/>'));
                tdStatus.append(spanSampleRequired);
            }

            // add alert for Missing physical sample
            if (displayRecords[i].missingPhysicalSample > 0) {
                var spanMissingSample = jq('<span/>', {
                    class: 'missing-physical-sample',
                    text: 'Missing physical sample: ' + displayRecords[i].missingPhysicalSample
                });
                tdStatus.append(jq('<br/>'));
                tdStatus.append(spanMissingSample);
            }

            // samples missing in the lab system - not sure how this status is arrived at though
            if (displayRecords[i].recordsNotFound > 0) {
                var spanRecordsNotFound = jq('<span/>', {
                    class: 'missing-physical-sample',
                    text: 'Records missing in the Lab: ' + displayRecords[i].recordsNotFound
                });
                tdStatus.append(jq('<br/>'));
                tdStatus.append(spanRecordsNotFound);
            }
            // add alert for Manual updates required
            if (displayRecords[i].manualUpdates > 0) {
                var spanManualUpdates = jq('<span/>', {
                    class: 'require-manual-updates',
                    text: 'Manual updates required: ' + displayRecords[i].manualUpdates
                });
                tdStatus.append(jq('<br/>'));
                tdStatus.append(spanManualUpdates);
            }
            // add alert for new sample
            if (displayRecords[i].incompleteSample > 0) {
                var spanIncompleteResults = jq('<span/>', {
                    class: 'result-not-ready',
                    text: 'Result not ready: ' + displayRecords[i].incompleteSample
                });
                tdStatus.append(jq('<br/>'));
                tdStatus.append(spanIncompleteResults);
            }
            tr.append(tdStatus);

            tr.append("<td>" + displayRecords[i].manifest.dispatchDate + "</td>");
            var actionTd = jq('<td/>');

            var btnView = jq('<button/>', {
                text: 'View',
                class: 'viewButton',
                value: displayRecords[i].manifest.id
            });
            actionTd.append(btnView);
            tr.append(actionTd);
            if (displayRecords[i].manifest.status == "Draft") {
                var btnEdit = jq('<button/>', {
                    text: 'Edit',
                    class: 'editButton',
                    value: displayRecords[i].manifest.id
                });
                actionTd.append(btnEdit);
                tr.append(actionTd);
            }
            jq('#manifest-list').append(tr);
        }
    }

</script>