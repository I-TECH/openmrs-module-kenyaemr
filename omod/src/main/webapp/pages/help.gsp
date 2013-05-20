<%
	ui.decorateWith("kenyaemr", "standardPage"/*, [ patient: patient ]*/)
%>
<div id="content">
	<div class="ke-panel-frame">
		<div class="ke-panel-heading">Help</div>
		<div class="ke-panel-content">
			If you are experiencing a problem you should contact your clinic's IT admin for support.
			You may also find the following resources helpful <em>(all links open in new windows)</em>:
			<br />

			<ul>
				<li><a href="">[TODO Link to release note]</a></li>
				<li><a href="">[TODO Link to job aid for RDE]</a></li>
				<li><a href="">[TODO Link to job aid 2]</a></li>
				<li><a href="">[TODO Link to job Aid 3]</a></li>
			</ul>

			If those do not resolve your problem, then you can submit a support ticket. To do so:<br />
			<br />

			<div style="width:450px; margin:0 auto; text-align: center; background-color: #e8e7e2; padding: 10px; border-radius: 4px">
				<img src="${ ui.resourceLink("kenyaui", "images/glyphs/phone.png") }" style="vertical-align: text-bottom" /> Call the help desk tariff free at <strong>${ supportNumber }</strong><br />
				or<br />
				<img src="${ ui.resourceLink("kenyaui", "images/glyphs/email.png") }" style="vertical-align: text-bottom" /> Email <a href="mailto:${ supportEmail }">${ supportEmail }</a>
				<br />
				<br />
				Please include your facility code which is <strong>${ facilityCode }</strong>
			</div>
			<br />

		</div>
	</div>
</div>