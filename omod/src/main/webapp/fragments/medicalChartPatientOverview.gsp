<%
	ui.decorateWith("kenyaemr", "panel", [ heading: "Overview" ])

	def cs = context.conceptService
	def conceptList = [
		cs.getConceptByUuid(MetadataConstants.WEIGHT_KG_CONCEPT_UUID),
		cs.getConceptByUuid(MetadataConstants.CD4_CONCEPT_UUID),
		cs.getConceptByUuid(MetadataConstants.CD4_PERCENT_CONCEPT_UUID)
	]
%>

<div style="float: left; width: 49%">
	${ ui.includeFragment("kenyaemr", "obsTableByDate", [ id: "tblhistory", concepts: conceptList ]) }
</div>
<div style="float: right; width: 49%">
	${ ui.includeFragment("kenyaemr", "obsGraphByDate", [ id: "cd4graph", concepts: conceptList, showUnits: true, style: "height: 300px" ]) }
</div>
