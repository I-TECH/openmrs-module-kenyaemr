<%
	ui.decorateWith("kenyaui", "panel", [ heading: config.heading, frameOnly: true ])

	// config supports "page", which will set up a clickFunction, that will have patientId=... appended

	def clickFunction = null
	if (config.page) {
		clickFunction = """function () {
				location.href = ui.pageLink('${ config.pageProvider }', '${ config.page }', { patientId: jq(this).find('input[name=patientId]').val() });
			}"""
	}

	config.numResultsFormatter = """function(results) { return results.length + (results.length > 1 ? ' patients' : ' patient'); }"""
%>
<script type="text/javascript">
	var patientItemOpts = {
		title: function(patient) {
			return patient.name + ' <input type="hidden" name="patientId" value="' + patient.id + '"/>';
		},
		icon: function(patient) {
			return '<img width="32" height="32" src="' + ui.resourceLink('kenyaui', 'images/buttons/patient_' + patient.gender.toLowerCase() + '.png') + '"/>';
		},
		leftDetails: function(patient) {
			return patient.age + ' <small>(DOB ' + patient.birthdate + ')</small>';
		},
		center: function(patient) {
			var tmp = "";
			for (var i = 0; i < patient.identifiers.length; ++i) {
				tmp += '<span class="ke-identifier-type">' + patient.identifiers[i].identifierType + '</span><br/>';
				tmp += '<span class="ke-identifier-value">' + patient.identifiers[i].identifier + '</span>';
				tmp += '<br/>';
			}
			return tmp;
		},
		right: function(patient) {
			return typeof patient.extra !== 'undefined' ? patient.extra : '';
		}
	};
	
	function formatPatientAsStackItem(patient) {
		return kenyaemr.threeColumnStackItemFormatter(patient, patientItemOpts);
	}
</script>

<%= ui.includeFragment("kenyaui", "widget/searchResults", config.merge([ itemFormatter: "formatPatientAsStackItem", clickFunction: clickFunction ])) %>