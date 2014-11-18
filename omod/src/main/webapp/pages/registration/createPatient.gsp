<%
	ui.decorateWith("kenyaemr", "standardPage", [ layout: "sidebar" ])

	ui.includeJavascript("kenyaemr", "controllers/account.js")

	def menuItems = [
			[ label: "Back to home", iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back to home", href: ui.pageLink("kenyaemr", "registration/registrationHome") ]
	]
%>

<div class="ke-page-sidebar">
	${ ui.includeFragment("kenyaui", "widget/panelMenu", [ heading: "Create Patient", items: menuItems ]) }

	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Help</div>
		<div class="ke-panel-content">
			<div id="new">
				<h3><u>Register new Patient</u></h3>
				Use this option if you are registering the patient for the first time in the system.

			</div>


			<div id="existing">
				<h3><u>Register Existing User to Patient</u></h3>
			If the registrant has worked at this facility then you should search to see if they already exist as a
			person in the EMR and create the patient record from that.
			</div>
		</div>
	</div>
</div>

<div class="ke-page-content">

	<script type="text/javascript">

		function ke_useNewPerson() {
			ui.navigate('kenyaemr', 'registration/createPatient2');
		}

		function ke_useExistingPerson() {
			ui.navigate('kenyaemr', 'registration/createPatient1');
		}

	</script>
	<div class="ke-panel-frame">
	<div class="ke-panel-heading">Step 1 :Select an Option</div>
	<div class="ke-panel-controls" style="overflow: auto">
		<table style="width: 100%">
			<tr>
				<td style="width: 50%; text-align: left; vertical-align: middle">
					<button type="button" onclick="ke_useNewPerson()">
						<img src="${ ui.resourceLink("kenyaui", "images/buttons/account_add.png") }" /> Register new Patient
					</button>
				</td>
				<td style="width: 50%; text-align: right; vertical-align: middle">
					<button type="button" onclick="ke_useExistingPerson()">
						<img src="${ ui.resourceLink("kenyaui", "images/buttons/account_add.png") }" /> Register Existing User to Patient
					</button>
				</td>

			</tr>
		</table>
	</div>
	</div>
</div>