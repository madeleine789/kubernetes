package misows.pi;

import io.fabric8.annotations.Endpoint;
import io.fabric8.annotations.Protocol;
import io.fabric8.annotations.ServiceName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class PiService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    @Protocol("http")
    @ServiceName("slave-node")
    @Endpoint
    private List<String> slaves;
    @Value("${pi.numberOfPods}")
    private int pods;

    @Value("${pi.batch}")
    private int batch;
    @Value("${pi.computation.time}")
    private int computationTime;

    private static final int CONVERGENCE_SPEED = 3;

    @Autowired
    private CommunicationService communication;

    @Autowired
    private CompletionService<PartialResult> completionService;

    public String computePi(int precision) {
        logger.info("Kubernetes state - {} pods with URLS {}", pods, slaves);
        logger.info("Received computation request precision {}", precision);
        int iterations = precision / CONVERGENCE_SPEED;
        int nextBatch = batch;
        int nextIteration = 0;

        LinkedList<String> free = new LinkedList<>(slaves);

        BigDecimal sum = new BigDecimal(0, new MathContext(precision));
        while (hasPendingBatchesOrNotAllBatchesComputed(iterations, nextIteration, free)) {
            Optional<Future<PartialResult>> future = getNextAvailableResult(iterations, nextIteration, free);
            if (future.isPresent()) {
                try {
                    PartialResult part = future.get().get();
                    free.addLast(part.getSlave());
                    if (part.failure()) {
                        completionService.submit(() -> communication.getPartialSum(free.removeFirst(), part.getStart(), part.getIterations(), precision));
                    } else {
                        nextBatch = updateNextBatchSize(part);
                        sum = sum.add(part.getSum());
                    }
                } catch (InterruptedException | ExecutionException e) {
                    throw new Error("A worker thread has been interrupted, quitting", e);
                }
            }
            if (nextIteration < iterations) {
                final int nextIterationCopy = nextIteration;
                final int nextBatchCopy = nextBatch;
                final String slave = free.removeFirst();
                completionService.submit(() -> communication.getPartialSum(slave, nextIterationCopy, nextBatchCopy, precision));
                nextIteration += nextBatch;
            }

        }
        logger.info("Computation completed precision {}", precision);
        return sum.divide(new BigDecimal(64), precision, BigDecimal.ROUND_FLOOR).toPlainString();
    }

    private boolean hasPendingBatchesOrNotAllBatchesComputed(int iterations, int nextIteration, LinkedList<String> free) {
        return nextIteration < iterations || free.size() != pods;
    }

    private int updateNextBatchSize(PartialResult part) {
        int nextBatch;
        if (part.getDuration() > computationTime) {
            nextBatch = part.getIterations() / 2;
        } else {
            nextBatch = part.getIterations() * 2;
        }
        return nextBatch;
    }

    private Optional<Future<PartialResult>> getNextAvailableResult(int iterations, int nextIteration, LinkedList<String> free) {
        Future<PartialResult> future;
        if (nextIteration >= iterations || free.isEmpty()) {
            try {
                future = completionService.take();
            } catch (InterruptedException e) {
                throw new Error("A worker thread has been interrupted, quitting", e);
            }
        } else {
            future = completionService.poll();
        }
        return Optional.ofNullable(future);
    }

    public List<String> getSlaves() {
        return slaves;
    }
}
