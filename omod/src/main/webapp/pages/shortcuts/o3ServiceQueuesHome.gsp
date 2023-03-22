<%
    ui.decorateWith("kenyaemr", "standardPage")
%>

<script type="text/javascript">
    //On ready
    jq = jQuery;
    jq(function () {
        // Redirect to O3
        var getUrl = window.location;
        var baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
        baseUrl = baseUrl + "/spa/outpatient/home"
        var spaUrl = new URL(baseUrl);
        window.location.replace(spaUrl);
    });
</script>