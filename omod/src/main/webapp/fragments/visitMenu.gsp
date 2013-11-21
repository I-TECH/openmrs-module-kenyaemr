<div class="ke-panelbar" style="text-align: right">
	<% if (visit) {
		if (config.allowCheckOut) {
	%>
		${ ui.includeFragment("kenyaui", "widget/dialogForm", [
				buttonConfig: [
						label: "End Visit",
						extra: "Patient going home",
						classes: [ "padded" ],
						iconProvider: "kenyaui",
						icon: "buttons/visit_end.png"
				],
				dialogConfig: [ heading: "Check Out", width: 50, height: 30 ],
				fields: [
						[ hiddenInputName: "visit.visitId", value: visit.visitId ],
						[ label: "End Date and Time", formFieldName: "visit.stopDatetime", class: java.util.Date, initialValue: new Date(), showTime: true ]
				],
				fragment: "registrationUtil",
				fragmentProvider: "kenyaemr",
				action: "editVisit",
				onSuccessCallback: "ui.navigate('" + ui.pageLink("kenyaemr", "registration/registrationViewPatient", [ patientId: patient.id ]) + "')",
				submitLabel: ui.message("general.submit"),
				cancelLabel: ui.message("general.cancel")
		]) }
	<%
		} else {
	%>
		${ ui.includeFragment("kenyaui", "widget/button", [
				iconProvider: "kenyaui",
				icon: "buttons/registration.png",
				label: "Go to Registration",
				extra: "to Check Out",
				href: ui.pageLink("kenyaemr", "registration/registrationViewPatient", [ patientId: patient.id ])
		]) }
	<%
		}
	} else {
		if (config.allowCheckIn) {
			def jsSuccess = "ui.navigate('kenyaemr', 'registration/registrationViewPatient', " + "{" + "patientId: ${ patient.id } });"
	%>
	<%= ui.includeFragment("kenyaui", "widget/dialogForm", [
			buttonConfig: [
					iconProvider: "kenyaui",
					icon: "buttons/registration.png",
					label: "Check In For Visit",
					classes: [ "padded" ],
					extra: "Patient is Here"
			],
			dialogConfig: [ heading: "Check In For Visit", width: 50, height: 30 ],
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
			onSuccessCallback: jsSuccess,
			submitLabel: ui.message("general.submit"),
			cancelLabel: ui.message("general.cancel")
	]) %>
	<% 	} else { %>
		${ ui.includeFragment("kenyaui", "widget/button", [
				iconProvider: "kenyaui",
				icon: "buttons/registration.png",
				label: "Go to Registration",
				extra: "to Check In",
				href: ui.pageLink("kenyaemr", "registration/registrationViewPatient", [ patientId: patient.id ])
		]) }
	<%
		}
	} %>
</div>