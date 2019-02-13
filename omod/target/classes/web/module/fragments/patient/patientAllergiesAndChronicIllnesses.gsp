<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Allergies & Chronic Illnesses", frameOnly: true ])
%>
<script type="text/javascript">

</script>

<style>
	.list-heading {
		color: #413c32;
		padding: 4px;
		text-align: left;
		font-weight: bold;
		font-size: 14px;
	}
</style>

<div class="ke-panel-content">
	<label class="list-heading">Allergens</label>
<% if (allergies) { %>
	<% allergies.each { rel -> %>
	<div class="ke-stack-item">	${ rel.allergen } 	</div>
	<% } %>
<% } else { %>
	<div class="ke-stack-item">	None </div>
<% } %>
<div style="clear: both"></div>
	<label class="list-heading">Chronic Illnesses</label>
<% if (illnesses) { %>
<% illnesses.each { rel -> %>
	<div class="ke-stack-item">	${ rel.illness } </div>
<% } %>
<% } else { %>
	<div class="ke-stack-item">None</div>
<% } %>
</div>

<div class="ke-panel-footer">
</div>