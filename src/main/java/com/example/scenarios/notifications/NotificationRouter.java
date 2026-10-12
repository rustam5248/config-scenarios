package com.example.scenarios.notifications;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Strategy selection at runtime. Spring injects every {@link NotificationSender} bean,
 * sorted by {@code @Order}, so the first element doubles as the fallback channel.
 */
@Service
public class NotificationRouter {

    private final List<NotificationSender> senders;
    private final Map<Channel, NotificationSender> byChannel;

    public NotificationRouter(List<NotificationSender> senders) {
        this.senders = List.copyOf(senders);
        Map<Channel, NotificationSender> map = new EnumMap<>(Channel.class);
        senders.forEach(sender -> map.put(sender.channel(), sender));
        this.byChannel = Collections.unmodifiableMap(map);
    }

    /** Sends on the customer's preferred channel, or the highest-priority one if it is unavailable. */
    public DeliveryReceipt send(Channel preferred, String recipient, String message) {
        NotificationSender sender = byChannel.getOrDefault(preferred, senders.get(0));
        return sender.send(recipient, message);
    }

    public List<Channel> availableChannels() {
        return senders.stream().map(NotificationSender::channel).toList();
    }
}
