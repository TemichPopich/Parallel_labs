import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utils.Utils.BASE_LINKS;

/**
 * Main class for the furniture parser application.
 */
@Log4j2
public class App {
    public static void main(String[] args) {

        List<Process> processes = new ArrayList<>();

        String classpath = System.getProperty("java.class.path");
        String workerClassPath = "worker.Worker";

        log.info("Start link parser application");
        for (int i = 0; i < BASE_LINKS.size(); i++) {
            ProcessBuilder builder = new ProcessBuilder(
                "java",
                "-cp", classpath,
                workerClassPath,
                String.valueOf(i + 1),
                BASE_LINKS.get(i))
                .inheritIO();
            try {
                processes.add(builder.start());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        processes.forEach(p -> {
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Main thread interrupted", e);
            }
        });

        log.info("End link parser application");
    }
}
