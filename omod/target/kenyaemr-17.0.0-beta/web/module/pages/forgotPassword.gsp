<%
	ui.decorateWith("kenyaemr", "standardPage")
%>
<style type="text/css">
	body {
		background-color: #e0d8cd;
	}
</style>

<form method="post" action="${ ui.pageLink("kenyaemr", "forgotPassword") }" style="padding: 20px; width: 500px">

	If you have saved a secret question and answer, you can use this form to reset your password.
	If not then you will have to contact your system administrator. Misuse of this form is a
	disciplinary offence.<br />
	<br />

	<% if (!secretQuestion) { %>

	Enter your username: <input type="text" name="uname" value="${ username }" /><br />
	<br />
	<button type="submit"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/secret.png") }" /> Show secret question</button>
	<button type="button" onclick="ui.navigate('login.htm')"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/cancel.png") }" /> Cancel</button>

	<% } else { %>

	Your secret question is: <strong>${ secretQuestion }</strong><br/>
	<br/>
	Enter your secret answer: <input type="text" name="secretAnswer" /><br />
	<br />
	<input type="hidden" name="uname" value="${ username }" />
	<button type="submit"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/login.png") }" /> Submit secret answer</button>

	<% } %>
</form>