<%
    ui.decorateWith("kenyaemr", "standardPage")
%>
<div class="ke-page-sidebar">
    ${ ui.includeFragment("kenyaui", "widget/panelMenu", [
            heading: "BackUp and Restore",
            items: [

                    [ iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back to home", href: ui.pageLink("kenyaemr", "admin/adminHome") ]
            ]
    ]) }


</div>

<div class="ke-page-content">
    ${ ui.includeFragment("kenyaemr", "system/backupSummary") }
    ${ ui.includeFragment("kenyaemr", "system/backupRestore",[ mysqlpassword: mysqlpassword])}
</div>