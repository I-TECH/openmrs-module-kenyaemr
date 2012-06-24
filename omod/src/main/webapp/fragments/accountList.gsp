<%
	// config supports "page", which will set up a clickFunction, that will have personId=... appended
	// provides a default numResultsFormatter to widget/panelList unless you override it 
	
	if (!config.numResultsFormatter) {
		config.numResultsFormatter = """function(listOfItems) { return listOfItems.length + " account(s)"; }""" 
	}
	
	def clickFunction = null
	if (config.page) {
		clickFunction = """function accountPanelClicked() {
				location.href = pageLink('${ config.page }', { personId: jq(this).find('input[name=personId]').val() });
			}"""
	}
%>
<script>
	var accountPanelOpts = {
		icon: '<img width="32" height="32" src="${ ui.resourceLink('uilibrary', 'images/user_business_32.png') }"/>',
		title: function(account) {
			return account.personName + '<input type="hidden" name="personId" value="' + account.personId + '"/>';
		},
		leftDetails: function(account) {
			var tmp = [];
			if (account.user) {
				tmp.push("User");
			}
			if (account.provider) {
				tmp.push("Provider");
			}
			return tmp.join(" + ");
		},
		right: function(account) {
			var tmp = [];
			if (account.user) {
				tmp.push('Username: ' + account.user.username);
			}
			if (account.provider) {
				tmp.push('Provider ID: ' + account.provider.identifier);
			}
			return tmp.join("<br/>");
		}
	};
	
	function formatAccountAsPanel(account) {
		return kenyaemr.twoColumnPanelFormatter(account, accountPanelOpts);
	}
</script>

<%= ui.includeFragment("widget/panelList", config.merge([
		itemFormatter: "formatAccountAsPanel",
		clickFunction: clickFunction
	])) %>