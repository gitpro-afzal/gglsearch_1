package Services;

import GUI.*;
import Utils.DirUtils;
import Utils.StrUtils;
import org.tinylog.Logger;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GuiService {
    private Bootstrapper bootstrapper;
    private SearchSettingsDialog settingsDialog;
    private AppSettings appSettings;
    private int totalItems;
    private int completedItems;
    public GuiService() {
    }

    public Frame getMainFrame() {
        return bootstrapper;
    }

    public void createNewSettingsDialog(DIResolver diResolver) {
        closeSettingsDialog();
        settingsDialog = new SearchSettingsDialog(diResolver);
        settingsDialog.setSize(1600, 700);
        settingsDialog.setTitle("Search settings");
        settingsDialog.setVisible(true);
    }

    public void createNewAppSettingsDialog(DIResolver diResolver) {
        closeAppSettings();
        appSettings = new AppSettings(diResolver);
        appSettings.setSize(800, 600);
        appSettings.setTitle("Preferences");
        appSettings.setVisible(true);
    }

    private void closeAppSettings() {
        if (appSettings != null) {
            appSettings.Dispose();
        }
    }

    public File selectFile(String title) {
        File inputLocationsFile = null;
        FileDialog dialog = new FileDialog(bootstrapper);
        dialog.setTitle(title);
        dialog.setVisible(true);
        if (dialog.getFile() != null && !dialog.getFile().equalsIgnoreCase("") && dialog.getFile().toLowerCase().endsWith(".csv")) {
            inputLocationsFile = new File(dialog.getDirectory() + dialog.getFile());
        }
        return inputLocationsFile;
    }

    private void closeSettingsDialog() {
        if (settingsDialog != null) {
            settingsDialog.onCancel();
        }
    }

    public void setBootstrapper(Bootstrapper bootstrapper) {
        this.bootstrapper = bootstrapper;
    }

    public String getSearchPlaceholderText(){
        return bootstrapper.getSearchingPlaceHolder().getText();
    }


    public void clearInputDataFilePath() {
        bootstrapper.getSelectedFileLabelData().setText("");
    }

    public void clearInputExclsuionsDataFilePath() {
        settingsDialog.getURLsExceptionsLabelFile().setText("");
    }

    public void setInputFilePath(File file) {
        if (DirUtils.isFileOk(file, "csv")) {
            bootstrapper.getSelectedFileLabelData().setText(cutPath(file.getAbsolutePath()));
        }
    }

    public void setInputURLsExclusionDataFile(File file) {
        if (DirUtils.isFileOk(file, "csv")) {
            settingsDialog.getURLsExceptionsLabelFile().setText(StrUtils.cutStringFromEnd(file.getAbsolutePath(),10));
        }
    }

    public void setPlaceholder(String placeholder) {
        bootstrapper.getSearchingPlaceHolder().setText(placeholder);
    }

    public void changeApplicationStateToWork(boolean isWorkState) {
        bootstrapper.getRunButton().setEnabled(!isWorkState);
        bootstrapper.getStopButton().setEnabled(isWorkState);
        bootstrapper.getSearchingPlaceHolder().setEnabled(!isWorkState);
        bootstrapper.getSettingsFile().setEnabled(!isWorkState);
        bootstrapper.getInputData().setEnabled(!isWorkState);
    }


    private String cutPath(String path) {
        int size = 120;
        if (path.length() <= size) {
            return path;
        } else {
            return "..."+path.substring(path.length() - (size - 3));
        }
    }

    public void setStatusText(String text) {
        bootstrapper.getLabelStatusData().setText(text);
    }

    public void updateCountItemsStatus(int currentItem, int totalItems) {

//        totalItems = totalItems + 1;
        this.totalItems = totalItems;
        this.completedItems = currentItem;
        if (totalItems > 1) {
            setStatusText("Processed " + currentItem + "/" + (totalItems) +" links.");
        }
        else {
            setStatusText("Processed " + currentItem + "/" + (totalItems) +" links");
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Logger.tag("SYSTEM").error(e, "Interrupt exception");
        }
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getCompletedItems() {
        return completedItems;
    }
}
