<%
	config.require("heading", "orders")

	ui.decorateWith("kenyaui", "panel", [ heading: config.heading ])
%>
<table class="ke-table-vertical">
	<thead>
		<th>Drug</th>
		<th>Dose</th>
		<th>Frequency</th>
		<th>Orderer</th>
		<th>Start date</th>
		<th>Stop date</th>
		<th>Instructions</th>
	</thead>
	<tbody>
	<% config.orders.each { order ->
		def stopDate = order.discontinuedDate ?: order.autoExpireDate
	%>
		<tr>
			<td>${ ui.format(order.concept) }</td>
			<td>${ ui.format(order.dose + (order.units ?: "")) }</td>
			<td>${ ui.format(order.frequency) }</td>
			<td>${ ui.format(order.orderer) }</td>
			<td>${ kenyaui.formatDate(order.startDate) }</td>
			<td>${ stopDate ? kenyaui.formatDate(stopDate) : "" }</td>
			<td>${ ui.format(order.instructions) }</td>
		</tr>
	<% } %>
	</tbody>
</table>