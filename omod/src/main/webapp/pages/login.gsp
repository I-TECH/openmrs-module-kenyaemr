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
<script type="text/javascript">
	var jq = jQuery;
	var CONTEXT_PATH = '${ contextPath }';
</script>

<style type="text/css">
	.centered {
		vertical-align: middle;
		display: table-cell;
	}

	#forgot-password {
		font-size: 12px;
		padding: 3px;
	}
</style>

${ ui.includeFragment("kenyaemr", "pageHeader") }

${ ui.includeFragment("kenyaui", "notifications") }

<div class="fullwindow">
	<!-- Remote address: ${ remoteAddr } -->
	<div class="centered">
		<form method="post" action="/${ contextPath }/loginServlet" autocomplete="off">
			<table style="margin-left: auto; margin-right: auto;">
				<tr>
					<td rowspan="5" width="300">
						<img src="${ ui.resourceLink("kenyaui", "images/moh_logo_large.png") }"/>
					</td>
					<td colspan="2"><div style="height:75px;">&nbsp;</div></td>
				</tr>
				<tr>
					<th>Username</th>
					<td><input id="uname" type="text" name="uname"/></td>
				</tr>
				<tr>
					<th>Password</th>
					<td><input type="password" name="pw"/></td>
				</tr>
				<tr>
					<td></td>
					<td><input type="submit" value="Login"/></td>
				</tr>
				<tr>
					<td></td>
					<td valign="top"><a id="forgot-password" href="forgotPassword.form">Forgot password?</a></td>
				</tr>
			</table>
		</form>
	</div>
</div>

<script type="text/javascript">
	jQuery(function() {
		jQuery('#uname').focus();
	});
</script>
