<% dataPoints.each { dataPoint -> %>
${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: dataPoint.key, value: dataPoint.value, showDateInterval: true ]) }
<% } %>