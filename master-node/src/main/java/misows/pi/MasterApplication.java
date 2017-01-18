package misows.pi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.concurrent.*;

@EnableAutoConfiguration
@EnableWebMvc
@Configuration
@ComponentScan
@SpringBootApplication
public class MasterApplication extends SpringBootServletInitializer {


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MasterApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(MasterApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ConversionService conversionService() {
        return new DefaultConversionService();
    }

    @Bean
    public CompletionService<PartialResult> completionService(@Value("${pi.numberOfPods}")int poolSize,  @Value("${pi.maxthreads}") int maxThreads) {
        if (poolSize > 0)
            return new ExecutorCompletionService<>(new ThreadPoolExecutor(poolSize, maxThreads, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<>()));
        return null;
    }
}
