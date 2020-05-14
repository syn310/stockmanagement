package bookrental;

import javax.persistence.*;

import bookrental.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

@Entity
@Table(name="Stock_table")
public class Stock {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String bookid;
    private Long qty;
    private String status;

    @PostPersist
    public void onPostPersist(){
        System.out.println("==============onPostPersist ====== bookid : "+getBookid());
        Incomed incomed = new Incomed();
        incomed.setId(this.getId());
        incomed.setBookid(this.getBookid());
        incomed.setQty(this.getQty());
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;

        try {
            json = objectMapper.writeValueAsString(incomed);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON format exception", e);
        }


        KafkaProcessor processor = Application.applicationContext.getBean(KafkaProcessor.class);
        MessageChannel outputChannel = processor.outboundTopic();

        outputChannel.send(MessageBuilder
                .withPayload(json)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());


        //BeanUtils.copyProperties(this, incomed);
        //incomed.publishAfterCommit();

    }

    @PostUpdate
    public void onPostUpdate(){
        String status = this.getStatus();
        System.out.println("==============onPostUpdate ====== status : "+status);

        if(status.equals("revSucceeded")){
            Revsuccessed revsuccessed = new Revsuccessed();

            revsuccessed.setId(this.getId());
            revsuccessed.setBookid(this.getBookid());
            ObjectMapper objectMapper = new ObjectMapper();
            String json = null;

            try {
                json = objectMapper.writeValueAsString(revsuccessed);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON format exception", e);
            }


            KafkaProcessor processor = Application.applicationContext.getBean(KafkaProcessor.class);
            MessageChannel outputChannel = processor.outboundTopic();

            outputChannel.send(MessageBuilder
                    .withPayload(json)
                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                    .build());


            /*BeanUtils.copyProperties(this, revsuccessed);
            revsuccessed.publishAfterCommit();*/
        } else if(status.equals("revFailed")){
            Revfailed revfailed = new Revfailed();

            revfailed.setId(this.getId());
            revfailed.setBookid(this.getBookid());
            ObjectMapper objectMapper = new ObjectMapper();
            String json = null;

            try {
                json = objectMapper.writeValueAsString(revfailed);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON format exception", e);
            }


            KafkaProcessor processor = Application.applicationContext.getBean(KafkaProcessor.class);
            MessageChannel outputChannel = processor.outboundTopic();

            outputChannel.send(MessageBuilder
                    .withPayload(json)
                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                    .build());
        } else if(status.equals("revCanceled")) {
            Revcanceled revcanceled = new Revcanceled();
            revcanceled.setId(this.getId());
            revcanceled.setBookid(this.getBookid());
            ObjectMapper objectMapper = new ObjectMapper();
            String json = null;

            try {
                json = objectMapper.writeValueAsString(revcanceled);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON format exception", e);
            }


            KafkaProcessor processor = Application.applicationContext.getBean(KafkaProcessor.class);
            MessageChannel outputChannel = processor.outboundTopic();

            outputChannel.send(MessageBuilder
                    .withPayload(json)
                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                    .build());
        }

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getBookid() {
        return bookid;
    }

    public void setBookid(String bookid) {
        this.bookid = bookid;
    }
    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }




}
