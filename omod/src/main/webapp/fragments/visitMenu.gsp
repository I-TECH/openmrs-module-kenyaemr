<div class="ke-panelbar" style="text-align: right">
	<% if (visit) { %>
	<%= ui.includeFragment("kenyaui", "widget/dialogForm", [
			buttonConfig: [ label: "Patient Summary", iconProvider: "kenyaui", icon: "buttons/visit_end.png" ],
			dialogConfig: [ heading: "Patient Summary", width: 50, height: 30 ],
			fields: [
					[ hiddenInputName: "visitId", value: visit.visitId ],
					[ hiddenInputName: "appId", value: currentApp.id ],
					[ label: "End Date and Time", formFieldName: "stopDatetime", class: java.util.Date, initialValue: new Date(), showTime: true ]
			],
			fragmentProvider: "kenyaemr",
			fragment: "registrationUtil",
			action: "stopVisit",
			onSuccessCallback: "ui.reloadPage()",
			submitLabel: ui.message("general.submit"),
			cancelLabel: ui.message("general.cancel")
	]) %>
	<%= ui.includeFragment("kenyaui", "widget/dialogForm", [
			buttonConfig: [ label: "Visit Summary", iconProvider: "kenyaui", icon: "buttons/visit_end.png" ],
			dialogConfig: [ heading: "Visit Summary", width: 50, height: 30 ],
			fields: [
					[ hiddenInputName: "visitId", value: visit.visitId ],
					[ hiddenInputName: "appId", value: currentApp.id ],
					[ label: "End Date and Time", formFieldName: "stopDatetime", class: java.util.Date, initialValue: new Date(), showTime: true ]
			],
			fragmentProvider: "kenyaemr",
			fragment: "registrationUtil",
			action: "stopVisit",
			onSuccessCallback: "ui.reloadPage()",
			submitLabel: ui.message("general.submit"),
			cancelLabel: ui.message("general.cancel")
	]) %>
	<%= ui.includeFragment("kenyaui", "widget/dialogForm", [
			buttonConfig: [ label: "Check out of visit", iconProvider: "kenyaui", icon: "buttons/visit_end.png" ],
			dialogConfig: [ heading: "Check Out", width: 50, height: 30 ],
			fields: [
						[ hiddenInputName: "visitId", value: visit.visitId ],
						[ hiddenInputName: "appId", value: currentApp.id ],
						[ label: "End Date and Time", formFieldName: "stopDatetime", class: java.util.Date, initialValue: new Date(), showTime: true ]
			],
			fragmentProvider: "kenyaemr",
			fragment: "registrationUtil",
			action: "stopVisit",
			onSuccessCallback: "ui.reloadPage()",
			submitLabel: ui.message("general.submit"),
			cancelLabel: ui.message("general.cancel")
	]) %>
	<% } else if (!patient.dead && !patient.voided) { %>
	<%= ui.includeFragment("kenyaui", "widget/dialogForm", [
			buttonConfig: [ label: "Patient Summary", iconProvider: "kenyaui", icon: "buttons/visit_end.png" ],
			dialogConfig: [ heading: "Patient Summary", width: 50, height: 30 ],
			fields: [
					[ hiddenInputName: "appId", value: currentApp.id ],
					[ label: "End Date and Time", formFieldName: "stopDatetime", class: java.util.Date, initialValue: new Date(), showTime: true ]
			],
			fragmentProvider: "kenyaemr",
			fragment: "registrationUtil",
			action: "stopVisit",
			onSuccessCallback: "ui.reloadPage()",
			submitLabel: ui.message("general.submit"),
			cancelLabel: ui.message("general.cancel")
	]) %>
	<%= ui.includeFragment("kenyaui", "widget/dialogForm", [
			buttonConfig: [ label: "Check in for visit", iconProvider: "kenyaui", icon: "buttons/registration.png" ],
			dialogConfig: [ heading: "Check In", width: 50, height: 30 ],
			prefix: "visit",
			commandObject: newCurrentVisit,
			hiddenProperties: [ "patient" ],
			properties: [ "visitType", "startDatetime" ],
			extraFields: [
					[ hiddenInputName: "appId", value: currentApp.id ]
			],
			propConfig: [
					"visitType": [ type: "radio" ],
			],
			fieldConfig: [
					"visitType": [ label: "Visit Type" ],
					"startDatetime": [ showTime: true ]
			],
			fragmentProvider: "kenyaemr",
			fragment: "registrationUtil",
			action: "startVisit",
			onSuccessCallback: "ui.reloadPage()",
			submitLabel: ui.message("general.submit"),
			cancelLabel: ui.message("general.cancel")
	]) %>
	<% } %>
</div>