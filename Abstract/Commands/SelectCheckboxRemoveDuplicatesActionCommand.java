package Abstract.Commands;

import Services.DIResolver;
import Services.OutputDataService;
import org.tinylog.Logger;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class SelectCheckboxRemoveDuplicatesActionCommand  extends AbstractCommandAction {

    private final DIResolver diResolver;

    public SelectCheckboxRemoveDuplicatesActionCommand(DIResolver diResolver) {
        super("Remove duplicates");
        this.diResolver = diResolver;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Logger.tag("SYSTEM").info("Remove duplicates");
        OutputDataService outputDataService = diResolver.getOutputDataService();
        try {
            stripDuplicatesFromFile(outputDataService.getOutputFile());
        } catch(IOException ex) {
            Logger.tag("SYSTEM").error("Problem while removing duplicates");
        }
    }

    private void stripDuplicatesFromFile(File file) throws IOException, OutOfMemoryError {
        BufferedReader Buff = new BufferedReader(new FileReader(file));
        String headerRow = Buff.readLine();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        Set<String> lines = new HashSet<>(5000000);
        String line;
        boolean isHeaderExcluded = false;
        while ((line = reader.readLine()) != null) {
            if (!isHeaderExcluded) {
                isHeaderExcluded = true;
                continue;
            }
            lines.add(line);
        }
        reader.close();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        boolean isHeaderAdded = false;
        for (String unique : lines) {
            if (!isHeaderAdded) {
                writer.write(headerRow);
                writer.newLine();
                isHeaderAdded = true;
            }
            writer.write(unique);
            writer.newLine();
        }
        writer.close();
    }
}
