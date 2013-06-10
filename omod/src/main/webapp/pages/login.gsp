<%
	ui.decorateWith("kenyaemr", "standardPage")
%>
<style type="text/css">
	body {
		background-color: #e0d8cd;
	}
</style>

<div style="text-align: center; padding-top: 100px">
	<form method="post" action="/${ contextPath }/loginServlet" autocomplete="off">
		<table style="margin-left: auto; margin-right: auto; border: 0" cellpadding="3" cellspacing="0">
			<tr>
				<td style="padding-right: 15px">
					<img src="${ ui.resourceLink("kenyaemr", "images/logos/moh_large.png") }"/>
				</td>
				<td style="padding-left: 15px; padding-top: 75px" valign="top">
					<table border="0" cellpadding="3" cellspacing="0">
						<tr>
							<th style="text-align: left">Username</th>
							<td style="text-align: left"><input id="uname" type="text" name="uname" style="width: 200px"/></td>
						</tr>
						<tr>
							<th style="text-align: left">Password</th>
							<td style="text-align: left"><input type="password" name="pw" style="width: 200px" /></td>
						</tr>
						<tr>
							<td></td>
							<td style="text-align: left"><input type="submit" value="Login"/></td>
						</tr>
						<tr>
							<td></td>
							<td style="text-align: left; vertical-align: top"><a style="font-size: 12px" href="forgotPassword.form">Forgot password?</a></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</form>
</div>

<script type="text/javascript">
	jq(function() {
		jq('#uname').focus();
	});
</script>
