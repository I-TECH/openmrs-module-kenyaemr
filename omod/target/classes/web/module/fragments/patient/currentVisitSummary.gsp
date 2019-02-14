<%
    ui.includeCss("kenyaemr", "referenceapplication.css", 100)
%>



<div>


    <div id="content" class="container">

        <div class="clear"></div>
        <div class="container">
            <div class="dashboard clear">
                <div class="info-container column">

                    <div class="info-section">
                        <div class="info-header">
                            <i class="icon-diagnosis"></i>
                            <h3>Vitals</h3>
                        </div>
                        <div class="info-body">
                            <% if (vitals) { %>
                                <table>
                                    <tr>
                                        <th>&nbsp;</th>
                                        <th>Value</th>
                                    </tr>
                                    <tr>
                                        <td>Weight</td>
                                        <td>${vitals.weight}</td>
                                    </tr>
                                    <tr>
                                        <td>Height</td>
                                        <td>${vitals.height}</td>
                                    </tr>
                                    <tr>
                                        <td>Temperature</td>
                                        <td>${vitals.temperature}  &#176;C</td>
                                    </tr>
                                    <tr>
                                        <td>Pulse Rate</td>
                                        <td>${vitals.pulse}</td>
                                    </tr>
                                    <tr>
                                        <td>BP</td>
                                        <td>${vitals.bp}</td>
                                    </tr>
                                    <tr>
                                        <td>Respiratory Rate</td>
                                        <td>${vitals.resp_rate}</td>
                                    </tr>
                                    <tr>
                                        <td>Oxygen Saturation</td>
                                        <td>${vitals.oxygen_saturation}</td>
                                    </tr>
                                    <tr>
                                        <td>MUAC</td>
                                        <td>${vitals.muac}</td>
                                    </tr>
                                    <tr>
                                        <td>LMP</td>
                                        <td>${vitals.lmp}</td>
                                    </tr>

                        </table>

                            <% } else { %>
                                No vitals for this visit
                            <% } %>
                            <!-- <a class="view-more">Show more info ></a> //-->
                        </div>
                    </div>

                    <div class="info-section">
                        <div class="info-header">
                            <i class="icon-calendar"></i>
                            <h3>Recent Visits</h3>
                        </div>
                        <div class="info-body">
                            <% if(recentVisits) { %>
                            <ul>
                            <% recentVisits.each { %>
                            <li class="clear">
                                ${it.visitDate}
                                <div class="tag">
                                    <% if(it.active) { %>
                                        Active -
                                    <% } %>
                                    Outpatient
                                </div>
                            </li>

                            <% } %>
                                </ul>
                            <% } else { %>
                                No visit in the last six months
                            <% } %>

                        </div>
                    </div>

                </div>
                <div class="info-container column">

                    <div class="info-section">
                        <div class="info-header">
                            <i class="icon-calendar"></i>
                            <h3>Diagnosis</h3>
                        </div>
                        <div class="info-body">
                            <% if (diagnoses) { %>
                            <% diagnoses.each { d -> %>
                            <div class="ke-stack-item">	${ d.diagnosis } </div>
                            <% } %>
                            <% } else { %>
                            <div class="ke-stack-item">None</div>
                            <% } %>
                        </div>
                    </div>

                    <div class="info-section allergies">
                        <div class="info-header">
                            <i class="icon-medical"></i>
                            <h3>Medications</h3>
                        </div>
                        <div class="info-body">

                            <% if (medication) { %>
                            <table>
                                <tr>
                                    <th>Drug</th>
                                    <th>Frequency</th>
                                    <th>Duration</th>
                                    <th>Visit Date</th>
                                </tr>
                            <% medication.each { m-> %>
                                <tr>
                                    <td>${m.drug}</td>
                                    <td>${m.frequency}</td>
                                    <td>${m.duration}</td>
                                    <td>${m.visitDate}</td>
                                </tr>
                            <% }  %>
                            </table>

                            <% } else { %>
                                None
                            <% } %>
                        </div>
                    </div>


                </div>

            </div>
        </div>
    </div>

</div><div class="datetimepicker datetimepicker-dropdown-bottom-left dropdown-menu" style="left: -162px;"><div class="datetimepicker-minutes" style="display: none;"><table class=" table-condensed"><thead><tr><th class="prev" style="visibility: visible;"><i class="icon-arrow-left"></i></th><th colspan="5" class="switch">7 August 2017</th><th class="next" style="visibility: hidden;"><i class="icon-arrow-right"></i></th></tr></thead><tbody><tr><td colspan="7"><span class="minute active">0:00</span><span class="minute disabled">0:05</span><span class="minute disabled">0:10</span><span class="minute disabled">0:15</span><span class="minute disabled">0:20</span><span class="minute disabled">0:25</span><span class="minute disabled">0:30</span><span class="minute disabled">0:35</span><span class="minute disabled">0:40</span><span class="minute disabled">0:45</span><span class="minute disabled">0:50</span><span class="minute disabled">0:55</span></td></tr></tbody><tfoot><tr><th colspan="7" class="today" style="display: none;">Today</th></tr></tfoot></table></div><div class="datetimepicker-hours" style="display: none;"><table class=" table-condensed"><thead><tr><th class="prev" style="visibility: visible;"><i class="icon-arrow-left"></i></th><th colspan="5" class="switch">7 August 2017</th><th class="next" style="visibility: hidden;"><i class="icon-arrow-right"></i></th></tr></thead><tbody><tr><td colspan="7"><span class="hour active">0:00</span><span class="hour disabled">1:00</span><span class="hour disabled">2:00</span><span class="hour disabled">3:00</span><span class="hour disabled">4:00</span><span class="hour disabled">5:00</span><span class="hour disabled">6:00</span><span class="hour disabled">7:00</span><span class="hour disabled">8:00</span><span class="hour disabled">9:00</span><span class="hour disabled">10:00</span><span class="hour disabled">11:00</span><span class="hour disabled">12:00</span><span class="hour disabled">13:00</span><span class="hour disabled">14:00</span><span class="hour disabled">15:00</span><span class="hour disabled">16:00</span><span class="hour disabled">17:00</span><span class="hour disabled">18:00</span><span class="hour disabled">19:00</span><span class="hour disabled">20:00</span><span class="hour disabled">21:00</span><span class="hour disabled">22:00</span><span class="hour disabled">23:00</span></td></tr></tbody><tfoot><tr><th colspan="7" class="today" style="display: none;">Today</th></tr></tfoot></table></div><div class="datetimepicker-days" style="display: block;"><table class=" table-condensed"><thead><tr><th class="prev" style="visibility: visible;"><i class="icon-arrow-left"></i></th><th colspan="5" class="switch">August 2017</th><th class="next" style="visibility: hidden;"><i class="icon-arrow-right"></i></th></tr><tr><th class="dow">Su</th><th class="dow">Mo</th><th class="dow">Tu</th><th class="dow">We</th><th class="dow">Th</th><th class="dow">Fr</th><th class="dow">Sa</th></tr></thead><tbody><tr><td class="day old">30</td><td class="day old">31</td><td class="day">1</td><td class="day">2</td><td class="day">3</td><td class="day">4</td><td class="day">5</td></tr><tr><td class="day">6</td><td class="day active">7</td><td class="day disabled">8</td><td class="day disabled">9</td><td class="day disabled">10</td><td class="day disabled">11</td><td class="day disabled">12</td></tr><tr><td class="day disabled">13</td><td class="day disabled">14</td><td class="day disabled">15</td><td class="day disabled">16</td><td class="day disabled">17</td><td class="day disabled">18</td><td class="day disabled">19</td></tr><tr><td class="day disabled">20</td><td class="day disabled">21</td><td class="day disabled">22</td><td class="day disabled">23</td><td class="day disabled">24</td><td class="day disabled">25</td><td class="day disabled">26</td></tr><tr><td class="day disabled">27</td><td class="day disabled">28</td><td class="day disabled">29</td><td class="day disabled">30</td><td class="day disabled">31</td><td class="day new disabled">1</td><td class="day new disabled">2</td></tr><tr><td class="day new disabled">3</td><td class="day new disabled">4</td><td class="day new disabled">5</td><td class="day new disabled">6</td><td class="day new disabled">7</td><td class="day new disabled">8</td><td class="day new disabled">9</td></tr></tbody><tfoot><tr><th colspan="7" class="today" style="display: none;">Today</th></tr></tfoot></table></div><div class="datetimepicker-months" style="display: none;"><table class="table-condensed"><thead><tr><th class="prev" style="visibility: visible;"><i class="icon-arrow-left"></i></th><th colspan="5" class="switch">2017</th><th class="next" style="visibility: hidden;"><i class="icon-arrow-right"></i></th></tr></thead><tbody><tr><td colspan="7"><span class="month">Jan</span><span class="month">Feb</span><span class="month">Mar</span><span class="month">Apr</span><span class="month">May</span><span class="month">Jun</span><span class="month">Jul</span><span class="month active">Aug</span><span class="month disabled">Sep</span><span class="month disabled">Oct</span><span class="month disabled">Nov</span><span class="month disabled">Dec</span></td></tr></tbody><tfoot><tr><th colspan="7" class="today" style="display: none;">Today</th></tr></tfoot></table></div><div class="datetimepicker-years" style="display: none;"><table class="table-condensed"><thead><tr><th class="prev" style="visibility: visible;"><i class="icon-arrow-left"></i></th><th colspan="5" class="switch">2010-2019</th><th class="next" style="visibility: hidden;"><i class="icon-arrow-right"></i></th></tr></thead><tbody><tr><td colspan="7"><span class="year old">2009</span><span class="year">2010</span><span class="year">2011</span><span class="year">2012</span><span class="year">2013</span><span class="year">2014</span><span class="year">2015</span><span class="year">2016</span><span class="year active">2017</span><span class="year disabled">2018</span><span class="year disabled">2019</span><span class="year old disabled">2020</span></td></tr></tbody><tfoot><tr><th colspan="7" class="today" style="display: none;">Today</th></tr></tfoot></table></div></div><div class="datetimepicker datetimepicker-dropdown-bottom-left dropdown-menu" style="left: -162px;"><div class="datetimepicker-minutes" style="display: none;"><table class=" table-condensed"><thead><tr><th class="prev" style="visibility: visible;"><i class="icon-arrow-left"></i></th><th colspan="5" class="switch">7 August 2017</th><th class="next" style="visibility: hidden;"><i class="icon-arrow-right"></i></th></tr></thead><tbody><tr><td colspan="7"><span class="minute active">0:00</span><span class="minute disabled">0:05</span><span class="minute disabled">0:10</span><span class="minute disabled">0:15</span><span class="minute disabled">0:20</span><span class="minute disabled">0:25</span><span class="minute disabled">0:30</span><span class="minute disabled">0:35</span><span class="minute disabled">0:40</span><span class="minute disabled">0:45</span><span class="minute disabled">0:50</span><span class="minute disabled">0:55</span></td></tr></tbody><tfoot><tr><th colspan="7" class="today" style="display: none;">Today</th></tr></tfoot></table></div><div class="datetimepicker-hours" style="display: none;"><table class=" table-condensed"><thead><tr><th class="prev" style="visibility: visible;"><i class="icon-arrow-left"></i></th><th colspan="5" class="switch">7 August 2017</th><th class="next" style="visibility: hidden;"><i class="icon-arrow-right"></i></th></tr></thead><tbody><tr><td colspan="7"><span class="hour active">0:00</span><span class="hour disabled">1:00</span><span class="hour disabled">2:00</span><span class="hour disabled">3:00</span><span class="hour disabled">4:00</span><span class="hour disabled">5:00</span><span class="hour disabled">6:00</span><span class="hour disabled">7:00</span><span class="hour disabled">8:00</span><span class="hour disabled">9:00</span><span class="hour disabled">10:00</span><span class="hour disabled">11:00</span><span class="hour disabled">12:00</span><span class="hour disabled">13:00</span><span class="hour disabled">14:00</span><span class="hour disabled">15:00</span><span class="hour disabled">16:00</span><span class="hour disabled">17:00</span><span class="hour disabled">18:00</span><span class="hour disabled">19:00</span><span class="hour disabled">20:00</span><span class="hour disabled">21:00</span><span class="hour disabled">22:00</span><span class="hour disabled">23:00</span></td></tr></tbody><tfoot><tr><th colspan="7" class="today" style="display: none;">Today</th></tr></tfoot></table></div><div class="datetimepicker-days" style="display: block;"><table class=" table-condensed"><thead><tr><th class="prev" style="visibility: visible;"><i class="icon-arrow-left"></i></th><th colspan="5" class="switch">August 2017</th><th class="next" style="visibility: hidden;"><i class="icon-arrow-right"></i></th></tr><tr><th class="dow">Su</th><th class="dow">Mo</th><th class="dow">Tu</th><th class="dow">We</th><th class="dow">Th</th><th class="dow">Fr</th><th class="dow">Sa</th></tr></thead><tbody><tr><td class="day old">30</td><td class="day old">31</td><td class="day">1</td><td class="day">2</td><td class="day">3</td><td class="day">4</td><td class="day">5</td></tr><tr><td class="day">6</td><td class="day active">7</td><td class="day disabled">8</td><td class="day disabled">9</td><td class="day disabled">10</td><td class="day disabled">11</td><td class="day disabled">12</td></tr><tr><td class="day disabled">13</td><td class="day disabled">14</td><td class="day disabled">15</td><td class="day disabled">16</td><td class="day disabled">17</td><td class="day disabled">18</td><td class="day disabled">19</td></tr><tr><td class="day disabled">20</td><td class="day disabled">21</td><td class="day disabled">22</td><td class="day disabled">23</td><td class="day disabled">24</td><td class="day disabled">25</td><td class="day disabled">26</td></tr><tr><td class="day disabled">27</td><td class="day disabled">28</td><td class="day disabled">29</td><td class="day disabled">30</td><td class="day disabled">31</td><td class="day new disabled">1</td><td class="day new disabled">2</td></tr><tr><td class="day new disabled">3</td><td class="day new disabled">4</td><td class="day new disabled">5</td><td class="day new disabled">6</td><td class="day new disabled">7</td><td class="day new disabled">8</td><td class="day new disabled">9</td></tr></tbody><tfoot><tr><th colspan="7" class="today" style="display: none;">Today</th></tr></tfoot></table></div><div class="datetimepicker-months" style="display: none;"><table class="table-condensed"><thead><tr><th class="prev" style="visibility: visible;"><i class="icon-arrow-left"></i></th><th colspan="5" class="switch">2017</th><th class="next" style="visibility: hidden;"><i class="icon-arrow-right"></i></th></tr></thead><tbody><tr><td colspan="7"><span class="month">Jan</span><span class="month">Feb</span><span class="month">Mar</span><span class="month">Apr</span><span class="month">May</span><span class="month">Jun</span><span class="month">Jul</span><span class="month active">Aug</span><span class="month disabled">Sep</span><span class="month disabled">Oct</span><span class="month disabled">Nov</span><span class="month disabled">Dec</span></td></tr></tbody><tfoot><tr><th colspan="7" class="today" style="display: none;">Today</th></tr></tfoot></table></div><div class="datetimepicker-years" style="display: none;"><table class="table-condensed"><thead><tr><th class="prev" style="visibility: visible;"><i class="icon-arrow-left"></i></th><th colspan="5" class="switch">2010-2019</th><th class="next" style="visibility: hidden;"><i class="icon-arrow-right"></i></th></tr></thead><tbody><tr><td colspan="7"><span class="year old">2009</span><span class="year">2010</span><span class="year">2011</span><span class="year">2012</span><span class="year">2013</span><span class="year">2014</span><span class="year">2015</span><span class="year">2016</span><span class="year active">2017</span><span class="year disabled">2018</span><span class="year disabled">2019</span><span class="year old disabled">2020</span></td></tr></tbody><tfoot><tr><th colspan="7" class="today" style="display: none;">Today</th></tr></tfoot></table></div></div>
