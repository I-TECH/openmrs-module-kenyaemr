<%
    ui.decorateWith("kenyaui", "panel", [ heading: "HIV Testing History" ])
%>
<style>
.simple-table {
    border: solid 1px #DDEEEE;
    border-collapse: collapse;
    border-spacing: 0;
    font: normal 13px Arial, sans-serif;
}
.simple-table thead th {
    background-color: #DDEFEF;
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
    <% if (linkageDetails) { %>
    <fieldset>
        <legend>Linkage Information</legend>
        <table class="simple-table">
            <tr>
            <th align="left">Date</th>
            <th align="left">Facility Linked to</th>
            <th align="left">Unique Patient Number</th>
            </tr>
            <tr>
                <td>${linkageDetails.encDate}</td>
                <td>${linkageDetails.facilityLinkedTo}</td>
                <td>${linkageDetails.upn}</td>
            </tr>
        </table>
    </fieldset>
    <% } %>

    <br/>
    <fieldset>
    <legend>Test History</legend>
    <%if (encounters) { %>
    <table class="simple-table">

        <tr>
            <th align="left">Date</th>
            <th align="left">Population Type</th>
            <th align="left">Strategy</th>
            <th align="left">Entry Point</th>
            <th align="left">Final Result</th>
        </tr>
        <% encounters.each { %>
        <tr>
            <td>${it.encDate}</td>
            <td>${it.popType}</td>
            <td>${it.htsStrategy}</td>
            <td>${it.entryPoint}</td>
            <td>${it.finalResult}</td>
        </tr>
        <% } %>
    </table>
    <% } else {%>
        <div>No history found</div>

    <% } %>
</fieldset>


</div>
