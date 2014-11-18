<% programs.each { descriptor -> %>
${ ui.includeFragment("kenyaemr", "program/programHistory", [ patient: patient, program: descriptor.target, showClinicalData: showClinicalData ]) }
<% } %>