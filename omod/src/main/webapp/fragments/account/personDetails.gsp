<div class="ke-panel-frame">
	<div class="ke-panel-heading">Person Details</div>
	<div class="ke-panel-content">
		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Real name", value: kenyaui.formatPersonName(person) ]) }
		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Gender", value: kenyaui.formatPersonGender(person) ]) }
		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Telephone", value: ui.format(wrapped.telephoneContact) ]) }
	</div>
	<div class="ke-panel-footer">
		${ ui.includeFragment("kenyaui", "widget/dialogForm", [
				id: "person-details-form",
				buttonConfig: [
						label: "Edit",
						iconProvider: "kenyaui",
						icon: "glyphs/edit.png"
				],
				dialogConfig: [ heading: "Edit person details for " + kenyaui.formatPersonName(person) ],
				fragment: "account/personDetails",
				fragmentProvider: "kenyaemr",
				action: "submit",
				prefix: "person",
				commandObject: form,
				hiddenProperties: [ "personId" ],
				properties: [ "personName.givenName", "personName.familyName", "gender", "telephone" ],
				propConfig: [
						"gender": [
								options: [
										[ label: "Female", value: "F" ],
										[ label: "Male", value: "M" ]
								]
						]
				],
				submitLabel: "Save Changes",
				cancelLabel: "Cancel",
				onSuccessCallback: "ui.reloadPage();"
		]) }
	</div>
</div>