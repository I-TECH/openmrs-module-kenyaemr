<%
	ui.includeCss("uilibrary", "uilibrary.css")
	ui.includeCss("uilibrary", "toastmessage/css/jquery.toastmessage.css")
	ui.includeCss("kenyaemr", "kenyaemr.css");

	ui.includeJavascript("uilibrary", "jquery.js")
	ui.includeJavascript("uilibrary", "jquery.toastmessage.js")
	ui.includeJavascript("uilibrary", "uiframework.js")
	ui.includeJavascript("uilibrary", "ui.js")
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
	
	#errors {
		margin: 1em;
		text-align: center;
		background-color: #FFFFBB;
		border: 1px gray solid;
	}

	#forgot-password {
		font-size: 12px;
		padding: 3px;
	}
</style>

${ ui.includeFragment("kenyaemr", "pageHeader") }

<div class="fullwindow">
	<!-- Remote address: ${ remoteAddr } -->
	<div class="centered">
		<form method="post" action="/${ contextPath }/loginServlet" autocomplete="off">
			<table style="margin-left: auto; margin-right: auto;">
				<tr>
					<td rowspan="5" width="300">
						<img src="${ ui.resourceLink("kenyaemr", "images/moh_logo_large.png") }"/>
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
				<tr>
					<td colspan="3">
						${ ui.includeFragment("uilibrary", "flashMessage") }
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
