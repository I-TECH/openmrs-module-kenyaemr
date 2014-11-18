/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

/**
 * Configures logging for profiling of reports
 */

import org.apache.log4j.*;
LogManager.getLogger("org.openmrs.module.reporting.evaluation.EvaluationProfiler").setLevel(Level.TRACE);
LogManager.getLogger("org.openmrs.api").setLevel(Level.WARN); // Switch off general service call logging

"Done"