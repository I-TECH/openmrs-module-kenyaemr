<%

%>
<script type="text/javascript">



</script>
<style>

#closeButton{
	width:450px;
	margin:0 auto;
	text-align: center;
	alignment: center !important;
}
#header{
	width:450px;
	margin:0 auto;
	text-align: center;
}
#label{
	width:600px;
	margin:0 auto;
	text-align: center;
}
.telephone-number {
	font-size: 1.5em;
}
* {
	box-sizing: border-box;
}

body {
	font-family: Arial, Helvetica, sans-serif;
}

/* Float four columns side by side */
.column {
	float: left;
	width: 25%;
	padding: 0 10px;
}
.card-label{
	font-size: 1.3em;
	line-height: 1.6em;
}

.pdfcolumn {
	float: left;
	width: 50%;
	padding: 0 10px;
}

/* Remove extra left and right margins, due to padding */
.row {margin: 0 -5px;}

/* Clear floats after the columns */
.row:after {
	content: "";
	display: table;
	clear: both;
}

/* Responsive columns */
@media screen and (max-width: 600px) {
	.column {
		width: 100%;
		display: block;
		margin-bottom: 20px;
	}
}

/* Style the counter cards */
.card {
	box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2);
	padding: 16px;
	text-align: center;
	background-color: #f1f1f1;
}



/*
* Typography
*/
@import url(https://fonts.googleapis.com/css?family=Open+Sans:400,700);
/*
* Color Variables
*/
/*
* Animation Variables
*/
/*
* Global
*/
*,
*:before,
*:after {
	box-sizing: border-box;
}

/*
* Base
*/
body {
	font-family: 'Open Sans', sans-serif;
	/*background: #eee;*/
}
body:before {
	content: '';
	/*! position: absolute; */
	/*! top: 0; */
	/*! right: 0; */
	/*! bottom: 0; */
	left: 0;
	/*! background: linear-gradient(to bottom, rgba(0, 0, 0, 0) 0%, rgba(0, 0, 0, 0.3) 100%); */
}

/*
* Accordion
*/
/* Basic Accordion Styles */
.accordion {
	width: 100%;
	padding-right: 2em;
	padding-left: 2em;
	box-shadow: 0 1px 8px rgba(0, 0, 0, 0.25);
	/* Radio Inputs */
	/* Labels */
	/* Panel Content */
}
.accordion input[name='panel'] {
	display: none;
}
.accordion label {
	position: relative;
	display: block;
	padding: 1em;
	/*background: linear-gradient(to bottom, #fefefe 0%, #d1d1d1 50%, #dbdbdb 55%, #e2e2e2 100%);*/
	border-top: 1px solid #fff;
	border-bottom: 1px solid rgba(0, 0, 0, 0.15);
	box-shadow: inset 0 2px 0 #fff;
	font-size: 1.5em;
	text-shadow: 0 1px 0 rgba(255, 255, 255, 0.75);
	color: #666;
	cursor: pointer;
	transition: all 0.4s cubic-bezier(0.865, 0.14, 0.095, 0.87);
}
.accordion label:after {
	content: '+';
	position: absolute;
	right: 1em;
	width: 1em;
	height: 1em;
	color: #eee;
	text-align: center;
	border-radius: 50%;
	background: #2980b9;
	box-shadow: inset 0 1px 6px rgba(0, 0, 0, 0.5), 0 1px 0 #fff;
	text-shadow: 0 1px 0 rgba(0, 0, 0, 0.75);
}
.accordion label:hover {
	color: #2980b9;
}
.accordion input:checked + label {
	color: #2980b9;
}
.accordion input:checked + label:after {
	content: '-';
	/* adjsut line-height to vertically center icon */
	line-height: .8em;
}
.accordion .accordion__content {
	overflow: hidden;
	max-height: 0em;
	position: relative;
	padding: 0 1.5em;
	box-shadow: inset 4px 0 0 0 #2980b9, inset 0 3px 6px rgba(0, 0, 0, 0);
	border-bottom: 1px solid rgba(0, 0, 0, 0.1);

	/*background: #444;
	background: linear-gradient(to bottom, #444444 0%, #222222 100%);
	color: #eee;*/
	transition: all 0.4s cubic-bezier(0.865, 0.14, 0.095, 0.87);
}
.accordion .accordion__content:not(:last-of-type) {
	box-shadow: inset 0 -2px 2px rgba(0, 0, 0, 0.25), inset 4px 0 0 0 #2980b9, inset 0 3px 6px rgba(0, 0, 0, 0);
	border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}
.accordion .accordion__content .accordion__header {
	padding: 0 0;
}
.accordion .accordion__content .accordion__body {
	font-size: .825em;
	line-height: 1.4em;
	padding: 0 0 1.5em;
}

input[name='panel']:checked ~ .accordion__content {
	/* Get this as close to what height you expect */
	max-height: 50em;
}





</style>
	<div id="header"><h2>HELP</h2></div>
	<br/>

	<div style="width:450px; margin:0 auto; text-align: center; background-color: #e8e7e2; padding: 10px; border-radius: 4px">
		<img src="${ ui.resourceLink("kenyaui", "images/glyphs/phone.png") }" class="ke-glyph" /> Call the help desk for free at <span class="telephone-number" > <strong>${ supportNumber }</strong></span><br />
		or<br />
		<img src="${ ui.resourceLink("kenyaui", "images/glyphs/email.png") }" class="ke-glyph" /> Email <a href="mailto:${ supportEmail }">${ supportEmail }</a>
		<br />
		<br />
	</div>
	<br />


	<div class="ke-page-content">

		<div id="label"><b>You may find the following resources helpful <em>(choose category)</em>:</b></div>
		<br />


		<div>

		<div class="accordion">

			<!-- Data tools -->
			<div>
				<input type="checkbox" name="panel" id="panel-1">
				<label for="panel-1">Using Data tools</label>
			<div class="accordion__content">
			<% if(dataToolsPdfResources) {  %>
				<div>
					<h3 class="accordion__header">Data tools pdf resource(s)</h3>
					<div class="accordion__body">
						<table width="100%">
							<tr class="row">
								<td valign="top">
									<% dataToolsPdfResources.each { resource -> %>

									<div class="pdfcolumn">
										<div>
											<a class="card-label" href="${ resource.url }"   target="_blank">${ resource.name }</a>
										</div>
									</div>

									<% } %>
								</td>
							</tr>
						</table>
					</div>
				</div>
			<% } %>

			<% if(dataTooVideoResources) {  %>

			<div>
				<h3 class="accordion__header">Data tools video resource(s)</h3>
				<div class="accordion__body">
					<table width="100%">
						<tr class="row">
							<td valign="top">
								<% dataTooVideoResources.each { resource -> %>

								<div class="column">
									<div class="card">
										<a class="card-label" href="${ resource.url }"   target="_blank">${ resource.name }</a>
										<video width="270" height="155" controls>
											<source src="http://localhost:8080${ resource.url }" type="video/mp4">
										</video>
									</div>
								</div>

								<% } %>
							</td>
						</tr>
					</table>


				</div>
			</div>
			<% } %>
			</div>
			</div>

			<!-- OTZ module -->
			<div>
				<input type="checkbox" name="panel" id="panel-2">
				<label for="panel-2">OTZ </label>
				<div class="accordion__content">
					<h3 class="accordion__header">OTZ pdf resource(s)</h3>
					<div class="accordion__body">
						<table width="100%">
							<tr class="row">
								<td valign="top">
									<% otzPdfResources.each { resource -> %>

									<div class="pdfcolumn">
										<div>
											<a class="card-label" href="${ resource.url }"   target="_blank">${ resource.name }</a>
										</div>
									</div>

									<% } %>
								</td>
							</tr>
						</table>
					</div>
				</div>
			</div>

			<!-- OVC -->
			<div>
				<input type="checkbox" name="panel" id="panel-3">
				<label for="panel-3">OVC</label>
			<div class="accordion__content">
				<h3 class="accordion__header">OVC pdf resource(s)</h3>
				<div class="accordion__body">
					<table width="100%">
						<tr class="row">
							<td valign="top">
								<% ovcPdfResources.each { resource -> %>

								<div class="pdfcolumn">
									<div>
										<a class="card-label" href="${ resource.url }"   target="_blank">${ resource.name }</a>
									</div>
								</div>

								<% } %>
							</td>
						</tr>
					</table>
				</div>
			</div>

		</div>

			<!-- PrEP -->
			<div>
				<input type="checkbox" name="panel" id="panel-4">
				<label for="panel-4">PrEP</label>
			<div class="accordion__content">
				<h3 class="accordion__header">PrEP pdf resource(s)</h3>
				<div class="accordion__body">
					<table width="100%">
						<tr class="row">
							<td valign="top">
								<% prepPdfResources.each { resource -> %>

								<div class="pdfcolumn">
									<div>
										<a class="card-label" href="${ resource.url }"   target="_blank">${ resource.name }</a>
									</div>
								</div>

								<% } %>
							</td>
						</tr>
					</table>
				</div>
			</div>

		</div>

			<!-- DWAPI -->
			<div>
				<input type="checkbox" name="panel" id="panel-5">
				<label for="panel-5">DWAPI Application content</label>
				<div class="accordion__content">
					<h3 class="accordion__header">DWAPI pdf resource(s)</h3>
					<div class="accordion__body">
						<table width="100%">
							<tr class="row">
								<td valign="top">
									<% dwapiPdfResources.each { resource -> %>

									<div class="pdfcolumn">
										<div>
											<a class="card-label" href="${ resource.url }"   target="_blank">${ resource.name }</a>
										</div>
									</div>

									<% } %>
								</td>
							</tr>
						</table>
					</div>
				</div>

			</div>

		</div><!-- .accordion -->
	   </div>





	</div>
<br/>
	<div class="ke-panel-footer">
		<% if(isAuthenticated){ %>
		<div id="closeButton">
			<button type="button" style="alignment: center" onclick="ui.navigate('userHome.page?')"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/cancel.png") }" /> Back to home</button>
		</div>
		<% } else{ %>
		<div id="closeButton">
			<button type="button" style="alignment: center" onclick="ui.navigate('login.htm')"><img src="${ ui.resourceLink("kenyaui", "images/glyphs/cancel.png") }" /> Cancel</button>
		</div>

		<% } %>
	</div>

