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
    <table class="simple-table">
        <tr>
            <th>Date</th>
            <th>Encounter Type</th>
            <th>Population Type</th>
            <th>Strategy</th>
            <th>Entry Point</th>
            <th>Final Result</th>
            <th>Client Linked</th>
        </tr>
        <tr>
            <td>2019-03-01</td>
            <td>Initial</td>
            <td>General</td>
            <td>Facility</td>
            <td>OPD</td>
            <td>Negative</td>
            <td></td>
        </tr>
        <tr>
            <td>2018-12-01</td>
            <td>Initial</td>
            <td>General</td>
            <td>Facility</td>
            <td>OPD</td>
            <td>Negative</td>
            <td></td>
        </tr>
    </table>

</div>
