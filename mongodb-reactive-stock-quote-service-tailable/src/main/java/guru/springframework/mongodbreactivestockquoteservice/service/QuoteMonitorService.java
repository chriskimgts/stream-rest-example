package guru.springframework.mongodbreactivestockquoteservice.service;

import guru.springframework.mongodbreactivestockquoteservice.client.StockQuoteClient;
import guru.springframework.mongodbreactivestockquoteservice.domain.Quote;
import guru.springframework.mongodbreactivestockquoteservice.repositories.QuoteRepository;
import java.time.Instant;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Created by jt on 10/19/17.
 */
@Service
public class QuoteMonitorService implements ApplicationListener<ContextRefreshedEvent> {

    private final StockQuoteClient stockQuoteClient;
    private final QuoteRepository quoteRepository;

    public QuoteMonitorService(StockQuoteClient stockQuoteClient, QuoteRepository quoteRepository) {
        this.stockQuoteClient = stockQuoteClient;
        this.quoteRepository = quoteRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        stockQuoteClient.getQuoteStream()
                .log("quote-monitor-service")
                .flatMap(quoteRepository::save)
                .doOnNext(quote -> 
                    System.out.println("I saved a quote! Id: " + quote.getId()))
                .subscribe();
    }
}
