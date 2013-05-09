<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Overview" ])

	config.require("patient")

	def conceptList = [ dictionary.WEIGHT_KG, dictionary.CD4_COUNT, dictionary.CD4_PERCENT ]
%>

<table width="100%" border="0">
	<tr>
		<td width="50%" valign="top">
			${ ui.includeFragment("kenyaui", "widget/obsHistoryTable", [ id: "tblhistory", patient: patient, concepts: conceptList ]) }
		</td>
		<td width="50%" valign="top">
			${ ui.includeFragment("kenyaui", "widget/obsHistoryGraph", [ id: "cd4graph", patient: patient, concepts: conceptList, showUnits: true, style: "height: 300px" ]) }
		</td>
	</tr>
</table>
