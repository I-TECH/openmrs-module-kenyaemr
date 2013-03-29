<% config.require("person") %>
${ ui.includeFragment("kenyaemr", "personAge", config) }
<span style="font-size: 0.8em;">
	(<% if (config.person.birthdateEstimated) { %>approx <% } %>DOB ${ kenyaUi.formatDate(config.person.birthdate) })
</span>