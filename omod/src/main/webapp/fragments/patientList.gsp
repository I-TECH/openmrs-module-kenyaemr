<%
	config.require("page") // (will go the that page, with a patientId parameter)
	// supports showNumResults (default false)

	// supports noneMessage (default "general.none", only takes effect if showNumResults is false)
	def noneMessage = config.noneMessage ?: "general.none"
	if (config.showNumResults)
		noneMessage = null
%>
<script>
	function panelItem(data, item) {
		if (item == null)
			return "";
		else if (typeof item == 'function')
			return item(data);
		else
			return item;
	}

	function panelHelper(data, values) {
		var ret = '<div class="panel">';
		ret += '<table width="100%"><tr valign="top"><td>';
		ret += '<span class="title">' + panelItem(data, values.title) + '</span>';
		ret += '<span class="icon">' + panelItem(data, values.icon) + '</span>';
		ret += '<span class="leftDetails">' + panelItem(data, values.leftDetails) + '</span>';
		ret += '</td><td align="center">';
		ret += panelItem(data, values.center);
		ret += '</td><td align="right">';
		ret += panelItem(data, values.right);
		ret += '</td></tr></table>';
		ret += '</div>';
		return ret;
	}
	
	var patientFormatter = {
		span: function(patient) {
			return '<span class="patient"><img src="' + resourceLink('uilibrary', 'images/user_16.png') + '"/>' + patient.personName + '</span>';
		},
		panel: function(patient) {
			return panelHelper(patient, {
				title: patient.personName + ' <input type="hidden" name="patientId" value="' + patient.patientId + '"/>',
				icon: '<img width="32" height="32" src="' + resourceLink('uilibrary', 'images/patient_' + patient.gender + '.gif') + '"/>',
				leftDetails: patient.age + ' yrs (' + patient.birthdate + ')',
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
			});
		}
	}
</script>

<style>
	.patient {
		border: 1px gray solid;
	}

	.identifier-label {
		font-color: #888888;
		font-size: 0.8em;
	}	
	.identifier-value {
		font-weight: bold;
	}
	
	.panel:nth-child(odd) {
		background-color: #e0e0e0;
	}	

	.panel {
		cursor: pointer;
		border: 1px #a0a0a0 solid;
		margin-bottom: 0.5em;
	}
	.panel > table td, .panel > table th {
		padding: 0.5em;
	}
	.panel .title {
		float: left;
		font-weight: bold;
		font-size: 1.2em;
	}
	.panel .icon {
		float: left;
		clear: left;
	}
	.panel .leftDetails {
		float: left;
	}
</style>

<div id="${ config.id }">
	<div class="num-results"></div>
	<% if (noneMessage) { %>
		<div class="no-results">${ ui.message(noneMessage) }</div>
	<% } %>
	<div class="results"></div>
</div>

<script>
subscribe("${ config.id }/show", function(event, data) {
	<% if (config.showNumResults) { %>
		jq('#${ config.id } > .num-results').html(typeof data.length === 'number' ? (data.length + ' patient(s)') : "");
	<% } %>
	<% if (noneMessage) { %>
		if (data.length == 0) {
			jq('#${ config.id } > .no-results').show();
		} else {
			jq('#${ config.id } > .no-results').hide();
		}
	<% } %>
		
	jq('#${ config.id } > .results').html('');
	
	for (var i = 0; i < data.length; ++i) {
		var html = patientFormatter.panel(data[i]);
		jq(html)
			.appendTo(jq('#${ config.id } > .results'))
			.click(function() {
				location.href = pageLink("${ config.page }", { patientId: jq(this).find('input[name=patientId]').val() }); 
			});
	}	
});
</script>