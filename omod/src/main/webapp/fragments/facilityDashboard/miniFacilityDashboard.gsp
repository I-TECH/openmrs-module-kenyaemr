<%
    ui.includeCss("kenyaemr", "referenceapplication.css", 100)
    ui.includeJavascript("kenyaemr", "highcharts.js")
    ui.includeJavascript("kenyaemr", "highcharts-grouped-categories.js")
%>
<style>
.alignLeft {
    text-align: left;
}
</style>
<script>
    jQuery(function () {
        jQuery('#care_and_treatment_chart').highcharts({
            chart: {
                type: 'column'
            },
            title: {
                text: ''
            },
            subtitle: {
                text: ''
            },
            xAxis: {
                type: 'category'
            },
            yAxis: {
                title: {
                    text: 'Number of Patients'
                }

            },
            legend: {
                enabled: false
            },
            plotOptions: {
                series: {
                    borderWidth: 0,
                    dataLabels: {
                        enabled: true,
                        format: '{point.y:.0f}'
                    }
                }
            },

            tooltip: {
                headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
                pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.0f}</b><br/>'
            },

            series: [{
                name: 'Statistics',
                colorByPoint: true,
                data: [{

                    name: 'Total Patients',
                    y:${allPatients},

                }, {
                    name: 'Total enrolled in HIV',
                    y: ${cumulativeEnrolledInHiv},

                }, {
                    name: 'Current in Care',
                    y: ${inCare},

                }, {
                    name: 'Current on ART',
                    y: ${onArt},

                }, {
                    name: 'Newly Enrolled',
                    y: ${newlyEnrolledInHiv},

                }, {
                    name: 'New on ART',
                    y: ${newOnArt},
                }]
            }],
        });
    });

    jQuery(function () {
        jQuery('#viral_load_tracker').highcharts({
            chart: {
                type: 'column'
            },
            title: {
                text: ''
            },
            subtitle: {
                text: ''
            },
            xAxis: {
                type: 'category'
            },
            yAxis: {
                title: {
                    text: 'Number of Patients'
                }

            },
            legend: {
                enabled: false
            },
            plotOptions: {
                series: {
                    borderWidth: 0,
                    dataLabels: {
                        enabled: true,
                        format: '{point.y:.0f}'
                    }
                }
            },

            tooltip: {
                headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
                pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.0f}</b><br/>'
            },

            series: [{
                name: 'Statistics',
                colorByPoint: true,
                data: [{

                    name: 'Total clients with viral loads',
                    y:${vlResults},

                }, {
                    name: 'Total Unsuppressed',
                    y: ${vlResults - suppressedVl},

                }, {
                    name: 'Total Suppressed',
                    y: ${suppressedVl},

                }]
            }],
        });
    });






</script>

<div class="ke-page-content">
    <div style="font-size: 18px; color: #006056; font-style: normal; font-weight: bold">Facility Dashboard
    <% if (dataToolUrl) { %>
        <span id="datatoolUrl"> | <a href="${dataToolUrl}" target="_blank">Data tool</a></span>
    <% } %>
    </div>

    <div id="program-tabs" class="ke-tabs">
        <div class="ke-tabmenu">
            <div class="ke-tabmenu-item" data-tabid="care_and_treatment">Care and Treatment</div>
            <div class="ke-tabmenu-item" data-tabid="appointments">Appointments</div>
        </div>

        <div class="ke-tab" data-tabid="care_and_treatment">
            <table cellspacing="0" cellpadding="0" width="100%">
                <tr>
                    <td style="width: 50%; vertical-align: top">
                        <div class="ke-panel-frame">
                            <div class="ke-panel-heading">Summary of Care and Treatment Statistics</div>

                            <div class="ke-panel-content">
                                <table class="alignLeft">
                                    <tr>
                                        <td colspan="3"
                                            class="heading2"><strong>Reporting Period: ${reportPeriod}</strong>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>Total Patients</th>
                                        <th>Total enrolled in HIV</th>
                                        <th>Current in Care</th>
                                        <th>Current on ART</th>
                                        <th>Newly Enrolled</th>
                                        <th>New on ART</th>
                                    </tr>
                                    <tr>
                                        <td>${allPatients}</td>
                                        <td>${cumulativeEnrolledInHiv}</td>
                                        <td>${inCare}</td>
                                        <td>${onArt}</td>
                                        <td>${newlyEnrolledInHiv}</td>
                                        <td>${newOnArt}</td>
                                    </tr>
                                </table>
                            </div>
                        </div>

                        <div id="care_and_treatment_chart"
                             style="min-width: 450px; height: 300px; margin: 0 auto"></div>
                    </td>
                    <td style="width: 50%; vertical-align: top; padding-left: 5px">
                        <div class="ke-panel-frame">
                            <div class="ke-panel-heading">Viral Load Tracker</div>

                            <div class="ke-panel-content">
                                <table class="alignLeft">
                                    <tr>
                                        <td colspan="3" class="heading2"><strong>Reporting Period: Today</strong></td>
                                    </tr>
                                    <tr>
                                        <th>Total clients with viral loads <br/>(in last 12 months)</th>
                                        <th>Total Unsuppressed</th>
                                        <th>Total Suppressed</th>
                                    </tr>
                                    <tr>
                                        <td>${vlResults}</td>
                                        <td>${vlResults - suppressedVl}</td>
                                        <td>${suppressedVl}</td>
                                    </tr>
                                </table>
                            </div>
                        </div>

                        <div id="viral_load_tracker" style="min-width: 450px; height: 300px; margin: 0 auto"></div>
                    </td>
                </tr>
            </table>
        </div>


        <div class="ke-tab" data-tabid="appointments">
            <table cellspacing="0" cellpadding="0" width="100%">
                <tr>
                    <td style="width: 50%; vertical-align: top">
                        <div class="ke-panel-frame">
                            <div class="ke-panel-heading">Today's Workload</div>

                            <div class="ke-panel-content">
                                <table class="alignLeft">
                                    <tr>
                                        <td colspan="3"
                                            class="heading2"><strong>Date: Today</strong>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>Number Scheduled</th>
                                        <th>Number Checked In</th>
                                        <th>Number Seen</th>
                                        <th>Unscheduled Visits</th>

                                    </tr>
                                    <tr>
                                        <td>${patientsScheduled}</td>
                                        <td>${checkedIn}</td>
                                        <td>${patientsSeen}</td>
                                        <td>${unscheduled}</td>
                                    </tr>
                                </table>
                            </div>
                        </div>

                    </td>

                </tr>
            </table>
        </div>
        <br/>
        <br/>
    </div>

</div>
