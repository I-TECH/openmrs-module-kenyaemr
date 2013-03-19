<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Overview" ])

	config.require("patient")

	def conceptList = [ MetadataConstants.WEIGHT_KG_CONCEPT_UUID, MetadataConstants.CD4_CONCEPT_UUID, MetadataConstants.CD4_PERCENT_CONCEPT_UUID ]
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
