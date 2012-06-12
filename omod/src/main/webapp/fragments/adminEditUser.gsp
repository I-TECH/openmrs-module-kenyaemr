<style>
	.user-details {
		width: 33%;
	}
	
	.user-details .label {
		color: #888888;
	}
</style>

<br/>
<br/>

<% if (user.retired) { %>
	<div class="ui-widget">
		<div class="ui-widget-header">
			Account disabled
		</div>
	</div>
	
	<br/>
	<form method="post" action="${ ui.actionLink("adminEditUser", "unretireUser") }">
		<input type="hidden" name="userId" value="${ user.userId }"/>
		<input type="submit" value="Enable account"/>
	</form>
<% } %>

<div class="user-details ui-widget <% if (user.retired) { %>ui-state-disabled<% } %>">
	<div class="ui-widget-header">
		<img class="icon" src="${ ui.resourceLink("images/user_32.png") }"/>
		<span class="label">User:</span>
		${ user.username }
		<br/>
		<span class="label">Real Name:</span>
		${ user.personName }
	</div>
</div>

<br/>

<% if (!user.retired) { %>
	Actions:
	<ul>
		<li>TODO: Change password</li>
		<li>
			<form method="post" action="${ ui.actionLink("adminEditUser", "retireUser") }">
				<input type="hidden" name="userId" value="${ user.userId }"/>
				<input type="submit" value="Disable account"/>
			</form>
		</li>
	</ul>
<% } %>