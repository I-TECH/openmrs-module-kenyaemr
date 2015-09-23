package org.openmrs.module.kenyaemr.fragment.controller.patient;

import org.openmrs.module.kenyacore.CoreUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.OpenmrsUtil;

import java.util.Date;

/**
 * Created by codehub on 9/22/15.
 */
public class DailySeenFragmentController {

    public void controller(FragmentModel model, @FragmentParam(value = "date", required = false) Date date) {

        Date today = OpenmrsUtil.firstSecondOfDay(new Date());
        Date yesterday = CoreUtils.dateAddDays(today, -1);

        // Date defaults to today
        if (date == null) {
            date = today;
        }
        else {
            // Ignore time
            date = OpenmrsUtil.firstSecondOfDay(date);
        }

        model.addAttribute("date", date);
        model.addAttribute("isToday", date.equals(today));
        model.addAttribute("isYesterday", date.equals(yesterday));
    }
}
