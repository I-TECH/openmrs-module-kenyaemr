<div class="ke-panel-frame" style="text-align: right">
	<% if (visit) {
		if (config.allowCheckOut) {
	%>
		${ ui.includeFragment("kenyaui", "widget/popupForm", [
				id: "check-out-form",
				buttonConfig: [
						label: "End Visit",
						extra: "Patient going home",
						classes: [ "padded" ],
						iconProvider: "kenyaui",
						icon: "buttons/visit_end.png"
				],
				popupTitle: "Check Out",
				fields: [
						[ hiddenInputName: "visit.visitId", value: visit.visitId ],
						[ label: "End Date and Time", formFieldName: "visit.stopDatetime", class: java.util.Date, initialValue: new Date(), showTime: true ]
				],
				fragment: "registrationUtil",
				fragmentProvider: "kenyaemr",
				action: "editVisit",
				successCallbacks: [ "location.href = '" + ui.pageLink("kenyaemr", "registration/registrationViewPatient", [ patientId: patient.id ]) + "'" ],
				submitLabel: ui.message("general.submit"),
				cancelLabel: ui.message("general.cancel"),
				submitLoadingMessage: "Checking Out"
		]) }
	<%
		} else {
	%>
		${ ui.includeFragment("kenyaui", "widget/button", [
				iconProvider: "kenyaui",
				icon: "buttons/registration.png",
				label: "Go to Registration",
				classes: [ "padded" ],
				extra: "to Check Out",
				href: ui.pageLink("kenyaemr", "registration/registrationViewPatient", [ patientId: patient.id ])
		]) }
	<%
		}
	} else {
		if (config.allowCheckIn) {
			def jsSuccess = "location.href = ui.pageLink('kenyaemr', 'registration/registrationViewPatient', " + "{" + "patientId: ${ patient.id } });"
	%>
	<%= ui.includeFragment("kenyaui", "widget/popupForm", [
			id: "check-in-form",
			buttonConfig: [
					iconProvider: "kenyaui",
					icon: "buttons/registration.png",
					label: "Check In For Visit",
					classes: [ "padded" ],
					extra: "Patient is Here"
			],
			popupTitle: "Check In For Visit",
			prefix: "visit",
			commandObject: newCurrentVisit,
			hiddenProperties: [ "patient" ],
			properties: [ "visitType", "startDatetime" ],
			propConfig: [
					"visitType": [ type: "radio" ],
			],
			fieldConfig: [
					"visitType": [ label: "Visit Type" ],
					"startDatetime": [ showTime: true ]
			],
			fragment: "registrationUtil",
			fragmentProvider: "kenyaemr",
			action: "startVisit",
			successCallbacks: [ jsSuccess ],
			submitLabel: ui.message("general.submit"),
			cancelLabel: ui.message("general.cancel"),
			submitLoadingMessage: "Checking In"
	]) %>
	<% 	} else { %>
		${ ui.includeFragment("kenyaui", "widget/button", [
				iconProvider: "kenyaui",
				icon: "buttons/registration.png",
				label: "Go to Registration",
				classes: [ "padded" ],
				extra: "to Check In",
				href: ui.pageLink("kenyaemr", "registration/registrationViewPatient", [ patientId: patient.id ])
		]) }
	<%
		}
	} %>
</div>