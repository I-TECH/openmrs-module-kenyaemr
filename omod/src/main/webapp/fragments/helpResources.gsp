<%
	ui.includeCss("kenyaemrorderentry", "font-awesome.css")
	ui.includeCss("kenyaemrorderentry", "font-awesome.min.css")
	ui.includeCss("kenyaemrorderentry", "font-awesome.css.map")
	ui.includeCss("kenyaemrorderentry", "fontawesome-webfont.svg")
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

.navigation-button {
	background-color: cadetblue;
	border: none;
	color: white;
	padding: 15px 32px;
	text-align: center;
	text-decoration: none;
	display: inline-block;
	font-size: 16px;
	margin: 4px 2px;
	cursor: pointer;
	width: 30em;
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
	font-size: 1.4rem;
}
.telephone-number {
	font-size: 1.5em;
	color: brown;
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
	display:block;
}
.table-header{
	font-size: 1.6em;
	line-height: 1.6em;
	text-align: left;
}

.column-width {
	width: 50%;
	/*padding: 0 10px;*/
}

/* Remove extra left and right margins, due to padding */
.row {
	margin: 0 -5px;
	background-color: papayawhip;
}

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
	overflow-y: scroll;
	max-height: 38em;
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

input[name='panel']:checked ~  .accordion__content {
	/* Get this as close to what height you expect */
	max-height: 100em;
}

.service-desk-contact {
	width:650px;
	margin:0 auto;
	text-align: center;
	background-color: #e8e7e2;
	padding: 10px;
	border-radius: 4px;
	font-size: 1.5rem;
}




</style>

	<br/>

	<div class="service-desk-contact">
		<img src="${ ui.resourceLink("kenyaui", "images/glyphs/phone.png") }" class="ke-glyph" /> Call the help desk for free at <span class="telephone-number" > <strong>${ supportNumber }</strong></span><br />
		or<br />
		<img src="${ ui.resourceLink("kenyaui", "images/glyphs/email.png") }" class="ke-glyph" /> Email <a href="mailto:${ supportEmail }">${ supportEmail }</a>
		<br />
		<br />
	</div>
	<br />


	<div class="ke-page-content">
		<div style="text-align: center">
			<% if(isAuthenticated){ %>
			<% } else{ %>
				<button type="button" class="navigation-button" onclick="ui.navigate('login.htm')"> Login</button>
			<% } %>
		</div>
		<br/>
		<div id="label"><b>You may find the following resources helpful</b></div>
		<br/>
		<div>

		<div class="accordion">
			<!-- Kenyaemr navigation -->
			<div>
				<input type="radio" name="panel" id="panel-1">
				<label for="panel-1">KenyaEMR navigation SOPs</label>
				<div class="accordion__content ">
					<% if(kenyaemrNavigationPdfResources) {  %>
					<div>
						<div class="accordion__body">
							<table width="100%" style="padding-top: 20px">
								<th class="table-header">PDF</th>
								<th class="table-header">Video</th>
								<tr class="row">
									<td valign="top" class="column-width">
										<% kenyaemrNavigationPdfResources.each { resource -> %>

										<div>
											<div>
												<a class="card-label" href="${ resource.url }"   target="_blank">
													<img src="${ ui.resourceLink("kenyaemr", "images/file-pdf-solid2.svg") }" />
													${ resource.name }</a>
											</div>
										</div>

										<% } %>
									</td>
									<td valign="top" class="column-width">
										<% kenyaemrNavigationVideoResources.each { resource -> %>

										<div>
											<div>
												<a class="card-label" href="${ resource.url }"   target="_blank">
													<i class="fa fa-video-camera" style="margin-top: 7px"></i>
													${ resource.name }</a>
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

			<!-- HTS -->
			<div>
				<input type="radio" name="panel" id="panel-2">
				<label for="panel-2">HTS</label>
				<div class="accordion__content ">
					<% if(htsPdfResources) {  %>
					<div>
						<div class="accordion__body">
							<table width="100%" style="padding-top: 20px">
								<th class="table-header">PDF</th>
								<th class="table-header">Video</th>
								<tr class="row">
									<td valign="top" class="column-width">
										<% htsPdfResources.each { resource -> %>

										<div>
											<div>
												<a class="card-label" href="${ resource.url }"   target="_blank">
													<img src="${ ui.resourceLink("kenyaemr", "images/file-pdf-solid2.svg") }" />

													${ resource.name }</a>
											</div>
										</div>

										<% } %>
									</td>
									<td valign="top" class="column-width">
										<% htsVideoResources.each { resource -> %>

										<div>
											<div>
												<a class="card-label" href="${ resource.url }"   target="_blank">
													<i class="fa fa-video-camera" style="margin-top: 7px"></i>
													${ resource.name }</a>
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


			<!-- Data tools -->
			<div>
				<input type="radio" name="panel" id="panel-3">
				<label for="panel-3">Using Data tools</label>
			<div class="accordion__content ">
			<% if(dataToolsPdfResources) {  %>
				<div>
					<div class="accordion__body">
						<table width="100%" style="padding-top: 20px">
							<th class="table-header">PDF</th>
							<th class="table-header">Video</th>
							<tr class="row">
								<td valign="top" class="column-width">
									<% dataToolsPdfResources.each { resource -> %>

									<div>
										<div>
											<a class="card-label" href="${ resource.url }"   target="_blank">
												<img src="${ ui.resourceLink("kenyaemr", "images/file-pdf-solid2.svg") }" />
												${ resource.name }</a>
										</div>
									</div>

									<% } %>
								</td>
								<td valign="top" class="column-width">
									<% dataTooVideoResources.each { resource -> %>

									<div>
										<div>
											<a class="card-label" href="${ resource.url }"   target="_blank">
												<i class="fa fa-video-camera" style="margin-top: 7px"></i>
												${ resource.name }</a>
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
				<input type="radio" name="panel" id="panel-4">
				<label for="panel-4">OTZ </label>
				<div class="accordion__content">
					<div class="accordion__body">
						<table width="100%" style="padding-top: 20px">
							<th class="table-header">PDF</th>
							<th class="table-header">Video</th>
							<tr class="row">
								<td valign="top" class="column-width">
									<% otzPdfResources.each { resource -> %>

									<div>
										<div>
											<a class="card-label" href="${ resource.url }"   target="_blank">
												<img src="${ ui.resourceLink("kenyaemr", "images/file-pdf-solid2.svg") }" />
												${ resource.name }</a>
										</div>
									</div>

									<% } %>
								</td>
								<td class="column-width">
								</td>
							</tr>
						</table>
					</div>
				</div>
			</div>

			<!-- OVC -->
			<div>
				<input type="radio" name="panel" id="panel-5">
				<label for="panel-5">OVC</label>
			<div class="accordion__content">
				<div class="accordion__body">
					<table width="100%" style="padding-top: 20px">
						<th class="table-header">PDF</th>
						<th class="table-header">Video</th>
						<tr class="row">
							<td valign="top" class="column-width">
								<% ovcPdfResources.each { resource -> %>

								<div >
									<div>
										<a class="card-label" href="${ resource.url }"   target="_blank">
											<img src="${ ui.resourceLink("kenyaemr", "images/file-pdf-solid2.svg") }" />
											${ resource.name }</a>
									</div>
								</div>

								<% } %>
							</td>
							<td class="column-width">
							</td>
						</tr>
					</table>
				</div>
			</div>

		</div>

			<!-- PrEP -->
			<div>
				<input type="radio" name="panel" id="panel-6">
				<label for="panel-6">PrEP</label>
			<div class="accordion__content">
				<div class="accordion__body">
					<table width="100%" style="padding-top: 20px">
						<th class="table-header">PDF</th>
						<th class="table-header">Video</th>
						<tr class="row">
							<td valign="top" class="column-width">
								<% prepPdfResources.each { resource -> %>

								<div>
									<div>
										<a class="card-label" href="${ resource.url }"   target="_blank">
											<img src="${ ui.resourceLink("kenyaemr", "images/file-pdf-solid2.svg") }" />
											${ resource.name }</a>
									</div>
								</div>

								<% } %>
							</td>
							<td class="column-width">
							</td>
						</tr>
					</table>
				</div>
			</div>

		</div>

			<!-- DWAPI -->
			<div>
				<input type="radio" name="panel" id="panel-7">
				<label for="panel-7">DWAPI Application content</label>
				<div class="accordion__content">
					<div class="accordion__body">
						<table width="100%" style="padding-top: 20px">
							<th class="table-header">PDF</th>
							<th class="table-header">Video</th>
							<tr class="row">
								<td valign="top" class="column-width">
									<% dwapiPdfResources.each { resource -> %>

									<div>
										<div>
											<a class="card-label" href="${ resource.url }"   target="_blank">
												<img src="${ ui.resourceLink("kenyaemr", "images/file-pdf-solid2.svg") }" />
												${ resource.name }</a>
										</div>
									</div>

									<% } %>
								</td>
								<td class="column-width">
								</td>
							</tr>
						</table>
					</div>
				</div>

			</div>

			<!-- mUzima -->
			<div>
				<input type="radio" name="panel" id="panel-8">
				<label for="panel-8">mUzima</label>
				<div class="accordion__content">
					<div class="accordion__body">
						<table width="100%" style="padding-top:20px ">
							<th class="table-header">PDF</th>
							<th class="table-header">Video</th>
							<tr class="row">
								<td valign="top" class="column-width">
									<% muzimaPdfResources.each { resource -> %>

									<div>
										<div>
											<a class="card-label" href="${ resource.url }"   target="_blank">
												<img src="${ ui.resourceLink("kenyaemr", "images/file-pdf-solid2.svg") }" />
												${ resource.name }</a>
										</div>
									</div>

									<% } %>
								</td>
								<td class="column-width">
								</td>
							</tr>
						</table>
					</div>
				</div>

			</div>
			<!-- AIR -->
			<div>
				<input type="radio" name="panel" id="panel-9">
				<label for="panel-9">Automated Indicator Reporting (AIR)</label>
				<div class="accordion__content">
					<div class="accordion__body">
						<table width="100%" style="padding-top: 20px">
							<th class="table-header">PDF</th>
							<th class="table-header">Video</th>
							<tr class="row">
								<td valign="top" class="column-width">
									<% airPdfResources.each { resource -> %>

									<div>
										<div>
											<a class="card-label" href="${ resource.url }"   target="_blank">
												<img src="${ ui.resourceLink("kenyaemr", "images/file-pdf-solid2.svg") }" />
												${ resource.name }</a>
										</div>
									</div>

									<% } %>
								</td>
								<td class="column-width">
								</td>
							</tr>
						</table>
					</div>
				</div>

			</div>

			<!-- IL -->
			<div>
				<input type="radio" name="panel" id="panel-10">
				<label for="panel-10">Interoperability Layer(IL)</label>
				<div class="accordion__content">
					<% if(ilPdfResources) {  %>
					<div>
						<div class="accordion__body">
							<table width="100%" style="padding-top: 20px">
								<th class="table-header">PDF</th>
								<th class="table-header">Video</th>
								<tr class="row">
									<td valign="top" class="column-width">
										<% ilPdfResources.each { resource -> %>

										<div >
											<div>
												<a class="card-label" href="${ resource.url }"   target="_blank">
													<img src="${ ui.resourceLink("kenyaemr", "images/file-pdf-solid2.svg") }" />
													${ resource.name }</a>
											</div>
										</div>

										<% } %>
									</td>
									<td valign="top" class="column-width">
										<% ilVideoResources.each { resource -> %>

										<div >
											<div>
												<a class="card-label" href="${ resource.url }"   target="_blank">
													<i class="fa fa-video-camera" style="margin-top: 7px"></i>
													${ resource.name }</a>
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

		</div><!-- .accordion -->
	   </div>





	</div>
<br/>
	<div class="ke-panel-footer">

	</div>

