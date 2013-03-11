<%
	ui.decorateWith("kenyaui", "panel", [ heading: config.heading ])

	// config supports "page", which will set up a clickFunction, that will have patientId=... appended

	def clickFunction = null
	if (config.page) {
		clickFunction = """function () {
				location.href = ui.pageLink('kenyaemr', '${ config.page }', { patientId: jq(this).find('input[name=patientId]').val() });
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
			return patient.personName + ' <input type="hidden" name="patientId" value="' + patient.patientId + '"/>';
		},
		icon: function(patient) {
			return '<img width="32" height="32" src="' + ui.resourceLink('kenyaemr', 'images/patient_' + patient.gender.toLowerCase() + '.png') + '"/>';
		},
		leftDetails: function(patient) {
			var str = patient.birthdateEstimated ? '~' : '';
			str += (patient.age < 1) ? (patient.ageMonths + ' month(s), ' + patient.ageDays + ' day(s)') : (patient.age + ' year(s)');
			str += ' <small>(' + (patient.birthdateEstimated ? "approx " : "") + patient.birthdate + ')</small>';
			return str;
		},
		center: function(patient) {
			var tmp = "";
			for (var i = 0; i < patient.activeIdentifiers.length; ++i) {
				tmp += '<span class="identifier-label">' + patient.activeIdentifiers[i].identifierType + ':</span><br/>';
				tmp += '<span class="identifier-value">' + patient.activeIdentifiers[i].identifier + '</span>';
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

<%= ui.includeFragment("kenyaemr", "widget/stack", config.merge([ itemFormatter: "formatPatientAsStackItem", clickFunction: clickFunction ])) %>