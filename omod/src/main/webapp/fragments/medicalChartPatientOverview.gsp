<%
	ui.decorateWith("kenyaemr", "panel", [ heading: "Overview" ])

	config.require("patient")

	def cs = context.conceptService
	def conceptList = [
		cs.getConceptByUuid(MetadataConstants.WEIGHT_KG_CONCEPT_UUID),
		cs.getConceptByUuid(MetadataConstants.CD4_CONCEPT_UUID),
		cs.getConceptByUuid(MetadataConstants.CD4_PERCENT_CONCEPT_UUID)
	]
%>

<table width="100%" border="0">
	<tr>
		<td width="50%" valign="top">
			${ ui.includeFragment("kenyaemr", "obsTableByDate", [ id: "tblhistory", patient: patient, concepts: conceptList ]) }
		</td>
		<td width="50%" valign="top">
			${ ui.includeFragment("kenyaemr", "obsGraphByDate", [ id: "cd4graph", patient: patient, concepts: conceptList, showUnits: true, style: "height: 300px" ]) }
		</td>
	</tr>
</table>
