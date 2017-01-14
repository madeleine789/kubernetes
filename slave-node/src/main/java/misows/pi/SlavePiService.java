package misows.pi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class SlavePiService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BigDecimal bellardsFormula(int start, int iterations, int precision) {
        logger.info("Received partial sum computation request start {} iters {} precision {}", start, iterations, precision);
        BigDecimal sum = new BigDecimal(0);
        for (int i = start; i < iterations + start; i++) {
            BigDecimal tmp;
            BigDecimal term;
            BigDecimal divisor;
            term = new BigDecimal(-32);
            divisor = new BigDecimal(4 * i + 1);
            tmp = term.divide(divisor, precision, BigDecimal.ROUND_FLOOR);
            term = new BigDecimal(-1);
            divisor = new BigDecimal(4 * i + 3);
            tmp = tmp.add(term.divide(divisor, precision, BigDecimal.ROUND_FLOOR));
            term = new BigDecimal(256);
            divisor = new BigDecimal(10 * i + 1);
            tmp = tmp.add(term.divide(divisor, precision, BigDecimal.ROUND_FLOOR));
            term = new BigDecimal(-64);
            divisor = new BigDecimal(10 * i + 3);
            tmp = tmp.add(term.divide(divisor, precision, BigDecimal.ROUND_FLOOR));
            term = new BigDecimal(-4);
            divisor = new BigDecimal(10 * i + 5);
            tmp = tmp.add(term.divide(divisor, precision, BigDecimal.ROUND_FLOOR));
            term = new BigDecimal(-4);
            divisor = new BigDecimal(10 * i + 7);
            tmp = tmp.add(term.divide(divisor, precision, BigDecimal.ROUND_FLOOR));
            term = new BigDecimal(1);
            divisor = new BigDecimal(10 * i + 9);
            tmp = tmp.add(term.divide(divisor, precision, BigDecimal.ROUND_FLOOR));
            int s = ((1 - ((i & 1) << 1)));
            divisor = new BigDecimal(2);
            divisor = divisor.pow(10 * i).multiply(new BigDecimal(s));
            sum = sum.add(tmp.divide(divisor, precision, BigDecimal.ROUND_FLOOR));
        }
        return sum;
    }

}
