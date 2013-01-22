<%
	ui.decorateWith("kenyaemr", "standardKenyaEmrPage", [ layout: "sidebar" ])

	def menuItems =  []
	if (data) {
		menuItems << [ iconProvider: "kenyaemr", icon: "buttons/report_configure.png", label: "Change Parameters", href: ui.pageLink("kenyaemr", "reportsRunMonthlyIndicatorReport", [ manager: manager.class ]) ]
	}
	menuItems << [ iconProvider: "kenyaemr", icon: "buttons/back.png", label: "Back to Reports", href: ui.pageLink("kenyaemr", "reportsHome") ]
%>

<div id="content-side">
	${ ui.includeFragment("kenyaemr", "widget/panelMenu", [ heading: "Report", items: menuItems ]) }
</div>

<div id="content-main">

<% if (data) { %>
	${ ui.includeFragment("kenyaemr", "reportOutput", [ definition: definition, data: data ]) }
<% } else { %>

	<div class="panel-frame">
		<div class="panel-heading">${ definition.name }</div>
		<div class="panel-content">

			<form method="POST">
				Period:
				${ ui.includeFragment("uilibrary", "widget/selectList", [
						formFieldName: "startDate",
						options: startDateOptions,
						optionsValueField: "key",
						optionsDisplayField: "value"
					]) }
				<br/>
				<br/>
				${ ui.includeFragment("uilibrary", "widget/radioButtons", [
						formFieldName: "mode",
						options: [
							[ value: "view", label: "View online" ],
							[ value: "excel", label: "Download as Excel" ]
						],
						selected: "view"
					]) }
				<br/>
				<br/>
				<input type="submit" class="button" value="Generate Report" />
			</form>

		</div>
	</div>
	
	<script type="text/javascript">
	jq(function() {
		jq('.button').button();
	});
	</script>

<% } %>

</div>