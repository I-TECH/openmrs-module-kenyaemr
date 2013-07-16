<%
	// Help dialog content - loaded into a modal dialog

	def helpBaseUrl = "../../help/"

	def resourceFiles = [
			"How to Create a Patient Record": "K_JobAid_1_CreatePt_13.2.pdf",
			"How to Search for a Patient Record": "K_JobAid_2_SearchPt_13.2.pdf",
			"How to Record a Patient's Family History": "K_JobAid_3_FamilyHistory_13.2.pdf",
			"How to Record a Patient's Obstetric History": "K_JobAid_4_ObsHistory_13.2.pdf",
			"How to Enroll a Patient in the HIV Program": "K_JobAid_5_HIVEnroll_13.2.pdf",
			"How to Enter Data from a Clinical Encounter": "K_JobAid_6_Encounter_13.2.pdf",
			"How to Complete a Clinical Encounter - HIV Addendum Form": "K_JobAid_7_HIVEncounter_13.2.pdf",
			"How to Enroll a Patient in the TB Program": "K_JobAid_8_TBEnroll_13.2.pdf",
			"How to Enter Patient Data From a Filled MOH 257": "K_JobAid_10_RE_13.2.pdf",
			"How to Record Starting a Patient on an ARV Regimen": "K_JobAid_11_StartARV_13.2.pdf",
			"How to Record Changes to a Patient's Current ARV Regimen": "K_JobAid_12_ChangeARV_13.2.pdf",
			"How to Record Stops in ART": "K_JobAid_13_StopARV_13.2.pdf",
			"Where to Go From the Main Menu": "K_JobAid_14_MainMenu_13.2.pdf"
	]

	def splitMap = { map ->
		def flip = true, left = [:], right = [:]
		map.each { key, value ->
			if (flip) {
				left.put(key, value)
			}
			else {
				right.put(key, value)
			}
			flip = !flip
		}
		[ left, right ]
	}

	def resourceMaps = splitMap(resourceFiles)
%>
If you are experiencing a problem you should contact your clinic's IT admin for support.
You may also find the following resources helpful <em>(all links open in new windows)</em>:
<br />

<table>
	<tr>
		<% resourceMaps.each { map -> %>
		<td valign="top">
			<ul>
				<% map.each { title, file -> %>
				<li><a href="${ helpBaseUrl + file }">${ title }</a></li>
				<% } %>
			</ul>
		</td>
		<% } %>
	</tr>
</table>

If those do not resolve your problem, then you can submit a support ticket. To do so:<br />
<br />

<div style="width:450px; margin:0 auto; text-align: center; background-color: #e8e7e2; padding: 10px; border-radius: 4px">
	<img src="${ ui.resourceLink("kenyaui", "images/glyphs/phone.png") }" style="vertical-align: text-bottom" /> Call the help desk for free at <strong>${ supportNumber }</strong><br />
	or<br />
	<img src="${ ui.resourceLink("kenyaui", "images/glyphs/email.png") }" style="vertical-align: text-bottom" /> Email <a href="mailto:${ supportEmail }">${ supportEmail }</a>
	<br />
	<br />
	Please include your facility code which is <strong>${ facilityCode }</strong>
</div>
<br />
<div style="text-align: center">
	${ ui.includeFragment("kenyaui", "widget/button", [ label: "Close", onClick: "kenyaui.closeDialog();" ]) }
</div>