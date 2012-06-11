<%
	// config supports "page", which will set up a clickFunction, that will have patientId=... appended

	def clickFunction = null
	if (config.page) {
		clickFunction = """function() {
				location.href = pageLink("${ config.page }", { patientId: jq(this).find('input[name=patientId]').val() });
			}"""
	}

	if (!config.numResultsFormatter) {
		config.numResultsFormatter = """function(listOfItems) { return listOfItems.length + " patient(s)"; }""" 
	}

	// supports showNumResults (default false)
	// supports numResultsSuffix (default "")

	// supports noneMessage (default "general.none", only takes effect if showNumResults is false)
	def noneMessage = config.noneMessage ?: "general.none"
	if (config.showNumResults)
		noneMessage = null
%>
<script>
	var patientPanelOpts = {
		title: function(patient) {
			return patient.personName + ' <input type="hidden" name="patientId" value="' + patient.patientId + '"/>';
		},
		icon: function(patient) {
			return '<img width="32" height="32" src="' + resourceLink('uilibrary', 'images/patient_' + patient.gender + '.gif') + '"/>';
		},
		leftDetails: function(patient) {
			return patient.age + ' yrs (' + patient.birthdate + ')';
		},
		center: function(patient) {
			var tmp = "";
			for (var i = 0; i < patient.activeIdentifiers.length; ++i) {
				tmp += '<span class="identifier-label">' + patient.activeIdentifiers[i].identifierType + ':</span>';
				tmp += '<span class="identifier-value">' + patient.activeIdentifiers[i].identifier + '</span>';
				tmp += '<br/>';
			}
			return tmp;
		},
		right: function(patient) {
			return typeof patient.extra !== 'undefined' ? patient.extra : '';
		}
	};
	
	function formatPatientAsPanel(patient) {
		return kenyaemr.panelFormatter(patient, patientPanelOpts);
	}
</script>

<style>
	.identifier-label {
		font-color: #888888;
		font-size: 0.8em;
	}	
	.identifier-value {
		font-weight: bold;
	}
</style>

<%= ui.includeFragment("widget/panelList", config.merge([
		itemFormatter: "formatPatientAsPanel",
		clickFunction: clickFunction
	])) %>