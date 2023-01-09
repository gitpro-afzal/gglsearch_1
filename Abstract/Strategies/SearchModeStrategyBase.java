package Abstract.Strategies;

import Abstract.Exceptions.InputFileEmptyException;
import Services.DIResolver;
import org.tinylog.Logger;

import java.util.concurrent.ThreadPoolExecutor;

public abstract class SearchModeStrategyBase {

    protected final DIResolver diResolver;
    protected ThreadPoolExecutor executor;

    public SearchModeStrategyBase(DIResolver diResolver) {
        this.diResolver = diResolver;
    }

    public abstract void processData() throws InputFileEmptyException;
    public abstract void stopProcessing();

    public abstract void updateStatusText();

    protected void waitForTheEnd(int taskCount) {
        Thread thread = new Thread(() -> {
            diResolver.getGuiService().changeApplicationStateToWork(true);
            while (true) {
                if (taskCount == executor.getCompletedTaskCount()) {
                    executor.shutdownNow();
                }
                if (executor.isTerminated()) {
                    break;
                }
                updateStatusText();
            }
            Logger.tag("SYSTEM").info("Program: Finished");
            diResolver.getGuiService().setStatusText("Finished");
            diResolver.getGuiService().changeApplicationStateToWork(false);
        });
        thread.start();
    }
}
