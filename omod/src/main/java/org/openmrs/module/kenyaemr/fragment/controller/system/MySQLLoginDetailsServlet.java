package org.openmrs.module.kenyaemr.fragment.controller.system;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by agnes on 9/4/14.
 */
public class MySQLLoginDetailsServlet extends HttpServlet {
    protected static final Log log = LogFactory.getLog(BackupRestoreFragmentController.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession httpSession = request.getSession();
        String username = request.getParameter("uname");
        String password = request.getParameter("pw");
        log.debug(username);
        log.debug(password);

    }
}
