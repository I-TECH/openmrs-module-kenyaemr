<style>
textarea {
	border: 1px solid black;
	background: black;
	color: white;
	padding: 10px;
	width: 850px;
	height: 300px;
	overflow: scroll;
	font-size: 12px;
	font-family: Monaco,Andale Mono,Courier New,monospace;
}
</style>

<div class="ke-page-sidebar">
	<div class="ke-panel-frame">
		${ ui.includeFragment("kenyaui", "widget/panelMenuItem", [ iconProvider: "kenyaui", icon: "buttons/back.png", label: "Back", href: returnUrl ]) }
	</div>
</div>
<div class="ke-page-content">

	<h2>ADX Message for ${ reportName }</h2>
	<h4>Reporting Date: ${ startDate } - ${ endDate } </h4>

	<textarea>
		${ adx }
	</textarea>

	<br/>

	<div id="showStatus">
		<span id="msgSpan"></span> &nbsp;&nbsp;<img src="${ ui.resourceLink("kenyaui", "images/loader_small.gif") }"/>
	</div>
	<div id="msg"></div>
	<button id="post">Submit Message</button>

</div>

<script type="text/javascript">
    jq = jQuery;

    jq(function() {
        jq("#showStatus").hide();
        jq('#post').click(function() {
            jq("#msgSpan").text("Sending Message to IL Server .....");
            jq("#showStatus").show();
            jq("#msg").text("");

            jq("#post").prop("disabled", true);
            jq.getJSON('${ ui.actionLink("buildXmlDocument") }', {
                'request': '${ reportRequest.id }',
                'returnUrl': '${ returnUrl }'
            })
                .success(function(data) {
                    jq("#showStatus").hide();
                    jq("#msg").text("Message successfully sent");
                    jq("#post").prop("disabled", false);
                })
                .error(function(xhr, status, err) {
                    jq("#showStatus").hide();
                    jq("#msg").text("There was an error sending message");
                    jq("#post").prop("disabled", false);
                    alert('AJAX error ' + err);
                })

        });

    });
</script>