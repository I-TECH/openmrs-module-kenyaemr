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

    <% if(episodes) { %>
        <% episodes.each { %>
            <fieldset>
                <legend>Started on ${ it.key }</legend>
                <% if(it.value) { %>
                    <table class="simple-table">

                        <tr>
                            <th align="left" width="15%">Date</th>
                            <th align="left" width="15%">Session Number</th>
                            <th align="left">Form</th>

                        </tr>
                    <% it.value.reverse().each { %>
                        <tr>
                            <td width="15%">${kenyaui.formatDate(it.encDate)}</td>
                            <td width="15%">${it.sessionNum}</td>
                            <td>
                                ${ui.includeFragment("kenyaemr", "widget/encounterStack", [encounters: it.encounter, onEncounterClick: onEncounterClick])}
                            </td>
                        </tr>
                    <% } %>
                    </table>
                <% } %>
            </fieldset>
        <% } %>
    <% } %>


</div>
