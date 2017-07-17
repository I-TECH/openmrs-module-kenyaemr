<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Allergies & Chronic Illnesses", frameOnly: true ])
%>
<script type="text/javascript">

</script>

<% if (allergies) { %>
<div class="ke-panel-content">

	<% allergies.each { rel -> %>
	<div class="ke-stack-item">

		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Allergen", value: rel ]) }

		<div style="clear: both"></div>
	</div>
	<% } %>
</div>
<% } %>

<div class="ke-panel-footer">
</div>