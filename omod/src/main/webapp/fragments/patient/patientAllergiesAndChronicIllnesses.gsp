<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Allergies & Chronic Illnesses", frameOnly: true ])
%>
<script type="text/javascript">

</script>


<div class="ke-panel-content">
<% if (allergies) { %>
	<% allergies.each { rel -> %>
	<div class="ke-stack-item">
		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Allergen", value: rel.allergen ]) }
	</div>
	<% } %>
<% } else { %>
	<div class="ke-stack-item">
		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Allergies", value: "None" ]) }
	</div>
<% } %>
<div style="clear: both"></div>
<% if (illnesses) { %>
<% illnesses.each { rel -> %>
	<div class="ke-stack-item">

		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Illness", value: rel.illness ]) }
	</div>
<% } %>
<% } else { %>
	<div class="ke-stack-item">

		${ ui.includeFragment("kenyaui", "widget/dataPoint", [ label: "Illnesses", value: "None" ]) }
	</div>
<% } %>
</div>

<div class="ke-panel-footer">
</div>