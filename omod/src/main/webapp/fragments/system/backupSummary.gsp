<%
    ui.decorateWith("kenyaui", "panel", [ heading: "Backup Summary" ])

    ui.includeJavascript("kenyaemr", "controllers/system.js")
%>
<div ng-controller="BackupSummary" ng-init="init()">
    <table class="ke-table-vertical">
        <tbody>
        <tr ng-repeat="info in infos">
            <td>{{ info.label }}</td>
            <td>{{ info.value }}</td>
        </tr>
        </tbody>
    </table>
</div>

