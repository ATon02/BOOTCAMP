package co.com.backend.reactive.sqs.sender;

import co.com.backend.reactive.model.datatosend.DataToSend;
import co.com.backend.reactive.model.datatosend.gateways.DataToSendRepository;
import co.com.backend.reactive.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements DataToSendRepository {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    public Mono<String> send(String message) {
        return Mono.fromCallable(() -> buildRequest(message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }

    @Override
    public Mono<Void> sendDataToSqs(DataToSend dataToSend) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(dataToSend))
                .flatMap(json -> Mono.fromFuture(client.sendMessage(
                        SendMessageRequest.builder()
                                .queueUrl(properties.queueUrl())
                                .messageBody(json)
                                .build())))
                .doOnNext(response -> log.info("Message sent SQS register-bootcamp, id: {}", response.messageId()))
                .then();
    }

}
