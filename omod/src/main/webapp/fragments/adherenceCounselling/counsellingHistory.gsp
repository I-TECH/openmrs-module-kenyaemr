<%
    ui.decorateWith("kenyaui", "panel", [ heading: "Adherence Counselling History" ])

    def onEncounterClick = { encounter ->
        """kenyaemr.openEncounterDialog('${currentApp.id}', ${encounter.id});"""
    }
    def encounters = {
            ui.includeFragment("kenyaemr", "widget/encounterStack", [encounters: encDetails.encounter, onEncounterClick: onEncounterClick])

    }
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

    <fieldset>
        <legend>Adherence Counselling History</legend>
        <%if (encDetails.encounter) { %>
        <table class="simple-table">

        <tr>
            <th align="left">Date</th>
            <th align="left">Session Number</th>
            <th align="left">Form</th>

         </tr>
            <% encDetails.each {  %>
          <tr>
            <td>${it.encDate}</td>
            <td>${it.sessionNum}</td>
            <td>
                ${ui.includeFragment("kenyaemr", "widget/encounterStack", [encounters: enhancedAdherenceEncounters, onEncounterClick: onEncounterClick])}
            </td>
            </tr>
        <% } %>

        </table>
        <% } else {%>
        <div>No counselling history</div>

        <% } %>
    </fieldset>

</div>
