<% config.require("person") %>
${ ui.includeFragment("kenyaemr", "kenyaemrPersonAge", config) }
<span style="font-size: 0.8em;">
	(<% if (config.person.birthdateEstimated) { %>approx <% } %>${ ui.format(config.person.birthdate) })
</span>