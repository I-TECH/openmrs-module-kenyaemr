<%
	ui.decorateWith("standardKenyaEmrPage")
%>

<% if (supportsExcel) { %>
	<div style="float: right">
		${ ui.includeFragment("widget/button", [
				iconProvider: "uilibrary",
				icon: "save_download_32.png",
				href: ui.pageLink("reportsRunPatientSummaryReport", [
							manager: manager.class.name,
							mode: excel
						])
			]) }
	</div>
<% } %>

${ ui.includeFragment("showReportOutput", [ definition: definition, data: data ]) }