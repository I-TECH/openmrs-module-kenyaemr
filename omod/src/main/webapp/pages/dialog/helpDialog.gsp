<%
	// Help dialog content - loaded into a modal dialog

	// Split resources into two columns for layout
	def colMax = (int) Math.ceil(resources.size() / 2);
	def resourceCols = [
			resources[0..colMax - 1],
			resources[colMax..resources.size() - 1]
	]
%>
<div class="ke-panel-content">
	If you are experiencing a problem you should contact your clinic's IT admin for support.
	You may also find the following resources helpful <em>(all links open in new windows)</em>:
	<br />

	<table>
		<tr>
			<% resourceCols.each { resources -> %>
			<td valign="top">
				<ul>
					<% resources.each { resource -> %>
					<li><a href="${ resource.url }" target="_blank">${ resource.name }</a></li>
					<% } %>
				</ul>
			</td>
			<% } %>
		</tr>
	</table>

	If those do not resolve your problem, then you can submit a support ticket. To do so:<br />
	<br />

	<div style="width:450px; margin:0 auto; text-align: center; background-color: #e8e7e2; padding: 10px; border-radius: 4px">
		<img src="${ ui.resourceLink("kenyaui", "images/glyphs/phone.png") }" class="ke-glyph" /> Call the help desk for free at <strong>${ supportNumber }</strong><br />
		or<br />
		<img src="${ ui.resourceLink("kenyaui", "images/glyphs/email.png") }" class="ke-glyph" /> Email <a href="mailto:${ supportEmail }">${ supportEmail }</a>
		<br />
		<br />
		Please include your facility code which is <strong>${ facilityCode }</strong>
	</div>
	<br />

</div>
<div class="ke-panel-footer">
	<button type="button" onclick="kenyaui.closeDialog()"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/close.png") }" /> Close</button>
</div>