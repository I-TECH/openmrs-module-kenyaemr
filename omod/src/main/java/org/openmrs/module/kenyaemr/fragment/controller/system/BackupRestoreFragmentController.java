package org.openmrs.module.kenyaemr.fragment.controller.system;

import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.EmrConstants;
import org.openmrs.module.kenyaemr.api.KenyaEmrService;
import org.openmrs.module.kenyaemr.security.Decryption;
import org.openmrs.module.kenyaui.KenyaUiUtils;
import org.openmrs.module.kenyaui.annotation.AppPage;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by agnes on 7/9/14.
 */
@Controller
@Component
@AppPage(EmrConstants.APP_ADMIN)
public class BackupRestoreFragmentController {

    @Autowired
    private KenyaUiUtils kenyaUiUtils;
    public static String strFilename;

    public void controller(){
	    nextScheduledBackup();
    }

    public static String decryptMysqlDetails() {
        final String iv = "0123456789abcdef"; // This has to be 16 characters
        final String secretKey = "Replace this by your secret key";
        String mysqlDetails = Context.getService(KenyaEmrService.class).getMysqlDetails();

        final String decryptedDatas = Decryption.decrypt(mysqlDetails, iv, secretKey);
        return decryptedDatas;
    }

    public static String backupEnhancement(@SpringBean KenyaUiUtils kenyaUi, HttpSession httpSession) throws Exception {
        Context.openSession();
        Location location = Context.getService(KenyaEmrService.class).getDefaultLocation();
        String defaultLocation = location.getName().replaceAll("\\s", "_");

        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-d-yyyy_hh-mm-ssa");
        strFilename = dateFormat.format(now);

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select Backup Location");
        chooser.setPreferredSize(new Dimension(800, 600));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setApproveButtonText("Run Backup");
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            String dir = (chooser.getSelectedFile().toString());

            String mysqlDetails = decryptMysqlDetails();
            String command = ("mysqldump -uroot -p"+mysqlDetails+" openmrs -r" + dir + "/" + defaultLocation + "-" + strFilename + ".sql");


/**
 * Save Last BackupDate/Time; Last Backup Status values to the database.
 * Install the global property
 * processComplete==0 when backup process is successful
 * Save backups in zip format
 */


            Process p = null;
            try {
                Runtime runtime = Runtime.getRuntime();
                p = runtime.exec(command);
                int processComplete = p.waitFor();
                if (processComplete == 0) {
                    kenyaUi.notifySuccess(httpSession,"Database Backup Successful");

                    byte[] buffer = new byte[1024];
                    String srcFilename = (dir + "/" + defaultLocation + "-" + strFilename + ".sql");
                    try {
                        FileOutputStream fos = new FileOutputStream(dir + "/" + defaultLocation + "-" + strFilename + ".sql.zip");
                        ZipOutputStream zos = new ZipOutputStream(fos);
                        File srcFile = new File(srcFilename);
                        ZipEntry ze = new ZipEntry("/" + defaultLocation + "-" + strFilename + ".sql");
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
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
/**
 *Save the values upon successful backup ie: date and time of last backup; Backup status: successful/failed
 */
                    Context.openSession();
                    GlobalProperty lastBackupGP = new GlobalProperty(EmrConstants.GP_LAST_BACKUP, strFilename);
                    Context.getAdministrationService().getGlobalPropertyObject(lastBackupGP.toString());

                    GlobalProperty backupSuccessfulGP = new GlobalProperty(EmrConstants.GP_BACKUP_STATUS, "Backup Successful");
                    Context.getAdministrationService().getGlobalPropertyObject(backupSuccessfulGP.toString());

                    if (Context.isAuthenticated() != true) {
                        String username = Context.getAdministrationService().getGlobalProperty("scheduler.username");
                        String password = Context.getAdministrationService().getGlobalProperty("scheduler.password");
                        Context.authenticate(username, password);
                    }
                    Context.getAdministrationService().saveGlobalProperty(lastBackupGP);
                    Context.getAdministrationService().saveGlobalProperty(backupSuccessfulGP);

                } else {
                    GlobalProperty lastBackupGP = new GlobalProperty(EmrConstants.GP_LAST_BACKUP, strFilename);
                    Context.openSession();
                    if (Context.isAuthenticated() != true) {
                        String username = Context.getAdministrationService().getGlobalProperty("scheduler.username");
                        String password = Context.getAdministrationService().getGlobalProperty("scheduler.password");
                        Context.authenticate(username, password);
                    }
                    Context.getAdministrationService().getGlobalPropertyObject(lastBackupGP.toString());
                    Context.getAdministrationService().saveGlobalProperty(lastBackupGP);
                    GlobalProperty backupFailedGP = new GlobalProperty(EmrConstants.GP_BACKUP_STATUS, "Backup Failed");
                    Context.getAdministrationService().getGlobalPropertyObject(backupFailedGP.toString());
                    Context.getAdministrationService().saveGlobalProperty(backupFailedGP);

                    kenyaUi.notifyError(httpSession, "Database Backup Failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            kenyaUi.notifySuccess(httpSession, "Database Backup Successful");

        } else {
            kenyaUi.notifyError(httpSession, "No backup destination folder selected");
        }
        return null;
    }


    /**
     * Generate UUID for random naming of cron jobs.
     */

    public static String cronJobID(){
        String cronID = UUID.randomUUID().toString();
        return cronID;
    }

    public static String TriggerJobID(){
        String cronID = UUID.randomUUID().toString();
        return cronID;
    }

    public static String groupJobID(){
        String cronID = UUID.randomUUID().toString();
        return cronID;
    }

    /**
     * method to schedule backups using quartz scheduler
     * displays next schedule date/time in the backup summary fragment
     *
     * @return the nextscheduled backup date/time
     *
     */

    public static String nextScheduledBackup(){
        try {
            JobDetail jobDetail = JobBuilder.newJob(ScheduledIncrementalBackups.class)
                    .withIdentity(cronJobID(), groupJobID())
                    .build();

            Trigger cronTrigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity(TriggerJobID(), groupJobID())
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 58 9,12,19 * * ?")
		                    .withMisfireHandlingInstructionFireAndProceed())
		            .startNow()
                    .build();

            SchedulerFactory schFactory = new StdSchedulerFactory();
            Scheduler sch = schFactory.getScheduler();
            sch.start();

            sch.scheduleJob(jobDetail, cronTrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }


        Trigger cronTrigger = TriggerBuilder
                .newTrigger()
                .withIdentity(TriggerJobID(), groupJobID())
                .withSchedule(CronScheduleBuilder.cronSchedule("0 58 9,12,19 * * ?")
		                .withMisfireHandlingInstructionFireAndProceed())
		        .startNow()
                .build();

        Date nowtime = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-d-yyyy_hh-mm-ssa");
        Date nextFireTime = cronTrigger.getFireTimeAfter(nowtime);
        String dtime = dateFormat.format(nextFireTime);
        System.out.println("NEXT TIME:" + nextFireTime);
        System.out.println("NOW TIME" + nowtime);


        GlobalProperty nextScheduledBackupGP = new GlobalProperty(EmrConstants.GP_NEXT_SCHEDULED_BACKUP, dtime);
        Context.getAdministrationService().getGlobalPropertyObject(nextScheduledBackupGP.toString());
        Context.getAdministrationService().saveGlobalProperty(nextScheduledBackupGP);
        String nextScheduledBackup = Context.getAdministrationService().getGlobalProperty(EmrConstants.GP_NEXT_SCHEDULED_BACKUP);

        return nextScheduledBackup;
    }

    /**
     * method to display the last backup date time in the backup summary fragment
     * Called in the class SystemUtilsFragmentController
     *
     * @return
     * @throws SQLException
     */
    public static String lastBackup() throws SQLException {
        String lastBackupDateTime = Context.getAdministrationService().getGlobalProperty(EmrConstants.GP_LAST_BACKUP);
        return lastBackupDateTime;
    }

    /**
     * method to display the status of the last backup in the backup summary fragment
     * Called in the class SystemUtilsFragmentController
     *
     * @return
     * @throws Exception
     */
    public static String backupStatus() throws Exception {
        String backupStatus = Context.getAdministrationService().getGlobalProperty(EmrConstants.GP_BACKUP_STATUS);
        return backupStatus;
    }

    /**
     * Restore the database from backup
      * @param kenyaUi
     * @param httpSession
     */
    public void restoreDatabase(@SpringBean KenyaUiUtils kenyaUi, HttpSession httpSession, HttpServletRequest request) {
        Context.openSession();
        JFileChooser fc = new JFileChooser();

        fc.setDialogTitle("Select Database Restore File");
        fc.setPreferredSize(new Dimension(800, 600));
        fc.setAcceptAllFileFilterUsed(false);
        fc.setApproveButtonText("Restore Database");
        FileFilter filter = new FileNameExtensionFilter("zip File", "zip");

        fc.addChoosableFileFilter(filter);
        fc.setFileFilter(filter);
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String myFile = (fc.getSelectedFile().toString());
            String myFolder = fc.getCurrentDirectory().toString();

            String INPUT_ZIP_FILE = myFile;
            String OUTPUT_FOLDER = myFolder;

            byte[] buffer = new byte[1024];
            try {
                File folder = new File(myFile);
                if (!folder.exists()) {
                    folder.mkdir();
                }
                ZipInputStream zis = new ZipInputStream(new FileInputStream(INPUT_ZIP_FILE));
                ZipEntry ze = zis.getNextEntry();
                while (ze != null) {
                    String fileName = ze.getName();
                    File newFile = new File(OUTPUT_FOLDER + File.separator + fileName);

                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    ze = zis.getNextEntry();
                    zis.closeEntry();
                    zis.close();

	                String mysqlDetails = decryptMysqlDetails();
                    String user = "root";
                    String dbname = "openmrs";
                    String source = newFile.getAbsoluteFile().toString();

                    String[] command = new String[]{"mysql", dbname, "--user=" + user, "--password=" +mysqlDetails, "-e", " source " + source};

                    Process p = null;
                    try {
                        Runtime runtime = Runtime.getRuntime();
                        p = runtime.exec(command);
                        int processComplete = p.waitFor();
                        if (processComplete == 0) {
                            kenyaUi.notifySuccess(httpSession, "Database Restore Successful");
                            System.out.println("Restore successful");

                        } else {
                            System.out.println("Restore failed");
                            kenyaUi.notifyError(httpSession, "Database Restore UnSuccessful");

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    Catch unzip try block
                    try {
                        File file = new File(source);
                        if (file.delete()) {
                        } else {
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
