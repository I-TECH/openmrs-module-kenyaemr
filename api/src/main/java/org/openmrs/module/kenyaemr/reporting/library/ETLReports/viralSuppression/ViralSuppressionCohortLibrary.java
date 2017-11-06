package org.openmrs.module.kenyaemr.reporting.library.ETLReports.viralSuppression;

import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.springframework.stereotype.Component;

/**
 * Library of cohort definitions for viral suppression
 */
@Component
public class ViralSuppressionCohortLibrary {
    public CohortDefinition suppressed(){
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery = "select c.patient_id\n" +
                "from kenyaemr_etl.etl_current_in_care c\n" +
                "left outer join \n" +
                "(\n" +
                "select \n" +
                "patient_id, \n" +
                "visit_date,\n" +
                "if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\", \"\")) as vl_result\n" +
                "from kenyaemr_etl.etl_laboratory_extract\n" +
                "where lab_test in (1305, 856)\n" +
                ") vl_result on vl_result.patient_id = c.patient_id\n" +
                "group by c.patient_id\n" +
                "having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)=\"LDL\" or mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11)<1000\n " +
                ";";
        cd.setName("suppressed");
        cd.setQuery(sqlQuery);
        cd.setDescription("Suppressed");

        return cd;
    }

    public  CohortDefinition unsuppressed() {
        SqlCohortDefinition cd = new SqlCohortDefinition();
        String sqlQuery=" select c.patient_id\n" +
                "from kenyaemr_etl.etl_current_in_care c\n" +
                "left outer join \n" +
                "(\n" +
                "select \n" +
                "patient_id, \n" +
                "visit_date,\n" +
                "if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\", \"\")) as vl_result\n" +
                "from kenyaemr_etl.etl_laboratory_extract\n" +
                "where lab_test in (1305, 856)\n" +
                ") vl_result on vl_result.patient_id = c.patient_id\n" +
                "group by c.patient_id\n" +
                "having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11) >= 1000 ;" ;


        cd.setName("unsuppressed");
        cd.setQuery(sqlQuery);
        cd.setDescription("Unsuppressed");

        return cd;
    }

    public  CohortDefinition noVLResults() {
        String sqlQuery="select c.patient_id\n" +
                "from kenyaemr_etl.etl_current_in_care c\n" +
                "left outer join \n" +
                "(\n" +
                "select \n" +
                "patient_id, \n" +
                "visit_date,\n" +
                "if(lab_test = 856, test_result, if(lab_test=1305 and test_result = 1302, \"LDL\", \"\")) as vl_result\n" +
                "from kenyaemr_etl.etl_laboratory_extract\n" +
                "where lab_test in (1305, 856)\n" +
                ") vl_result on vl_result.patient_id = c.patient_id\n" +
                "group by c.patient_id\n" +
                "having mid(max(concat(vl_result.visit_date, vl_result.vl_result)), 11) is null;";
        SqlCohortDefinition cd = new SqlCohortDefinition();
        cd.setName("noVLResults");
        cd.setQuery(sqlQuery);
        cd.setDescription("No VL Results");
        return cd;
    }

}
