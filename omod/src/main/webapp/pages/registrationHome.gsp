<%
	ui.decorateWith("standardAppPage")
%>

<style>
.col2 {
	width: 48%;
	float: left;
}
</style>

<div class="col2">
	<h1>Registration App</h1>
	<h3>Welcome!</h3>
	
	${ ui.includeFragment("widget/button", [ icon: "search_32.png", label: "Find a Patient", href: ui.pageLink("registrationSearch") ]) }
</div>

<div class="col2">
	<h2>Checked In Patients</h2>
	List of currently checked in patients (clicking takes you to their open visit)
	<br/>
	Link to "Close of business day, check everyone out"
</div>