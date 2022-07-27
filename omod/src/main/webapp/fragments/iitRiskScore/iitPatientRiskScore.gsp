<%
    ui.decorateWith("kenyaui", "panel", [ heading: "IIT Risk Score", frameOnly: true ])
%>
<div class="ke-panel-content">
<table>
    <tr>
        <tr>
    <td> Risk Score </td> <td> ${riskScore} </td>
        </tr>
        <tr>
            <td>Evaluation Date </td><td>${evaluationDate}</td>
        </tr>
        <tr>
            <td>Description </td><td>${description}</td>
       </tr>
    <tr>
        <td>Risk Factors </td><td>${riskFactor}</td>
    </tr>
</table>
</div>



</div>
