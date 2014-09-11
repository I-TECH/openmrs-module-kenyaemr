package org.openmrs.module.kenyaemr.fragment.controller.system;

import org.apache.log4j.Logger;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by agnes on 8/7/14.
 */
public class ScheduledIncrementalBackups implements Job {
    private Logger log = Logger.getLogger(ScheduledIncrementalBackups.class);
    @Autowired
    private KenyaUiUtils kenyaUiUtils;
    HttpSession httpSession;
    public String userHome = System.getProperty("user.home");
    String url = "jdbc:mysql://localhost:3306/openmrs";
    String user = "root";
    String password = "pass";
    Connection conn = null;
    PreparedStatement stmt = null;

    public void execute(JobExecutionContext jExeCtx) throws JobExecutionException {

        log.debug("TestJob run successfully...");
        System.out.println("TestJob run successfully...");

        Context.openSession();
        Location location = Context.getService(KenyaEmrService.class).getDefaultLocation();
        String defaultLocation = location.getName().replaceAll("\\s", "_");
        Context.closeSession();

//  2.  Current date time
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-d-yyyy_hh-mm-ssa");
        String strFilename = dateFormat.format(now);
        System.out.println(strFilename);

        System.out.println(userHome);
        String dir = userHome + "/KENYAEMRMANUALBACKUPS";
        if (!new File(userHome + "/KENYAEMRMANUALBACKUPS").exists()) {
            File directory = new File(userHome + "/KENYAEMRMANUALBACKUPS");
            boolean successful = directory.mkdir();
            if (successful) {
                if (kenyaUiUtils !=null) {
                    kenyaUiUtils.notifySuccess(httpSession, "directory created successfully");
                }
                System.out.println("directory created successfully");
            } else {
                System.out.println("could not create directory");
            }
        }

        /**
         * Call method to delete the saved scheduled backups after 30 days
         */
        deleteScheduledIncrementalBackups(1, ".sql.zip");

        String command = ("mysqldump -uroot -ppass backuptest -r" + dir + "/" + defaultLocation + "-" + strFilename + ".sql");

        Process p = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            p = runtime.exec(command);
            int processComplete = p.waitFor();
            if (processComplete == 0) {

                byte[] buffer = new byte[1024];
                String srcFilename = (dir + "/" + defaultLocation + "-" + strFilename + ".sql");
                try {
                    FileOutputStream fos = new FileOutputStream(dir + "/" + defaultLocation + "-" + strFilename + ".sql.zip");
                    ZipOutputStream zos = new ZipOutputStream(fos);
                    File srcFile = new File(srcFilename);
                    ZipEntry ze = new ZipEntry(defaultLocation + "-" + strFilename + ".sql");
                    FileInputStream in = new FileInputStream(srcFile);
                    zos.putNextEntry(ze);

                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                    in.close();
                    zos.close();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                try {
                    File file = new File(srcFilename);
                    if (file.delete()) {
                        System.out.println(file.getName() + " is deleted!");
                    } else {
                        System.out.println("Delete operation is failed.");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                /**
                 * save the last backup date time to the database
                 * authentication required
                 */
                Context.openSession();
                GlobalProperty lastBackupGP = new GlobalProperty(EmrConstants.GP_LAST_BACKUP, strFilename);
                GlobalProperty backupSuccessfulGP = new GlobalProperty(EmrConstants.GP_BACKUP_STATUS, "Backup Successful");

                if (Context.isAuthenticated() != true) {
                    String username = Context.getAdministrationService().getGlobalPropertyByUuid("efc55087-9285-46db-b613-9ccd8655a5e1").getValue().toString();
                    String password = Context.getAdministrationService().getGlobalPropertyByUuid("3a8e6ff8-6fd2-44c6-b625-b1e25ce243de").getValue().toString();
                    Context.authenticate(username, password);
                }
                Context.getAdministrationService().saveGlobalProperty(lastBackupGP);
                Context.getAdministrationService().saveGlobalProperty(backupSuccessfulGP);

            } else {
                GlobalProperty backupFailedGP = new GlobalProperty(EmrConstants.GP_BACKUP_STATUS, "Backup Failed");
                Context.getAdministrationService().getGlobalPropertyObject(backupFailedGP.toString());
                Context.getAdministrationService().saveGlobalProperty(backupFailedGP);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param days number of days after which the saved scheduled backups should be deleted
     * @param fileExtension specify's the kind of files to be deleted
     */
    public void deleteScheduledIncrementalBackups(long days, String fileExtension) {
        if (new File(userHome + "/KENYAEMRMANUALBACKUPS").exists()) {
            String dir = userHome + "/KENYAEMRMANUALBACKUPS";
            File folder = new File(dir);
            if (folder.exists()) {
                File[] listFiles = folder.listFiles();
                long eligibleForDeletion = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L);
                System.out.println("Deleted files successfully");
                for (File listFile : listFiles) {
                    if (listFile.getName().endsWith(fileExtension)
                            && listFile.lastModified() < eligibleForDeletion) {
                        if (!listFile.delete()) {
                            System.out.println("Sorry Unable to Delete Files..");

                        }
                    }
                }
            }
        }
    }
}

