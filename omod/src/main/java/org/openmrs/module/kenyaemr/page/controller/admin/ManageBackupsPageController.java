package org.openmrs.module.kenyaemr.page.controller.admin;

import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by agnes on 9/4/14.
 */
@AppPage(EmrConstants.APP_ADMIN)
public class ManageBackupsPageController {
    public void controller(@RequestParam(value = "mysqlpassword", required = false) String mysqlpassword,
                           PageModel model) {

        model.addAttribute("mysqlpassword", mysqlpassword);

        System.out.println(mysqlpassword);

    }
}
