<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Groovy Console" ]);

	ui.includeJavascript("kenyaemr", "codemirror.js");
	ui.includeJavascript("kenyaemr", "codemirror-groovy.js");
	ui.includeCss("kenyaemr", "codemirror.css");
%>
<style type="text/css">
	.CodeMirror {
		border: 1px #ccc solid;
	}
	.CodeMirror * {
		font-family: monospace;
	}
	.groovy-eval {
		background-color: #EEE;
		font-family: monospace;
		margin: 0;
	}
</style>

<textarea id="groovy-script" rows="10" cols="100"></textarea>

<script type="text/javascript">
	jQuery(function() {
		jQuery('#groovy-run').click(function() {
			var script = editor.getValue();
			jQuery.post(ui.fragmentActionLink('kenyaemr', 'developer/developerUtils', 'executeGroovy', { returnFormat: 'json' }), { script: script }, function(data) {
				jQuery('#groovy-result').html(data.result);
				jQuery('#groovy-output').html(data.output);
				jQuery('#groovy-stacktrace').html(data.stacktrace);
			}, 'json');
		});
	});

	var editor = CodeMirror.fromTextArea(jQuery('#groovy-script').get(0), {
		lineNumbers: true,
		indentWithTabs: true,
		mode: 'text/x-groovy'
	});
</script>

<div style="padding: 5px 0 10px 0">
	<button id="groovy-run"><img src="${ ui.resourceLink("images/glyphs/start.png") }" /> Run</button>
</div>

${ ui.includeFragment("kenyaui", "widget/tabMenu", [ items: [
			[ label: "Result", tabid: "result" ],
			[ label: "Output", tabid: "output" ],
			[ label: "Stacktrace", tabid: "stacktrace" ]
] ]) }

<div class="ke-tab" data-tabid="result">
	<pre class="groovy-eval" id="groovy-result"> </pre>
</div>
<div class="ke-tab" data-tabid="output">
	<pre class="groovy-eval" id="groovy-output"> </pre>
</div>
<div class="ke-tab" data-tabid="stacktrace">
	<pre class="groovy-eval" id="groovy-stacktrace"> </pre>
</div>