<%
    def visitDates = ui.format(config.visit.startDatetime)
	if (config.visit.stopDatetime)
		visitDates += " to " + ui.format(config.visit.stopDatetime)
%>

<style type="text/css">
#selected-visit-header {
	padding: 3px;
	text-align: center;
	border-bottom: 1px #444 solid;
	background-color: #628c93;
	color: white;
}
</style>

<div id="selected-visit-header">
	Editing <span style="font-weight: bold">${ ui.format(config.visit.visitType) }</span> visit from ${ visitDates }
</div>