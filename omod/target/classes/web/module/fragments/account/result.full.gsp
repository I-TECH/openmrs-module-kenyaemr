<table style="width: 100%">
	<tr>
		<td style="width: 32px; vertical-align: top; padding-right: 5px">
			<img ng-src="${ ui.resourceLink("kenyaui", "images/buttons/") }{{ account.provider ? 'provider' : 'user' }}_{{ account.gender }}.png" />
		</td>
		<td style="text-align: left; vertical-align: top; width: 33%">
			<strong>{{ account.name }}</strong><br/>
			{{ account.birthDate }}
		</td>
		<td style="text-align: center; vertical-align: top; width: 33%">
			<div ng-if="account.provider">
				<span class="ke-identifier-type">Provider ID</span> <span class="ke-identifier-value">{{ account.provider.identifier }}</span>
			</div>
			<div ng-if="account.telephoneContact">
				<span class="ke-identifier-type">Telephone</span> <span class="ke-identifier-value">{{ account.telephoneContact }}</span>
			</div>
			<div ng-if="account.emailAddress">
				<span class="ke-identifier-type">Email</span> <span class="ke-identifier-value">{{ account.emailAddress }}</span>
			</div>
		</td>
		<td style="text-align: right; vertical-align: top; width: 33%">
			<div ng-if="account.user">
				<% if (config.showUsernames) { %>
				<span ng-if="account.user.online" class="ke-onlinetag">Logged in as <strong>{{ account.user.username }}</strong></span>
				<span ng-if="!account.user.online">Login <strong>{{ account.user.username }}</strong></span>
				<% } else { %>
				<span ng-if="account.user.online" class="ke-onlinetag">Online</span>
				<% } %>
			</div>
		</td>
	</tr>
</table>