<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage")
%>

<% if (supportsExcel) { %>
	<div style="float: right">
		${ ui.includeFragment("uilibrary", "widget/button", [
				iconProvider: "uilibrary",
				icon: "save_download_32.png",
				href: ui.pageLink("kenyaemr", "reportsRunPatientAlertListReport", [
							manager: manager.class.name,
							mode: excel
						])
			]) }
	</div>
<% } %>

${ ui.includeFragment("kenyaemr", "showReportOutput", [ definition: definition, data: data ]) }