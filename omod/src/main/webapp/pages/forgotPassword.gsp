<%
	ui.includeCss("kenyaui", "kenyaui.css")
	ui.includeCss("kenyaui", "toastmessage/css/jquery.toastmessage.css")
	ui.includeCss("kenyaemr", "kenyaemr.css");

	ui.includeJavascript("kenyaui", "jquery.js")
	ui.includeJavascript("kenyaui", "jquery.toastmessage.js")
	ui.includeJavascript("kenyaui", "kenyaui.js")
	ui.includeJavascript("kenyaui", "ui.js")
	ui.includeJavascript("kenyaemr", "kenyaemr.js")
%>

${ ui.includeFragment("kenyaemr", "pageHeader") }

${ ui.includeFragment("kenyaui", "notifications") }

<div class="fullwindow">

	<form method="post" style="padding: 20px; width: 500px">

		If you have saved a secret question and answer, you can use this form to reset your password.
		If not then you will have to contact your system administrator. Misuse of this form is a
		disciplinary offence.<br />
		<br />

		<% if (!secretQuestion) { %>

		Enter your username: <input type="text" name="uname" value="${ username }" /><br />
		<br />
		<input type="submit" value="Show secret question" />

		<% } else { %>

		Your secret question is: <b>${ secretQuestion }</b><br/>
		<br/>
		Enter your secret answer: <input type="text" name="secretAnswer" /><br />
		<br />
		<input type="hidden" name="uname" value="${ username }" />
		<input type="submit" value="Submit secret answer" />

		<% } %>
	</form>

</div>