package com.example.scenarios.http;

/** Expensive shared resource: there should be exactly one per application. */
public class ConnectionPool {

    private final int maxConnections;

    public ConnectionPool(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int maxConnections() {
        return maxConnections;
    }
}
