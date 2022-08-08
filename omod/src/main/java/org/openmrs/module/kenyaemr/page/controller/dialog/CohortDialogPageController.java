/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.page.controller.dialog;

import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportManager;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.DateOfEnrollmentArtCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.LastCD4ResultCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.LastViralLoadResultCalculation;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.ViralLoadAndLdlCalculation;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.SharedPage;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.indicator.dimension.CohortIndicatorAndDimensionResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.module.reportingcompatibility.service.ReportingCompatibilityService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controller for cohort dialog
 */
@SharedPage
public class CohortDialogPageController {

    public void controller(@RequestParam("request") ReportRequest reportRequest,
                           @RequestParam("dataset") String dataSetName,
                           @RequestParam("column") String columnName,
                           PageRequest pageRequest,
                           PageModel model,
                           UiUtils ui,
                           HttpServletResponse response,
                           @SpringBean ReportManager reportManager,
                           @SpringBean KenyaUiUtils kenyaUi,
                           @SpringBean ReportService reportService) {

        ReportDefinition definition = reportRequest.getReportDefinition().getParameterizable();
        ReportDescriptor report = reportManager.getReportDescriptor(definition);

        CoreUtils.checkAccess(report, kenyaUi.getCurrentApp(pageRequest));

        ReportData reportData = reportService.loadReportData(reportRequest);

        MapDataSet dataSet = (MapDataSet) reportData.getDataSets().get(dataSetName);

        DataSetColumn dataSetColumn = dataSet.getMetaData().getColumn(columnName);
        Object result = dataSet.getData(dataSetColumn);

        Cohort cohort = null;
        if (result instanceof CohortIndicatorAndDimensionResult) {
            CohortIndicatorAndDimensionResult cidr = (CohortIndicatorAndDimensionResult) dataSet.getData(dataSetColumn);
            cohort = cidr.getCohortIndicatorAndDimensionCohort();
        } else if (result instanceof Cohort) {
            cohort = (Cohort) result;
        }

        Set<Integer> cohortPatients = new HashSet<Integer>();
        if(cohort != null) {

            for (CohortMembership membership : cohort.getMemberships()) {
                cohortPatients.add(membership.getPatientId());

            }
        }

        List<Patient> patients = Context.getService(ReportingCompatibilityService.class).getPatients(cohortPatients);



        PatientCalculationService calculationService = Context.getService(PatientCalculationService.class);
        PatientCalculationContext calculationContext = calculationService.createCalculationContext();
        Date endDate = (Date) reportRequest.getReportDefinition().getParameterMappings().get("endDate");
        calculationContext.setNow(endDate);

        DateOfEnrollmentArtCalculation dateOfEnrollmentArtCalculation = new DateOfEnrollmentArtCalculation();
        CalculationResultMap enrollmentDates = dateOfEnrollmentArtCalculation.evaluate(cohortPatients, null, calculationContext);

        InitialArtStartDateCalculation initialArtStartDateCalculation = new InitialArtStartDateCalculation();
        CalculationResultMap artInitializationDates = initialArtStartDateCalculation.evaluate(cohortPatients, null, calculationContext);

        LastViralLoadResultCalculation lastVlResultCalculation = new LastViralLoadResultCalculation();
        CalculationResultMap lastVlResults = lastVlResultCalculation.evaluate(cohortPatients, null, calculationContext);

        AgeDataDefinition d = new AgeDataDefinition();
        d.setEffectiveDate(endDate);
        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(cohort);
        EvaluatedPersonData pd = null;
        try {
            pd = Context.getService(PersonDataService.class).evaluate(d, context);
        } catch (EvaluationException e) {
            e.printStackTrace();
        }

        /*LastCD4ResultCalculation lastCD4ResultCalculation = new LastCD4ResultCalculation();
        CalculationResultMap lastCD4Results = lastCD4ResultCalculation.evaluate(cohort.getMemberIds(), null, calculationContext);
*/

        model.addAttribute("column", dataSetColumn);
        model.addAttribute("reportRequest", reportRequest);
        model.addAttribute("dataSet", dataSetName);
        model.addAttribute("cohort", cohort);
        model.addAttribute("patients", ui.simplifyCollection(patients));
        model.addAttribute("enrollmentDates", enrollmentDates);
        model.addAttribute("artInitializationDates", artInitializationDates);
        model.addAttribute("lastVlResults", lastVlResults);
        model.addAttribute("ageAtReportingResults", pd);
        //model.addAttribute("lastCD4Results", lastCD4Results);
    }
}