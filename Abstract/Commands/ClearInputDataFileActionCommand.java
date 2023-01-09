package Abstract.Commands;

import Services.DBConnectionService;
import Services.DIResolver;
import Services.GuiService;
import Services.InputDataService;
import org.tinylog.Logger;

import java.awt.event.ActionEvent;

public class ClearInputDataFileActionCommand extends AbstractCommandAction {

    private final DIResolver diResolver;
    @Override
    public void actionPerformed(ActionEvent e) {
        Logger.tag("SYSTEM").info("Input data file removed");
        GuiService guiService = diResolver.getGuiService();
        InputDataService inputDataService = diResolver.getInputDataService();
        DBConnectionService dbConnectionService = diResolver.getDbConnectionService();

        inputDataService.clearInputDataFile();
        guiService.clearInputDataFilePath();
        dbConnectionService.updateFileDataPath("");
    }

    public ClearInputDataFileActionCommand(DIResolver diResolver) {
        super("Clear current file");
        this.diResolver = diResolver;
    }
}
