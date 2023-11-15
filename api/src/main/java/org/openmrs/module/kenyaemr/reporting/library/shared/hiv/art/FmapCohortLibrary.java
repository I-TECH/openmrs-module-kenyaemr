/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.library.shared.hiv.art;

import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class FmapCohortLibrary {

    /**
     * FIRST REVIEW: 12 Months Cohort composition
     */

    /**
     * Number in HEI Cohort 12 months
     * @return the indicator
     */
    public CohortDefinition AdultFirstTLD() {
        String sqlQuery = "SELECT he.patient_id FROM kenyaemr_etl.etl_drug_order he\n" +
                "WHERE DATE(he.visit_date) between date_sub(date(:startDate),interval 1 YEAR) and  date_sub(date(:endDate),interval 1 YEAR) and drug_name = 'TENOFOVIR DISOPROXIL FUMARATE+LAMIVUDINE+DOLUTEGRAVIR';";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("adultFirstLineTLD");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("TDF+3TC+DTG");
        return cd;
    }

    public CohortDefinition AdultFirstTLE() {
        String sqlQuery = "SELECT he.patient_id FROM kenyaemr_etl.etl_drug_order he\n" +
                "WHERE DATE(he.visit_date) between date_sub(date(:startDate),interval 1 YEAR) and  date_sub(date(:endDate),interval 1 YEAR) and drug_name = 'TENOFOVIR DISOPROXIL FUMARATE+LAMIVUDINE+EFAVIRENZ';";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("adultFirstLineTLE");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("TDF+3TC+EFV");
        return cd;
    }

    public CohortDefinition GetPatientOnSpecificRegimenandRegimenLine(String regimenName,String regimenLine,String ageGroup){
        String sqlQuery = "select t.patient_id\n" +
                "from(\n" +
                "select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "case when timestampdiff(YEAR,date(p.DOB),max(e.visit_date)) >= 15 then \"adult\" else \"child\" end as agegroup,\n"+
                "greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "d.patient_id as disc_patient,\n" +
                "d.effective_disc_date as effective_disc_date,\n" +
                "max(d.visit_date) as date_discontinued,\n" +
                "de.patient_id as started_on_drugs\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "left outer JOIN\n" +
                "(select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "group by patient_id\n" +
                ") d on d.patient_id = fup.patient_id\n" +
                "where fup.visit_date <= date(:endDate)\n" +
                "group by patient_id\n" +
                "having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "(\n" +
                "((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                ")\n" +
                ")\n" +
                ") t left join(\n" +
                "select * from(\n" +
                "select\n" +
                "e.uuid,\n" +
                "e.patient_id,\n" +
                "e.encounter_datetime,\n" +
                "-- e.encounter_datetime,\n" +
                "e.creator,\n" +
                "e.encounter_id,\n" +
                "                                    o.value_coded,\n" +
                "max(if(o.concept_id=1255,'HIV',if(o.concept_id=1268, 'TB', null))) as program,\n" +
                "max(if(o.concept_id=1193,(\n" +
                "case o.value_coded\n" +
                "when 162565 then \"3TC/NVP/TDF\"\n" +
                "when 164505 then \"TDF/3TC/EFV\"\n" +
                "when 1652 then \"AZT/3TC/NVP\"\n" +
                "when 160124 then \"AZT/3TC/EFV\"\n" +
                "when 792 then \"D4T/3TC/NVP\"\n" +
                "when 160104 then \"D4T/3TC/EFV\"\n" +
                "when 164971 then \"TDF/3TC/AZT\"\n" +
                "when 165357 then \"ABC/3TC/ATV/r\"\n" +
                "when 164968 then \"AZT/3TC/DTG\"\n" +
                "when 164969 then \"TDF/3TC/DTG\"\n" +
                "when 164970 then \"ABC/3TC/DTG\"\n" +
                "when 162561 then \"AZT/3TC/LPV/r\"\n" +
                "when 164511 then \"AZT/3TC/ATV/r\"\n" +
                "when 162201 then \"TDF/3TC/LPV/r\"\n" +
                "when 1067 then \"Unknown\"\n" +
                "when 164512 then \"TDF/3TC/ATV/r\"\n" +
                "when 162560 then \"D4T/3TC/LPV/r\"\n" +
                "when 164972 then \"AZT/TDF/3TC/LPV/r\"\n" +
                "when 164973 then \"ETR/RAL/DRV/RTV\"\n" +
                "when 164974 then \"ETR/TDF/3TC/LPV/r\"\n" +
                "when 162200 then \"ABC/3TC/LPV/r\"\n" +
                "when 162199 then \"ABC/3TC/NVP\"\n" +
                "when 162563 then \"ABC/3TC/EFV\"\n" +
                "when 817 then \"AZT/3TC/ABC\"\n" +
                "when 164975 then \"D4T/3TC/ABC\"\n" +
                "when 162562 then \"TDF/ABC/LPV/r\"\n" +
                "when 162559 then \"ABC/DDI/LPV/r\"\n" +
                "when 164976 then \"ABC/TDF/3TC/LPV/r\"\n" +
                "when 165375 then \"RAL+3TC+DRV+RTV\"\n" +
                "when 165376 then \"RAL+3TC+DRV+RTV+AZT\"\n" +
                "when 165377 then \"RAL+3TC+DRV+RTV+ABC\"\n" +
                "when 165378 then \"ETV+3TC+DRV+RTV\"\n" +
                "when 165379 then \"RAL+3TC+DRV+RTV+TDF\"\n" +
                "when 165369 then \"TDF+3TC+DTG+DRV/r\"\n" +
                "when 165370 then \"TDF+3TC+RAL+DRV/r\"\n" +
                "when 165371 then \"TDF+3TC+DTG+EFV+DRV/r\"\n" +
                "when 165372 then \"ABC+3TC+RAL\"\n" +
                "when 165373 then \"AZT+3TC+RAL+DRV/r\"\n" +
                "when 165374 then \"ABC+3TC+RAL+DRV/r\"\n" +
                "when 1675 then \"RHZE\"\n" +
                "when 768 then \"RHZ\"\n" +
                "when 1674 then \"SRHZE\"\n" +
                "when 164978 then \"RfbHZE\"\n" +
                "when 164979 then \"RfbHZ\"\n" +
                "when 164980 then \"SRfbHZE\"\n" +
                "when 84360 then \"S (1 gm vial)\"\n" +
                "when 75948 then \"E\"\n" +
                "when 1194 then \"RH\"\n" +
                "when 159851 then \"RHE\"\n" +
                "when 1108 then \"EH\"\n" +
                "else o.value_coded\n" +
                "end ),null)) as regimen,\n" +
                "max(if(o.concept_id=1193,(\n" +
                "case o.value_coded\n" +
                "when 162565 then \"3TC+NVP+TDF\"\n" +
                "when 164505 then \"TDF+3TC+EFV\"\n" +
                "when 1652 then \"AZT+3TC+NVP\"\n" +
                "when 160124 then \"AZT+3TC+EFV\"\n" +
                "when 792 then \"D4T+3TC+NVP\"\n" +
                "when 160104 then \"D4T+3TC+EFV\"\n" +
                "when 164971 then \"TDF+3TC+AZT\"\n" +
                "when 164968 then \"AZT+3TC+DTG\"\n" +
                "when 1067 then \"Unknown\"\n" +
                "when 164969 then \"TDF+3TC+DTG\"\n" +
                "when 164970 then \"ABC+3TC+DTG\"\n" +
                "when 162561 then \"AZT+3TC+LPV/r\"\n" +
                "when 164511 then \"AZT+3TC+ATV/r\"\n" +
                "when 162201 then \"TDF+3TC+LPV/r\"\n" +
                "when 165357 then \"ABC/3TC/ATV/r\"\n" +
                "when 164512 then \"TDF+3TC+ATV/r\"\n" +
                "when 162560 then \"D4T+3TC+LPV/r\"\n" +
                "when 164972 then \"AZT+TDF+3TC+LPV/r\"\n" +
                "when 164973 then \"ETR+RAL+DRV+RTV\"\n" +
                "when 164974 then \"ETR+TDF+3TC+LPV/r\"\n" +
                "when 162200 then \"ABC+3TC+LPV/r\"\n" +
                "when 162199 then \"ABC+3TC+NVP\"\n" +
                "when 162563 then \"ABC+3TC+EFV\"\n" +
                "when 817 then \"AZT+3TC+ABC\"\n" +
                "when 164975 then \"D4T+3TC+ABC\"\n" +
                "when 162562 then \"TDF+ABC+LPV/r\"\n" +
                "when 162559 then \"ABC+DDI+LPV/r\"\n" +
                "when 164976 then \"ABC+TDF+3TC+LPV/r\"\n" +
                "when 165375 then \"RAL+3TC+DRV+RTV\"\n" +
                "when 165376 then \"RAL+3TC+DRV+RTV+AZT\"\n" +
                "when 165377 then \"RAL+3TC+DRV+RTV+ABC\"\n" +
                "when 165378 then \"ETV+3TC+DRV+RTV\"\n" +
                "when 165379 then \"RAL+3TC+DRV+RTV+TDF\"\n" +
                "when 165369 then \"TDF+3TC+DTG+DRV/r\"\n" +
                "when 165370 then \"TDF+3TC+RAL+DRV/r\"\n" +
                "when 165371 then \"TDF+3TC+DTG+EFV+DRV/r\"\n" +
                "when 165372 then \"ABC+3TC+RAL\"\n" +
                "when 165373 then \"AZT+3TC+RAL+DRV/r\"\n" +
                "when 165374 then \"ABC+3TC+RAL+DRV/r\"\n" +
                "when 1675 then \"RHZE\"\n" +
                "when 768 then \"RHZ\"\n" +
                "when 1674 then \"SRHZE\"\n" +
                "when 164978 then \"RfbHZE\"\n" +
                "when 164979 then \"RfbHZ\"\n" +
                "when 164980 then \"SRfbHZE\"\n" +
                "when 84360 then \"S (1 gm vial)\"\n" +
                "when 75948 then \"E\"\n" +
                "when 1194 then \"RH\"\n" +
                "when 159851 then \"RHE\"\n" +
                "when 1108 then \"EH\"\n" +
                "else \"\"\n" +
                "end ),null)) as regimen_name,\n" +
                "max(if(o.concept_id=1193,(\n" +
                "case o.value_coded\n" +
                "when 162565 then \"First line\"\n" +
                "when 164505 then \"First line\"\n" +
                "when 1652 then \"First line\"\n" +
                "when 160124 then \"First line\"\n" +
                "when 792 then \"First line\"\n" +
                "when 160104 then \"First line\"\n" +
                "when 164971 then \"First line\"\n" +
                "when 164968 then \"First line\"\n" +
                "when 164969 then \"First line\"\n" +
                "when 164970 then \"First line\"\n" +
                "when 162561 then \"First line\"\n" +
                "when 164511 then \"First line\"\n" +
                "when 164512 then \"First line\"\n" +
                "when 162201 then \"First line\"\n" +
                "when 162561 then \"Second line\"\n" +
                "when 164511 then \"Second line\"\n" +
                "when 162201 then \"Second line\"\n" +
                "when 164512 then \"Second line\"\n" +
                "when 162560 then \"Second line\"\n" +
                "when 164972 then \"Second line\"\n" +
                "when 164973 then \"Second line\"\n" +
                "when 164974 then \"Second line\"\n" +
                "when 165357 then \"Second line\"\n" +
                "when 164968 then \"Second line\"\n" +
                "when 164969 then \"Second line\"\n" +
                "when 164970 then \"Second line\"\n" +
                "when 165375 then \"Third line\"\n" +
                "when 165376 then \"Third line\"\n" +
                "when 165379 then \"Third line\"\n" +
                "when 165378 then \"Third line\"\n" +
                "when 165369 then \"Third line\"\n" +
                "when 165370 then \"Third line\"\n" +
                "when 165371 then \"Third line\"\n" +
                "when 162200 then \"First line\"\n" +
                "when 162199 then \"First line\"\n" +
                "when 162563 then \"First line\"\n" +
                "when 817 then \"First line\"\n" +
                "when 164975 then \"First line\"\n" +
                "when 162562 then \"First line\"\n" +
                "when 162559 then \"First line\"\n" +
                "when 164976 then \"First line\"\n" +
                "when 165372 then \"First line\"\n" +
                "when 162561 then \"Second line\"\n" +
                "when 164511 then \"Second line\"\n" +
                "when 162200 then \"Second line\"\n" +
                "when 165357 then \"Second line\"\n" +
                "when 165373 then \"Second line\"\n" +
                "when 165374 then \"Second line\"\n" +
                "when 165375 then \"Third line\"\n" +
                "when 165376 then \"Third line\"\n" +
                "when 165377 then \"Third line\"\n" +
                "when 165378 then \"Third line\"\n" +
                "when 165373 then \"Third line\"\n" +
                "when 165374 then \"Third line\"\n" +
                "when 1675 then \"Adult intensive\"\n" +
                "when 768 then \"Adult intensive\"\n" +
                "when 1674 then \"Adult intensive\"\n" +
                "when 164978 then \"Adult intensive\"\n" +
                "when 164979 then \"Adult intensive\"\n" +
                "when 164980 then \"Adult intensive\"\n" +
                "when 84360 then \"Adult intensive\"\n" +
                "when 75948 then \"Child intensive\"\n" +
                "when 1194 then \"Child intensive\"\n" +
                "when 159851 then \"Adult continuation\"\n" +
                "when 1108 then \"Adult continuation\"\n" +
                "else \"\"\n" +
                "end ),null)) as regimen_line,\n" +
                "max(if(o.concept_id=1191,(case o.value_datetime when NULL then 0 else 1 end),null)) as discontinued,\n" +
                "null as regimen_discontinued,\n" +
                "max(if(o.concept_id=1191,o.value_datetime,null)) as date_discontinued,\n" +
                "max(if(o.concept_id=1252,o.value_coded,null)) as reason_discontinued,\n" +
                "max(if(o.concept_id=5622,o.value_text,null)) as reason_discontinued_other\n" +
                "\n" +
                "from  openmrs.encounter e\n" +
                "inner join  openmrs.person p on p.person_id=e.patient_id and p.voided=0\n" +
                "inner join  openmrs.obs o on e.encounter_id = o.encounter_id and o.voided =0\n" +
                "and o.concept_id in(1193,1252,5622,1191,1255,1268)\n" +
                "inner join\n" +
                "(\n" +
                "select encounter_type, uuid,name from  openmrs.form where\n" +
                "uuid in('da687480-e197-11e8-9f32-f2801f1b9fd1')\n" +
                ") f on f.encounter_type=e.encounter_type\n" +
                "group by e.encounter_id\n" +
                ")regimendata where regimen not in ('RHZE','RHZ','SRHZE','RfbHZE','RfbHZ','SRfbHZE','S (1 gm vial)','E','RHE','EH')\n" +
                "and encounter_datetime <= :endDate and regimen = ':regimenName' and regimen_line = ':regimenLine'\n" +
                ")regimens on t.patient_id = regimens.patient_id where regimens.patient_id is not null and agegroup = ':ageGroup'";
        sqlQuery = sqlQuery.replaceAll(":regimenName", regimenName);
        sqlQuery = sqlQuery.replaceAll(":regimenLine", regimenLine);
        sqlQuery = sqlQuery.replaceAll(":ageGroup", ageGroup);
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Regimens");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Regimens");
        return cd;
    }

    public CohortDefinition GetPatientOnAnyOtherRegimenandRegimenLine(String regimenName,String regimenLine,String ageGroup){
        String sqlQuery = "select t.patient_id\n" +
                "from(\n" +
                "select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n" +
                "case when timestampdiff(YEAR,date(p.DOB),max(e.visit_date)) >= 15 then \"adult\" else \"child\" end as agegroup,\n"+
                "greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n" +
                "greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n" +
                "greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n" +
                "d.patient_id as disc_patient,\n" +
                "d.effective_disc_date as effective_disc_date,\n" +
                "max(d.visit_date) as date_discontinued,\n" +
                "de.patient_id as started_on_drugs\n" +
                "from kenyaemr_etl.etl_patient_hiv_followup fup\n" +
                "join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n" +
                "join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n" +
                "left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n" +
                "left outer JOIN\n" +
                "(select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n" +
                "where date(visit_date) <= date(:endDate) and program_name='HIV'\n" +
                "group by patient_id\n" +
                ") d on d.patient_id = fup.patient_id\n" +
                "where fup.visit_date <= date(:endDate)\n" +
                "group by patient_id\n" +
                "having (started_on_drugs is not null and started_on_drugs <> '') and (\n" +
                "(\n" +
                "((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n" +
                "and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n" +
                ")\n" +
                ")\n" +
                ") t left join(\n" +
                "select * from(\n" +
                "select\n" +
                "e.uuid,\n" +
                "e.patient_id,\n" +
                "e.encounter_datetime,\n" +
                "-- e.encounter_datetime,\n" +
                "e.creator,\n" +
                "e.encounter_id,\n" +
                "                                    o.value_coded,\n" +
                "max(if(o.concept_id=1255,'HIV',if(o.concept_id=1268, 'TB', null))) as program,\n" +
                "max(if(o.concept_id=1193,(\n" +
                "case o.value_coded\n" +
                "when 162565 then \"3TC/NVP/TDF\"\n" +
                "when 164505 then \"TDF/3TC/EFV\"\n" +
                "when 1652 then \"AZT/3TC/NVP\"\n" +
                "when 160124 then \"AZT/3TC/EFV\"\n" +
                "when 792 then \"D4T/3TC/NVP\"\n" +
                "when 160104 then \"D4T/3TC/EFV\"\n" +
                "when 164971 then \"TDF/3TC/AZT\"\n" +
                "when 165357 then \"ABC/3TC/ATV/r\"\n" +
                "when 164968 then \"AZT/3TC/DTG\"\n" +
                "when 164969 then \"TDF/3TC/DTG\"\n" +
                "when 164970 then \"ABC/3TC/DTG\"\n" +
                "when 162561 then \"AZT/3TC/LPV/r\"\n" +
                "when 164511 then \"AZT/3TC/ATV/r\"\n" +
                "when 162201 then \"TDF/3TC/LPV/r\"\n" +
                "when 1067 then \"Unknown\"\n" +
                "when 164512 then \"TDF/3TC/ATV/r\"\n" +
                "when 162560 then \"D4T/3TC/LPV/r\"\n" +
                "when 164972 then \"AZT/TDF/3TC/LPV/r\"\n" +
                "when 164973 then \"ETR/RAL/DRV/RTV\"\n" +
                "when 164974 then \"ETR/TDF/3TC/LPV/r\"\n" +
                "when 162200 then \"ABC/3TC/LPV/r\"\n" +
                "when 162199 then \"ABC/3TC/NVP\"\n" +
                "when 162563 then \"ABC/3TC/EFV\"\n" +
                "when 817 then \"AZT/3TC/ABC\"\n" +
                "when 164975 then \"D4T/3TC/ABC\"\n" +
                "when 162562 then \"TDF/ABC/LPV/r\"\n" +
                "when 162559 then \"ABC/DDI/LPV/r\"\n" +
                "when 164976 then \"ABC/TDF/3TC/LPV/r\"\n" +
                "when 165375 then \"RAL+3TC+DRV+RTV\"\n" +
                "when 165376 then \"RAL+3TC+DRV+RTV+AZT\"\n" +
                "when 165377 then \"RAL+3TC+DRV+RTV+ABC\"\n" +
                "when 165378 then \"ETV+3TC+DRV+RTV\"\n" +
                "when 165379 then \"RAL+3TC+DRV+RTV+TDF\"\n" +
                "when 165369 then \"TDF+3TC+DTG+DRV/r\"\n" +
                "when 165370 then \"TDF+3TC+RAL+DRV/r\"\n" +
                "when 165371 then \"TDF+3TC+DTG+EFV+DRV/r\"\n" +
                "when 165372 then \"ABC+3TC+RAL\"\n" +
                "when 165373 then \"AZT+3TC+RAL+DRV/r\"\n" +
                "when 165374 then \"ABC+3TC+RAL+DRV/r\"\n" +
                "when 1675 then \"RHZE\"\n" +
                "when 768 then \"RHZ\"\n" +
                "when 1674 then \"SRHZE\"\n" +
                "when 164978 then \"RfbHZE\"\n" +
                "when 164979 then \"RfbHZ\"\n" +
                "when 164980 then \"SRfbHZE\"\n" +
                "when 84360 then \"S (1 gm vial)\"\n" +
                "when 75948 then \"E\"\n" +
                "when 1194 then \"RH\"\n" +
                "when 159851 then \"RHE\"\n" +
                "when 1108 then \"EH\"\n" +
                "else o.value_coded\n" +
                "end ),null)) as regimen,\n" +
                "max(if(o.concept_id=1193,(\n" +
                "case o.value_coded\n" +
                "when 162565 then \"3TC+NVP+TDF\"\n" +
                "when 164505 then \"TDF+3TC+EFV\"\n" +
                "when 1652 then \"AZT+3TC+NVP\"\n" +
                "when 160124 then \"AZT+3TC+EFV\"\n" +
                "when 792 then \"D4T+3TC+NVP\"\n" +
                "when 160104 then \"D4T+3TC+EFV\"\n" +
                "when 164971 then \"TDF+3TC+AZT\"\n" +
                "when 164968 then \"AZT+3TC+DTG\"\n" +
                "when 1067 then \"Unknown\"\n" +
                "when 164969 then \"TDF+3TC+DTG\"\n" +
                "when 164970 then \"ABC+3TC+DTG\"\n" +
                "when 162561 then \"AZT+3TC+LPV/r\"\n" +
                "when 164511 then \"AZT+3TC+ATV/r\"\n" +
                "when 162201 then \"TDF+3TC+LPV/r\"\n" +
                "when 165357 then \"ABC/3TC/ATV/r\"\n" +
                "when 164512 then \"TDF+3TC+ATV/r\"\n" +
                "when 162560 then \"D4T+3TC+LPV/r\"\n" +
                "when 164972 then \"AZT+TDF+3TC+LPV/r\"\n" +
                "when 164973 then \"ETR+RAL+DRV+RTV\"\n" +
                "when 164974 then \"ETR+TDF+3TC+LPV/r\"\n" +
                "when 162200 then \"ABC+3TC+LPV/r\"\n" +
                "when 162199 then \"ABC+3TC+NVP\"\n" +
                "when 162563 then \"ABC+3TC+EFV\"\n" +
                "when 817 then \"AZT+3TC+ABC\"\n" +
                "when 164975 then \"D4T+3TC+ABC\"\n" +
                "when 162562 then \"TDF+ABC+LPV/r\"\n" +
                "when 162559 then \"ABC+DDI+LPV/r\"\n" +
                "when 164976 then \"ABC+TDF+3TC+LPV/r\"\n" +
                "when 165375 then \"RAL+3TC+DRV+RTV\"\n" +
                "when 165376 then \"RAL+3TC+DRV+RTV+AZT\"\n" +
                "when 165377 then \"RAL+3TC+DRV+RTV+ABC\"\n" +
                "when 165378 then \"ETV+3TC+DRV+RTV\"\n" +
                "when 165379 then \"RAL+3TC+DRV+RTV+TDF\"\n" +
                "when 165369 then \"TDF+3TC+DTG+DRV/r\"\n" +
                "when 165370 then \"TDF+3TC+RAL+DRV/r\"\n" +
                "when 165371 then \"TDF+3TC+DTG+EFV+DRV/r\"\n" +
                "when 165372 then \"ABC+3TC+RAL\"\n" +
                "when 165373 then \"AZT+3TC+RAL+DRV/r\"\n" +
                "when 165374 then \"ABC+3TC+RAL+DRV/r\"\n" +
                "when 1675 then \"RHZE\"\n" +
                "when 768 then \"RHZ\"\n" +
                "when 1674 then \"SRHZE\"\n" +
                "when 164978 then \"RfbHZE\"\n" +
                "when 164979 then \"RfbHZ\"\n" +
                "when 164980 then \"SRfbHZE\"\n" +
                "when 84360 then \"S (1 gm vial)\"\n" +
                "when 75948 then \"E\"\n" +
                "when 1194 then \"RH\"\n" +
                "when 159851 then \"RHE\"\n" +
                "when 1108 then \"EH\"\n" +
                "else \"\"\n" +
                "end ),null)) as regimen_name,\n" +
                "max(if(o.concept_id=1193,(\n" +
                "case o.value_coded\n" +
                "when 162565 then \"First line\"\n" +
                "when 164505 then \"First line\"\n" +
                "when 1652 then \"First line\"\n" +
                "when 160124 then \"First line\"\n" +
                "when 792 then \"First line\"\n" +
                "when 160104 then \"First line\"\n" +
                "when 164971 then \"First line\"\n" +
                "when 164968 then \"First line\"\n" +
                "when 164969 then \"First line\"\n" +
                "when 164970 then \"First line\"\n" +
                "when 162561 then \"First line\"\n" +
                "when 164511 then \"First line\"\n" +
                "when 164512 then \"First line\"\n" +
                "when 162201 then \"First line\"\n" +
                "when 162561 then \"Second line\"\n" +
                "when 164511 then \"Second line\"\n" +
                "when 162201 then \"Second line\"\n" +
                "when 164512 then \"Second line\"\n" +
                "when 162560 then \"Second line\"\n" +
                "when 164972 then \"Second line\"\n" +
                "when 164973 then \"Second line\"\n" +
                "when 164974 then \"Second line\"\n" +
                "when 165357 then \"Second line\"\n" +
                "when 164968 then \"Second line\"\n" +
                "when 164969 then \"Second line\"\n" +
                "when 164970 then \"Second line\"\n" +
                "when 165375 then \"Third line\"\n" +
                "when 165376 then \"Third line\"\n" +
                "when 165379 then \"Third line\"\n" +
                "when 165378 then \"Third line\"\n" +
                "when 165369 then \"Third line\"\n" +
                "when 165370 then \"Third line\"\n" +
                "when 165371 then \"Third line\"\n" +
                "when 162200 then \"First line\"\n" +
                "when 162199 then \"First line\"\n" +
                "when 162563 then \"First line\"\n" +
                "when 817 then \"First line\"\n" +
                "when 164975 then \"First line\"\n" +
                "when 162562 then \"First line\"\n" +
                "when 162559 then \"First line\"\n" +
                "when 164976 then \"First line\"\n" +
                "when 165372 then \"First line\"\n" +
                "when 162561 then \"Second line\"\n" +
                "when 164511 then \"Second line\"\n" +
                "when 162200 then \"Second line\"\n" +
                "when 165357 then \"Second line\"\n" +
                "when 165373 then \"Second line\"\n" +
                "when 165374 then \"Second line\"\n" +
                "when 165375 then \"Third line\"\n" +
                "when 165376 then \"Third line\"\n" +
                "when 165377 then \"Third line\"\n" +
                "when 165378 then \"Third line\"\n" +
                "when 165373 then \"Third line\"\n" +
                "when 165374 then \"Third line\"\n" +
                "when 1675 then \"Adult intensive\"\n" +
                "when 768 then \"Adult intensive\"\n" +
                "when 1674 then \"Adult intensive\"\n" +
                "when 164978 then \"Adult intensive\"\n" +
                "when 164979 then \"Adult intensive\"\n" +
                "when 164980 then \"Adult intensive\"\n" +
                "when 84360 then \"Adult intensive\"\n" +
                "when 75948 then \"Child intensive\"\n" +
                "when 1194 then \"Child intensive\"\n" +
                "when 159851 then \"Adult continuation\"\n" +
                "when 1108 then \"Adult continuation\"\n" +
                "else \"\"\n" +
                "end ),null)) as regimen_line,\n" +
                "max(if(o.concept_id=1191,(case o.value_datetime when NULL then 0 else 1 end),null)) as discontinued,\n" +
                "null as regimen_discontinued,\n" +
                "max(if(o.concept_id=1191,o.value_datetime,null)) as date_discontinued,\n" +
                "max(if(o.concept_id=1252,o.value_coded,null)) as reason_discontinued,\n" +
                "max(if(o.concept_id=5622,o.value_text,null)) as reason_discontinued_other\n" +
                "\n" +
                "from  openmrs.encounter e\n" +
                "inner join  openmrs.person p on p.person_id=e.patient_id and p.voided=0\n" +
                "inner join  openmrs.obs o on e.encounter_id = o.encounter_id and o.voided =0\n" +
                "and o.concept_id in(1193,1252,5622,1191,1255,1268)\n" +
                "inner join\n" +
                "(\n" +
                "select encounter_type, uuid,name from  openmrs.form where\n" +
                "uuid in('da687480-e197-11e8-9f32-f2801f1b9fd1')\n" +
                ") f on f.encounter_type=e.encounter_type\n" +
                "group by e.encounter_id\n" +
                ")regimendata where regimen not in ('RHZE','RHZ','SRHZE','RfbHZE','RfbHZ','SRfbHZE','S (1 gm vial)','E','RHE','EH')\n" +
                "and encounter_datetime <= :endDate and regimen NOT IN ('AZT/3TC/NVP','AZT/3TC/EFV','AZT/3TC/DTG','AZT/3TC/LPV/r','AZT/3TC/ATV/R','TDF/3TC/NVP','TDF/3TC/EFV','TDF/3TC/ATV/r','TDF/3TC/DTG','TDF/3TC/LPV/r','ABC/3TC/NVP','ABC/3TC/EFV','ABC/3TC/DTG','ABC/3TC/LPV/r','ABC/3TC/ATV/r') and regimen_line = ':regimenLine'\n" +
                ")regimens on t.patient_id = regimens.patient_id where regimens.patient_id is not null and agegroup = ':ageGroup'";
        sqlQuery = sqlQuery.replaceAll(":regimenName", regimenName);
        sqlQuery = sqlQuery.replaceAll(":regimenLine", regimenLine);
        sqlQuery = sqlQuery.replaceAll(":ageGroup", ageGroup);
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("Regimens");
        cd.setQuery(sqlQuery);
        cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        cd.addParameter(new Parameter("endDate", "End Date", Date.class));
        cd.setDescription("Regimens");
        return cd;
    }
}