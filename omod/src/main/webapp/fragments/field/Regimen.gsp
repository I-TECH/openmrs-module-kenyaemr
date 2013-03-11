<%
	ui.includeJavascript("kenyaui", "coreFragments.js")

	def units = [ "mg", "g", "ml", "tab" ]

	def frequencies = [
			OD: "Once daily",
			NOCTE: "Once daily, at bedtime",
			qPM: "Once daily, in the evening",
			qAM: "Once daily, in the morning",
			BD: "Twice daily",
			TDS: "Thrice daily"
	]

	def refDefIndex = 0;

	def groupOptions = {
		it.regimens.collect( { reg -> """<option value="${ refDefIndex++ }">${ reg.name }</option>""" } ).join()
	}

	def drugOptions = drugs.collect( { """<option value="${ it }">${ kenyaUi.formatDrug(it, ui) }</option>""" } ).join()
	def unitsOptions = units.collect( { """<option value="${ it }">${ it }</option>""" } ).join()
	def frequencyOptions = frequencies.collect( { """<option value="${ it.key }">${ it.value }</option>""" } ).join()
%>
<script type="text/javascript">
	// Create variable to hold regimen presets
	if (typeof regimen_presets === 'undefined') {
		regimen_presets = new Array();
	}
	regimen_presets['${ config.id }'] = ${ ui.toJson(regimenDefinitions) };

	jq(function() {
		jq('#${ config.id }-container .standard-regimen-select').change(function () {
			// Get selected regimen definition
			var stdRegIndex = parseInt(jq(this).val());
			var stdReg = regimen_presets['${ config.id }'][stdRegIndex];
			var components = stdReg.components;

			// Get container div and component fields
			var container = jq(this).parent();
			var drugFields = container.find('.regimen-component-drug');
			var doseFields = container.find('.regimen-component-dose');
			var unitsFields = container.find('.regimen-component-units');
			var frequencyFields = container.find('.regimen-component-frequency');

			// Clear all inputs
			container.find('input, select').val('');

			// Set component controls for each component of selected regimen
			for (var c = 0; c < components.length; c++) {
				var component = components[c];
				jq(drugFields[c]).val(component.drugRef);
				jq(doseFields[c]).val(component.dose);
				jq(unitsFields[c]).val(component.units);
				jq(frequencyFields[c]).val(component.frequency);
			}

			kenyaemr.updateRegimenFromDisplay('${ config.id }');
		});

		jq('#${ config.id }-container .regimen-component-drug, #${ config.id }-container .regimen-component-dose, #${ config.id }-container .regimen-component-units, #${ config.id }-container .regimen-component-frequency').blur(function() {
			kenyaemr.updateRegimenFromDisplay('${ config.id }');
		});
	});
</script>

<div id="${ config.id }-container">
	<input type="hidden" id="${ config.id }" name="${ config.formFieldName }" />
	<i>Use standard:</i> <select class="standard-regimen-select">
		<option label="Select..." value="" />
		<% regimenGroups.each { group -> %>
			<optgroup label="${ group.name }">${ groupOptions(group) }</optgroup>
		<% } %>
	</select><br />
	<br />
	<span id="${ config.id }-error" class="error" style="display: none"></span>
	<% for (def c = 0; c < maxComponents; ++c) { %>
	<div class="regimen-component">
		Drug: <select class="regimen-component-drug"><option value="" />${ drugOptions }</select>
		Dosage: <input class="regimen-component-dose" type="text" size="5" /><select class="regimen-component-units">${ unitsOptions }</select>
		Frequency: <select class="regimen-component-frequency">${ frequencyOptions }</select>
	</div>
	<% } %>
</div>

<% if (config.parentFormId) { %>
<script type="text/javascript">
	jq(function() {
		subscribe('${ config.parentFormId }.reset', function() {
			jq('#${ config.id } input, #${ config.id } select').val('');
		});

		subscribe('${ config.parentFormId }.clear-errors', function() {
			jq('#${ config.id }-error').html("").hide();
		});

		subscribe('${ config.parentFormId }/${ config.formFieldName }.show-errors', function(message, payload) {
			FieldUtils.showErrorList('${ config.id }-error', payload);
		});

		jq('#${ config.id }').change(function() {
			publish('${ config.parentFormId }/changed');
		});
	});
</script>
<% } %>