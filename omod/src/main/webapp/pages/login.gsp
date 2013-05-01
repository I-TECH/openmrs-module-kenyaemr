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
			<table style="margin-left: auto; margin-right: auto; border: 0" cellpadding="3" cellspacing="0">
				<tr>
					<td style="padding-right: 15px">
						<img src="${ ui.resourceLink("kenyaui", "images/moh_logo_large.png") }"/>
					</td>
					<td style="padding-left: 15px; padding-top: 75px" valign="top">
						<table border="0" cellpadding="3" cellspacing="0">
							<tr>
								<th>Username</th>
								<td><input id="uname" type="text" name="uname" style="width: 200px"/></td>
							</tr>
							<tr>
								<th>Password</th>
								<td><input type="password" name="pw" style="width: 200px" /></td>
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
					</td>
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
