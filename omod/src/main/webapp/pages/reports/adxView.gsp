<%
	ui.decorateWith("kenyaemr", "standardPage")
%>
<style>
textarea {
	border: 1px solid black;
	background: #545556;
	color: white;
	padding: 10px;
	width: 650px;
	height: 200px;
	overflow: scroll;
}
</style>

<div class="ke-page-sidebar">
	<div class="ke-panel-frame">
		${ ui.includeFragment("kenyaui", "widget/panelMenuItem", [ iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back", href: returnUrl ]) }
	</div>
</div>
<div class="ke-page-content">

	<h2>ADX Message for ${ reportName }</h2>
	<h4>Report generated between ${ evaluationStart } and ${ evaluationEnd } </h4>
	<textarea>
		${ adx }
	</textarea>

	<br/>

	<button>Submit Message</button>

</div>