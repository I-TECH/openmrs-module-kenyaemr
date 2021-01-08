<%
    ui.decorateWith("kenyaui", "panel", [ heading: "mUzima Queue Summary" ])
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
</style>


<div>

    <fieldset>
        <legend>mUzima Error Queue</legend>
        <div>
            <table class="simple-table">
                <thead>
                <th>Error Type</th>
                <th>Total Errors</th>
                <th></th>
                </thead>
                <tbody>
                <tr>
                    <td>Potential Registration duplicates</td>
                    <td>${registrationErrors}</td>
                    <td><button onclick="ui.navigate('/' + OPENMRS_CONTEXT_PATH + '/module/muzimacore/view.list#/duplicates')">View Errors</button> </td>
                </tr>
                <tr>
                    <td>Total Errors</td>
                    <td>${totalErrors}</td>
                    <td><button onclick="ui.navigate('/' + OPENMRS_CONTEXT_PATH + '/module/muzimacore/view.list#/errors')">View Errors</button> </td>
                </tr>
                </tbody>
            </table>
        </div>
    </fieldset>

    <br/>
    <fieldset>
        <legend>mUzima Queue Data</legend>
        <div>Total records in queue: ${queueData}</div>
    </fieldset>


</div>
