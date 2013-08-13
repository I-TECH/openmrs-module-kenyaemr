<%
	ui.decorateWith("kenyaui", "panel", [ heading: config.heading ])

	// config supports "page", which will set up a clickFunction, that will have patientId=... appended

	def clickFunction = null
	if (config.page) {
		clickFunction = """function () {
				location.href = ui.pageLink('${ config.pageProvider }', '${ config.page }', { patientId: jq(this).find('input[name=patientId]').val() });
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
<script type="text/javascript">
	var patientItemOpts = {
		title: function(patient) {
			return patient.name + ' <input type="hidden" name="patientId" value="' + patient.id + '"/>';
		},
		icon: function(patient) {
			return '<img width="32" height="32" src="' + ui.resourceLink('kenyaui', 'images/patient_' + patient.gender.toLowerCase() + '.png') + '"/>';
		},
		leftDetails: function(patient) {
			return patient.age + ' <small>(DOB ' + patient.birthdate + ')</small>';
		},
		center: function(patient) {
			var tmp = "";
			for (var i = 0; i < patient.identifiers.length; ++i) {
				tmp += '<span class="ke-identifier-type">' + patient.identifiers[i].identifierType + ':</span><br/>';
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

<%= ui.includeFragment("kenyaui", "widget/stack", config.merge([ itemFormatter: "formatPatientAsStackItem", clickFunction: clickFunction ])) %>