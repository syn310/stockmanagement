package bookrental;

import bookrental.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{

    @Autowired
    StockRepository stockRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRequested_Checkstock(@Payload Requested requested){
        System.out.println("============================= Kafka : " + requested);
        if(requested.isMe()) {
            System.out.println("=============================");
            System.out.println("requested");
            stockRepository.findByBookid(requested.getBookid())
                    .ifPresent(
                            stock -> {
                                long qty = stock.getQty();

                                if (qty >= 1) {
                                    stock.setQty(qty - 1);
                                    stock.setStatus("revSucceeded");
                                    stockRepository.save(stock);
                                    System.out.println("set Stock -1");
                                } else {
                                    stock.setStatus("revFailed");
                                    stockRepository.save(stock);
                                    System.out.println("stock-out");
                                }
                            }
                    )
            ;
        }
            System.out.println("=============================");

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCanceled_Cancelstock(@Payload Canceled canceled){

        if(canceled.isMe()) {
            System.out.println("=============================");
            System.out.println("canceled");

            stockRepository.findByBookid(canceled.getBookid())
                    .ifPresent(
                            stock -> {
                                stock.setQty(stock.getQty() + 1);
                                stock.setStatus("revCanceled");
                                stockRepository.save(stock);
                            }
                    )
            ;
            System.out.println("set Stock +1");
            System.out.println("=============================");
        }
    }
}
