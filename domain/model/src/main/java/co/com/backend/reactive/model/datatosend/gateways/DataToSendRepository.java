package co.com.backend.reactive.model.datatosend.gateways;

import co.com.backend.reactive.model.datatosend.DataToSend;
import reactor.core.publisher.Mono;

public interface DataToSendRepository {
    Mono<Void> sendDataToSqs(DataToSend dataToSend);
}
