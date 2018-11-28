
<style type="text/css">
.dropdown {
	display: inline-block;
	top: 12%;
	right: 5%;
	margin: 0 0;
}
.dropdown-content {
	display: none;
	right: 0;
	padding-top: 0%;
	padding-bottom: 0%;
	width: auto;
	height: auto;
	background-color: #7f7b72;
	overflow: hidden;
	border: 3px solid #edede6;
	font-size: 15px;
}
.dropdown-content a {
	color: white;
	padding: 3%;
	text-decoration: none;
	display: inline-block;
}
.dropdown-content a:hover {
}
.dropdown:hover .dropdown-content {
	display: inline-block;
}
.dropdown:hover .dropbtn {
	background-color: transparent;
}
</style>
<div>
	<div style="float: left">
		<div >
			<ul>
				<li class="dropdown">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">
						<i class="fa fa-th"></i>
						<span class="hidden-xs hidden-sm">More</span>
					</a>
					<ul class="dropdown-content linguas" style="background-color:#7f7b72">
						<li style="cursor: pointer;background-color:#7f7b72" >
							<a data-toggle="modal" data-target="#generalModalLabs">
								<i class="fa fa-medkit"></i>
								<span>Lab Orders</span>
							</a>
						</li>
						<li style="cursor: pointer;background-color:#7f7b72">
							<a data-toggle="modal" data-target="#generalModalLabk">
								<i class="fa fa-medkit"></i>
								<span>Drug Orders</span>
							</a>
						</li>
					</ul>
				</li>
			</ul>
		</div>
	</div>
</div>
</div>