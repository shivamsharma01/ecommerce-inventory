package com.mcart.inventory.subscriber;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.mcart.inventory.service.ProductPubSubEventHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "inventory.pubsub.enabled", havingValue = "true", matchIfMissing = false)
public class ProductEventSubscriber {

	private static final String DEFAULT_SUBSCRIPTION = "inventory-product-events-sub";

	private final ProductPubSubEventHandler handler;
	private final PubSubTemplate pubSubTemplate;

	@Value("${inventory.pubsub.subscription:" + DEFAULT_SUBSCRIPTION + "}")
	private String subscriptionName;

	private com.google.cloud.pubsub.v1.Subscriber subscriber;

	@PostConstruct
	public void subscribe() {
		subscriber = pubSubTemplate.subscribe(subscriptionName, this::onMessage);
		log.info("Inventory subscribed to {}", subscriptionName);
	}

	@PreDestroy
	public void shutdown() {
		if (subscriber != null) {
			subscriber.stopAsync();
		}
	}

	private void onMessage(BasicAcknowledgeablePubsubMessage message) {
		try {
			String raw = message.getPubsubMessage().getData().toStringUtf8();
			boolean ok = handler.handleMessage(raw);
			if (ok) {
				message.ack();
			} else {
				message.nack();
			}
		} catch (Exception ex) {
			log.error("Product event subscriber error", ex);
			message.nack();
		}
	}
}
