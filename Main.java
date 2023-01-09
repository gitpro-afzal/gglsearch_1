import Abstract.Commands.ApplicationStartedActionCommand;
import GUI.Bootstrapper;
import Services.*;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.start();
    }

    private void start() {
        initLookAndFeel();

        GuiService guiService = new GuiService();
        UserAgentsRotatorService userAgentsRotatorService = new UserAgentsRotatorService();
        InputDataService inputDataService = new InputDataService();
        SettingsService settingsService = new SettingsService();
        DBConnectionService dbConnectionService = new DBConnectionService();
        OutputDataService outputDataService = new OutputDataService(dbConnectionService);

        DIResolver diResolver = new DIResolver(userAgentsRotatorService, guiService, outputDataService, inputDataService, settingsService, dbConnectionService);

        Bootstrapper bootstrapper = new Bootstrapper(diResolver);
        bootstrapper.setTitle("Info searcher v. 6.1.14 [GGL]");
        bootstrapper.setVisible(true);
        bootstrapper.setResizable(false);
        bootstrapper.setSize(800, 700);

        guiService.setBootstrapper(bootstrapper);

        ApplicationStartedActionCommand applicationStartedActionCommand = new ApplicationStartedActionCommand(diResolver);
        applicationStartedActionCommand.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
    }

    private void initLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
    }
}
