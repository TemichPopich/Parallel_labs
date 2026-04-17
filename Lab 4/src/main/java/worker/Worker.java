package worker;

import lombok.extern.log4j.Log4j2;
import service.Service;

import static utils.Utils.PARSE_ERROR_MESSAGE;
import static utils.Utils.THREADS;
import static utils.Utils.isNumeric;

@Log4j2
public class Worker {
    private static final String REGEX = "^https://mkub\\.ru/dlja-gostinoj/.*/\\?limit=48$";

    public static void main(String[] args) {
        if (args.length < 2 || !isNumeric(args[0]) || !args[1].matches(REGEX)) {
            throw new RuntimeException("Not valid argument");
        }

        final Service service = new Service();

        THREADS.forEach(threadCount -> {
            try {
                service.getProducers(threadCount, args[0], args[1]);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error(PARSE_ERROR_MESSAGE, e);
            }
        });

    }
}
