package service;

import lombok.extern.log4j.Log4j2;
import parser.Parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static utils.Utils.BASE_LINKS;
import static utils.Utils.RESULT_FILE_NAME;

@Log4j2
public class Service {
    private final Parser parser = new Parser();

    public void getProducers(final int threadCount) throws InterruptedException {
        final ConcurrentHashMap<String, Integer> result = new ConcurrentHashMap<>();
        final CopyOnWriteArrayList<String> links = new CopyOnWriteArrayList<>();

        long startTime = 0;
        long endTime = Long.MAX_VALUE;

        try (final ExecutorService executorService = Executors.newFixedThreadPool(threadCount)) {
            startTime = System.currentTimeMillis();

            List<Callable<Void>> baseLinksTasks = BASE_LINKS.stream().map(link -> (Callable<Void>) () -> {
                List<String> parsedLinks = parser.parseLinks(link);
                links.addAll(parsedLinks);
                return null;
            }).toList();

            executorService.invokeAll(baseLinksTasks);

            List<Callable<Void>> subLinksTasks = links.stream().map(link -> (Callable<Void>) () -> {
                String producer = parser.parseProducer(link);
                result.merge(producer, 1, Integer::sum);
                return null;
            }).toList();

            executorService.invokeAll(subLinksTasks);

            executorService.shutdown();

            endTime = System.currentTimeMillis();
        } finally {
            StringBuilder resultString = new StringBuilder();
            result.forEach((key, value) -> resultString.append(key).append(" : ").append(value).append("\n"));

            File file = new File(RESULT_FILE_NAME.formatted(threadCount));
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(resultString.toString());
            } catch (IOException e) {
                log.error("Error while writing result to file", e);
            }

            long duration = endTime - startTime;
            log.info("Execution time: {} milliseconds for {} threads, processed {} links", duration, threadCount, links.size());
        }
    }
}