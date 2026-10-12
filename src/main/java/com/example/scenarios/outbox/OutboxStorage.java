package com.example.scenarios.outbox;

import java.util.List;

public interface OutboxStorage {

    String kind();

    void append(OutboxMessage message);

    List<OutboxMessage> pending();
}
