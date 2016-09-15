package org.openmrs.module.kenyaemr.page.controller.clinician;

import org.openmrs.Patient;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.EmrWebConstants;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * Created by codehub on 10/22/15.
 */
@AppPage(EmrConstants.APP_CLINICIAN)
public class ClinicianSearchSeenPageController {
    public String controller(UiUtils ui,
                             @RequestParam(required = false, value = "seenDate") Date seenDate,
                             PageModel model) {

        Patient patient = (Patient) model.getAttribute(EmrWebConstants.MODEL_ATTR_CURRENT_PATIENT);

        // Get the date for schedule view
        if (seenDate == null) {
            seenDate = new Date();
        }
        seenDate = DateUtil.getStartOfDay(seenDate);
        model.addAttribute("seenDate", seenDate);

        if (patient != null) {
            return "redirect:" + ui.pageLink(EmrConstants.MODULE_ID, "clinician/clinicianHome", SimpleObject.create("patientId", patient.getId()));
        } else {
            return null;
        }
    }
}
