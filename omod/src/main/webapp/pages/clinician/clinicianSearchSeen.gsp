<%
    ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

    ui.includeJavascript("kenyaemr", "controllers/visit.js")

%>

<style type="text/css">
#calendar {
    text-align: center;
}
#calendar .ui-widget-content {
    border: 0;
    background: inherit;
    padding: 0;
    margin: 0 auto;
}
</style>

<script type="text/javascript">
    jQuery(function() {
        jQuery('#calendar').datepicker({
            dateFormat: 'yy-mm-dd',
            defaultDate: '${ kenyaui.formatDateParam(seenDate) }',
            gotoCurrent: true,
            maxDate: new Date,
            onSelect: function(dateText) {
                ui.navigate('kenyaemr', 'clinician/clinicianSearchSeen', { seenDate: dateText });
            }
        });
    });
</script>
<div class="ke-page-sidebar">
    ${ ui.decorate("kenyaui", "panel", [ heading: "Select Day to View Seen Patients" ], """<div id="calendar"></div>""") }
</div>
<div class="ke-page-content">
    ${ ui.includeFragment("kenyaemr", "patient/dailySeen", [ pageProvider: "kenyaemr", page: "clinician/clinicianHome", date: seenDate ]) }
</div>



