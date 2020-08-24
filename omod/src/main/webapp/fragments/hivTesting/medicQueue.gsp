<%
    ui.decorateWith("kenyaui", "panel", [ heading: "Medic Queue Summary" ])
%>
<script type="text/javascript">
    jq = jQuery;

    jq(function() {
        jq("#showStatus").hide();
        jq('#refresh').click(function() {
            jq("#msgSpan").text("Refreshing Error Messages");
            jq("#showStatus").show();
            jq("#msg").text("");

            jq("#refresh").prop("disabled", true);
            jq("#errorMessages").prop("disabled", true);
            jq.getJSON('${ ui.actionLink("refreshTables") }')
                .success(function(data) {
                    jq("#showStatus").hide();
                    jq("#msg").text("Error messages refreshed successfully");
                    jq("#refresh").prop("disabled", false);
                    jq("#errorMessages").prop("disabled", false);
                    for (index in data) {
                        jq('#log_table > tbody > tr').remove();
                        var tbody = jq('#log_table > tbody');
                        for (index in data) {

                            var item = data[index];
                            var row = '<tr>';
                            row += '<td width="20%">' + item.id + '</td>';
                            row += '<td width="20%">' + item.message + '</td>';
                            row += '<td width="20%">' + item.date_created + '</td>';
                            row += '</tr>';
                            tbody.append(row);
                        }
                    }
                })
                .error(function(xhr, status, err) {
                    jq("#showStatus").hide();
                    jq("#msg").text("There was an error refreshing Error messages");
                    jq("#refresh").prop("disabled", false);
                    jq("#errorMessages").prop("disabled", false);
                    alert('AJAX error ' + err);
                })
        });
        jq('#errorMessages').click(function() {
            jq("#errorMessages").attr("disabled", false);
            jq("#msgSpan").text("Refreshing error Messages");
            jq("#showStatus").show();
            jq("#msg").text("");
            jq("#refresh").prop("disabled", true);
            jq("#errorMessages").prop("disabled", true);
            jq.getJSON('${ ui.actionLink("errorMessages") }')
                .success(function(data) {
                    jq("#showStatus").hide();
                    jq("#msg").text("Error messages refreshed successfully");
                    jq("#refresh").prop("disabled", false);
                    jq("#errorMessages").prop("disabled", false);
                    for (index in data) {
                        jq('#log_table > tbody > tr').remove();
                        var tbody = jq('#log_table > tbody');
                        for (index in data) {

                            var item = data[index];
                            var row = '<tr>';
                            row += '<td width="20%">' + item.id + '</td>';
                            row += '<td width="20%">' + item.message + '</td>';
                            row += '<td width="20%">' + item.date_created + '</td>';
                            row += '</tr>';
                            tbody.append(row);
                        }
                    }
                })
                .error(function(xhr, status, err) {
                    jq("#showStatus").hide();
                    jq("#msg").text("There was an error refreshing Error messages");
                    jq("#refresh").prop("disabled", false);
                    jq("#errorMessages").prop("disabled", false);
                    alert('AJAX error ' + err);
                })
        });
    });
</script>
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
</style>


<div>

    <fieldset>
        <legend>Medic Error Queue</legend>
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
                </tr>
                <tr>
                    <td>Total Errors</td>
                    <td>${totalErrors}</td>
                </tr>
                </tbody>
            </table>
        </div>
        <hr>
        <div>

            <button id="refresh">
                <img src="${ ui.resourceLink("kenyaui", "images/glyphs/ok.png") }" /> Refresh error messages
            </button>

            <br/>
            <br/>
        </div>
        <br/>
        <div id="showStatus">
            <span id="msgSpan"></span> &nbsp;&nbsp;<img src="${ ui.resourceLink("kenyaui", "images/loader_small.gif") }"/>
        </div>
        <div id="msg"></div>
        <div>
            <h3>History of Error Messages (Last 5 entries)</h3>
            <table id="log_table">
                <thead>
                <tr>
                    <th>Message ID</th>
                    <th>Error message</th>
                    <th>Date Created</th>
                </tr>
                </thead>
                <tbody class='scrollable'>
                <% if (logs) { %>
                <% logs.each { log -> %>
                <tr>
                    <td>${ log.id }</td>
                    <td>${ log.message }</td>
                    <td>${ log.date_created }</td>
                </tr>
                <% } %>
                <% } else { %>
                <tr>
                    <td colspan="4">No record found. Please refresh for details</td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>

    </fieldset>

    <br/>
    <fieldset>
    <legend>Medic Queue Data</legend>
        <div>Total records in queue: ${queueData}</div>
</fieldset>


</div>
