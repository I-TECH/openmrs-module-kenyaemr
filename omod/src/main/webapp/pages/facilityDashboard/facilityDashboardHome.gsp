<%
	ui.decorateWith("kenyaemr", "standardPage", [layout: "sidebar" ])
    def menuItems = [
            [ label: "Back to home", iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back to home", href: ui.pageLink("kenyaemr", "userHome") ]
    ]

%>
<%
    ui.includeCss("kenyaemr", "referenceapplication.css", 100)
%>
<style>
.alignLeft {
    text-align: left;
}
</style>
<script type="text/javascript" src="../../moduleResources/kenyaemr/scripts/highcharts.js"></script>
<script>
    jQuery(function () {
        jQuery('#container').highcharts({
            chart: {
                type: 'column'
            },
            title: {
                text: 'Facility Statistics'
            },
            subtitle: {
                text: 'Click the columns to view data.'
            },
            xAxis: {
                type: 'category'
            },
            yAxis: {
                title: {
                    text: 'Total Number of Patients'
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
                        format: '{point.y:.1f}'
                    }
                }
            },

            tooltip: {
                headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
                pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>'
            },

            series: [{
                name: 'Brands',
                colorByPoint: true,
                data: [{

                    name:'Total Patients' ,
                    y:${allPatients} ,

                }, {
                    name: 'Current in Care',
                    y: ${inCare},

                }, {
                    name: 'Current on ART',
                    y: ${onArt},

                }, {
                    name: 'New on ART',
                    y: ${newOnArt},

                }, {
                    name: 'Valid VL <12 Months',
                    y: ${vlResults},

                }, {
                    name: 'Total Suppressed',
                    y: ${suppressedVl},
                }]
            }],
        });
    });
    jQuery(function () {
        jQuery('#hts-container').highcharts({
            title: {
                text: 'Viral Load Trend',
                x: -20 //center
            },
            subtitle: {
                text: 'VL cp/ml',
                x: -20
            },
            xAxis: {
                categories: ['Jan', 'Mar', 'May', 'Jul', 'Sep', 'Nov', 'Dec']
            },
            yAxis: {
                title: {
                    text: 'Viral Load cp/ml'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
                valueSuffix: 'cp/ml'
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'middle',
                borderWidth: 0
            },
            series: [{
                name: 'VL',
                data: [200, 300, 500, 1000, 750, 500, 400]
            }, {
                name: 'Threshold',
                data: [1000, 1000, 1000, 1000, 1000, 1000, 1000]
            }]
        });
    });
</script>

<div class="ke-page-sidebar">
	<div class="ke-panel-frame">
        ${ ui.includeFragment("kenyaui", "widget/panelMenu", [ heading: "Navigation", items: menuItems ]) }    </div>
</div>

<div class="ke-page-content">
    <div style="font-size: 18px; color: #006056; font-style: normal; font-weight: bold">Facility Dashboard</div>
    <table cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td style="width: 50%; vertical-align: top">
                <div class="ke-panel-frame">
                    <div class="ke-panel-heading">Summary of Care and Treatment Statistics</div>
                    <div class="ke-panel-content">
                        <table class="alignLeft">
                            <tr>
                                <td colspan="3" class="heading2"><strong>Reporting Period: ${reportPeriod} </strong></td>
                            </tr>
                            <tr>
                                <th>Total Patients</th>
                                <th>Current in Care</th>
                                <th>Current on ART</th>
                                <th>New on ART</th>
                                <th>Total with Valid viral loads <br/>(in last 12 months)</th>
                                <th>Total suppressed </th>
                            </tr>
                            <tr>
                                <td>${allPatients}</td>
                                <td>${inCare}</td>
                                <td>${onArt}</td>
                                <td>${newOnArt}</td>
                                <td>${vlResults}</td>
                                <td>${suppressedVl}</td>
                            </tr>
                        </table>
                    </div>
                </div>
                <div id="container" style="min-width: 450px; height: 300px; margin: 0 auto"></div>
            </td>
            <td style="width: 50%; vertical-align: top; padding-left: 5px">
                <div class="ke-panel-frame">
                    <div class="ke-panel-heading">Summary of HTS Statistics</div>
                    <div class="ke-panel-content">
                        <table class="alignLeft">
                            <tr>
                                <td colspan="3" class="heading2"><strong>Reporting Period: Today</strong></td>
                            </tr>
                            <tr>
                                <th>&nbsp;</th>
                                <th>Total Tested</th>
                                <th>Total Positive</th>
                                <th>Total Enrolled </th>
                            </tr>
                            <tr>
                                <td><b>Total Contacts</b></td>
                                <td>0</td>
                                <td>0</td>
                                <td>0</td>
                            </tr>
                            <tr>
                                <td><b>Family Members</b></td>
                                <td>0</td>
                                <td>0</td>
                                <td>0</td>
                            </tr>
                            <tr>
                                <td><b>Sexual Partner</b></td>
                                <td>0</td>
                                <td>0</td>
                                <td>0</td>
                            </tr>
                        </table>
                    </div>
                </div>
                <div id="hts-container" style="min-width: 450px; height: 300px; margin: 0 auto"></div>
            </td>
        </tr>
    </table>
    <br/>
    <br/>
    <br/>

</div>