KenyaEMR Distribution: OpenMRS Module
=====================================
<a href="http://ci.kenyaemr.org/viewType.html?buildTypeId=bt2"><img src="http://ci.kenyaemr.org/app/rest/builds/buildType:bt2/statusIcon"/></a>

Overview
--------
Initial ITECH work on an OpenMRS-based EMR for the Kenya MoH, along with the [KenyaUI](https://github.com/I-TECH/openmrs-module-kenyaui)
module.

Requirements
------------
Module requires OpenMRS 1.9.3. All other required modules are included in the distribution zip.

Installation
------------
Build the distro project to create a zip archive of all required modules. This should then be extracted into your
OpenMRS modules repository folder.

```bash
mvn -Pdistribution clean package
```

Accreditation
-------------
* Highcharts graphing library by Highsoft used under Creative Commons Licence 3.0 (http://www.highcharts.com/)
* Pretty Office Icons used with permission from CustomIconDesign (http://www.customicondesign.com)
