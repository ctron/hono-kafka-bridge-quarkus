/*******************************************************************************
 * Copyright (c) 2020 Red Hat Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jens Reimann - initial API and implementation
 *******************************************************************************/

package de.dentrassi.iot.hono.bridge;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.apache.kafka.common.header.internals.RecordHeaders;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.reactive.messaging.amqp.IncomingAmqpMetadata;
import io.smallrye.reactive.messaging.kafka.OutgoingKafkaRecordMetadata;
import io.vertx.core.json.JsonObject;

@ApplicationScoped
public class TelemetryBridge {

    private static final Logger log = LoggerFactory.getLogger(TelemetryBridge.class);

    @Incoming("telemetry-hono")
    @Outgoing("telemetry-kafka")
    public Message<byte[]> generate(final Message<byte[]> incoming) {

        final Optional<IncomingAmqpMetadata> metadata = incoming.getMetadata(IncomingAmqpMetadata.class);

        String deviceId = null;
        byte[] deviceAlias = null;

        if (metadata.isPresent()) {
            final JsonObject properties = metadata.get().getProperties();
            deviceId = properties.getString("device_id");
            deviceAlias = Optional.ofNullable(properties.getString("deviceAlias"))
                    .map(s -> s.getBytes(StandardCharsets.UTF_8))
                    .orElse(null);
        }

        log.info("Passing along message - device: {}, alias: {} -> {}", deviceId, deviceAlias, incoming.getPayload());

        final Metadata outgoingMetadata = Metadata.of(OutgoingKafkaRecordMetadata.<String>builder()
                .withKey(deviceId)
                .withHeaders(new RecordHeaders().add("deviceAlias", deviceAlias))
                .build());

        return Message.of(
                incoming.getPayload(),
                outgoingMetadata,
                incoming::ack);
    }

}
