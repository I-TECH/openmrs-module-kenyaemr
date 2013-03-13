Kenya EMR OpenMRS Module
========================
[![Build Status](https://travis-ci.org/I-TECH/openmrs-module-kenyaemr.png?branch=master)](https://travis-ci.org/I-TECH/openmrs-module-kenyaemr)

Overview
--------
Initial ITECH work on an OpenMRS-based EMR for the Kenya MoH, along with the [Kenya UI](https://github.com/I-TECH/openmrs-module-kenyaui)
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
