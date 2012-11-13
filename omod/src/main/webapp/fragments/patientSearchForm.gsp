<%
	def defaultWhich = config.defaultWhich ?: "checked-in"
%>

<%= ui.includeFragment("uilibrary", "widget/form", [
	id: "patientSearch",
	fields: [
		[ label: "Which patients", fragment: "widget/radioButtons", formFieldName: "which",
			selected: defaultWhich,
			separator: "&nbsp;&nbsp;",
			options: [
				[ label: "All", value: "all" ],
				[ label: "Only Checked In", value: "checked-in" ]
			],
			onChange: """function() { publish('patientSearch/changed'); }"""
		],
		[ label: "ID or Name", formFieldName: "q", class: java.lang.String ],
		[ label: "Age", formFieldName: "age", class: java.lang.Integer ]
	],
	fragment: "patientSearch",
	fragmentProvider: "kenyaemr",
	action: "search",
	submitOnEvent: "patientSearch/changed",
	resetOnSubmit: false,
	successEvent: "patientSearch/results"
]) %>