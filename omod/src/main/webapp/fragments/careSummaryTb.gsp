<%
	def dataPoints = []

	// TODO
%>

<% dataPoints.each { %>
<div>${ it.label }: <b>${ it.value }</b> <% if (it.date) { %><small>(${ kenyaEmrUi.formatDateNoTime(it.date) })</small><% } %></div>
<% } %>