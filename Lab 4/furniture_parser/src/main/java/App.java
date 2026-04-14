import lombok.extern.log4j.Log4j2;
import service.Service;

import static utils.Utils.PARSE_ERROR_MESSAGE;
import static utils.Utils.THREADS;

/**
 * Main class for the furniture parser application.
 */
@Log4j2
public class App {
    public static void main(String[] args) {
        log.info("Start link parser application");

        Service service = new Service();

        THREADS.forEach(threadCount -> {
            try {
                service.getProducers(threadCount);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error(PARSE_ERROR_MESSAGE, e);
            }
        });

        log.info("End link parser application");
    }
}
