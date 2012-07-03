<%
	ui.decorateWith("standardKenyaEmrPage")
%>

<br/>

<% reports.each { %>

	${ ui.includeFragment("widget/button", [
		iconProvider: "uilibrary",
		icon: "chart_32.png",
		label: it.name, 
		href: ui.pageLink("reportsRunMonthlyIndicatorReport", [ manager: it.uuid ])
	]) }
	<br/>

<% } %>