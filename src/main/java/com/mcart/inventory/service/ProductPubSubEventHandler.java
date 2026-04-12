package com.mcart.inventory.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.mcart.inventory.dto.InventoryInitRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductPubSubEventHandler {

	private static final String CREATED = "PRODUCT_CREATED";
	private static final String UPDATED = "PRODUCT_UPDATED";
	private static final String DELETED = "PRODUCT_DELETED";

	private final ObjectMapper objectMapper;
	private final InventoryService inventoryService;

	public boolean handleMessage(String json) {
		if (json == null || json.isBlank()) {
			return true;
		}
		try {
			JsonNode root = objectMapper.readTree(json);
			if (!root.isObject() || !root.hasNonNull("eventType")) {
				log.warn("Invalid product event: missing eventType");
				return true;
			}
			String eventType = root.get("eventType").asText();
			JsonNode payloadNode = root.get("payload");
			if (payloadNode == null || !payloadNode.isObject()) {
				log.warn("Invalid product event: missing payload");
				return true;
			}

			if (DELETED.equals(eventType)) {
				String productId = text(payloadNode, "productId");
				if (productId == null || productId.isBlank()) {
					JsonNode agg = root.get("aggregateId");
					if (agg != null && agg.isTextual()) {
						productId = agg.asText();
					}
				}
				if (productId == null || productId.isBlank()) {
					log.warn("PRODUCT_DELETED without productId or aggregateId");
					return true;
				}
				String id = productId.trim();
				inventoryService.deleteByProductId(id);
				log.info("Inventory row removed for deleted product {}", id);
				return true;
			}

			if (CREATED.equals(eventType) || UPDATED.equals(eventType)) {
				String productId = text(payloadNode, "productId");
				if (productId == null || productId.isBlank()) {
					log.warn("{} without productId", eventType);
					return true;
				}
				JsonNode sqNode = payloadNode.get("stockQuantity");
				if (sqNode == null || sqNode.isNull() || !sqNode.isNumber()) {
					log.debug("Skipping inventory for {} — no stockQuantity in payload", productId);
					return true;
				}
				int qty = sqNode.asInt();
				if (qty < 0) {
					qty = 0;
				}
				inventoryService.init(new InventoryInitRequest(productId.trim(), qty));
				return true;
			}

			log.debug("Ignoring product event type {}", eventType);
			return true;
		} catch (Exception ex) {
			log.error("Failed to process product event message", ex);
			return false;
		}
	}

	private static String text(JsonNode node, String field) {
		JsonNode n = node.get(field);
		return n != null && n.isTextual() ? n.asText() : null;
	}
}
