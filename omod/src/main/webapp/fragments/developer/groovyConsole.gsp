<%
	ui.decorateWith("kenyaui", "panel", [ heading: "Groovy Console", frameOnly: true ]);

	ui.includeJavascript("kenyaemr", "codemirror.js");
	ui.includeJavascript("kenyaemr", "codemirror-groovy.js");
	ui.includeCss("kenyaemr", "codemirror.css");

	ui.includeJavascript("kenyaemr", "controllers/developer.js");
%>
<style type="text/css">
	.CodeMirror {
		border: 1px #ccc solid;
		margin-bottom: 5px;
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

<script type="text/javascript">
	jQuery(function() {
		window.codeMirrorEditor = CodeMirror.fromTextArea(jQuery('#groovy-script').get(0), {
			lineNumbers: true,
			indentWithTabs: true,
			mode: 'text/x-groovy'
		});
	});
</script>

<div ng-controller="GroovyConsoleController">
	<div class="ke-panel-content">
		<textarea id="groovy-script" rows="10" cols="100"></textarea>

		${ ui.includeFragment("kenyaui", "widget/tabMenu", [ items: [
					[ label: "Result", tabid: "result" ],
					[ label: "Output", tabid: "output" ],
					[ label: "Stacktrace", tabid: "stacktrace" ]
		] ]) }

		<div class="ke-tab" data-tabid="result">
			<pre class="groovy-eval">{{ result }}</pre>
		</div>
		<div class="ke-tab" data-tabid="output">
			<pre class="groovy-eval">{{ output }}</pre>
		</div>
		<div class="ke-tab" data-tabid="stacktrace">
			<pre class="groovy-eval">{{ stacktrace }}</pre>
		</div>
	</div>

	<div class="ke-panel-controls">
		<button ng-click="run()" ng-disabled="running"><img src="${ ui.resourceLink("images/glyphs/start.png") }" /> Run</button>
	</div>
</div>