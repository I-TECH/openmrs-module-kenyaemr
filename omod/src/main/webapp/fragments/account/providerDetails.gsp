<div class="ke-panel-frame">
	<div class="ke-panel-heading">Provider Details</div>

	<% if (provider && !provider.retired) { %>
	<div class="ke-panel-content">
		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Provider ID", value: provider.identifier ]) }
	</div>
	<% } %>

	<div class="ke-panel-footer">
	<% if (provider && !provider.retired) { %>

		<%= ui.includeFragment("kenyaui", "widget/dialogForm", [
				buttonConfig: [
						label: "Edit",
						iconProvider: "kenyaui",
						icon: "glyphs/edit.png"
				],
				dialogConfig: [ heading: "Edit Provider account for ${ kenyaui.formatPersonName(person) }", width: 50, height: 30 ],
				fragmentProvider: "kenyaemr",
				fragment: "account/providerDetails",
				action: "submit",
				prefix: "provider",
				commandObject: form,
				properties: [ "identifier" ],
				extraFields: [
						[ hiddenInputName: "personId", value: person.id ],
						[ hiddenInputName: "provider.providerId", value: provider.id ]
				],
				submitLabel: "Save Changes",
				cancelLabel: "Cancel",
				onSuccessCallback: "ui.reloadPage();"
		]) %>

	<% } else { %>

		<%= ui.includeFragment("kenyaui", "widget/dialogForm", [
				buttonConfig: [
						label: "Make this person a provider",
						iconProvider: "kenyaui",
						icon: "buttons/provider_${ person.gender == "F" ? 'f' : 'm' }.png"
				],
				dialogConfig: [ heading: "New Provider account for ${ kenyaui.formatPersonName(person) }", width: 50, height: 30 ],
				fragmentProvider: "kenyaemr",
				fragment: "account/providerDetails",
				action: "submit",
				prefix: "provider",
				commandObject: form,
				properties: [ "identifier" ],
				extraFields: [
						[ hiddenInputName: "personId", value: person.id ]
				],
				submitLabel: "Save Changes",
				cancelLabel: "Cancel",
				onSuccessCallback: "ui.reloadPage();"
		]) %>

	<% } %>
	</div>
</div>