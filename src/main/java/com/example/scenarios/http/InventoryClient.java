package com.example.scenarios.http;

/** Client for the inventory service. */
public class InventoryClient {

    private final ConnectionPool pool;

    public InventoryClient(ConnectionPool pool) {
        this.pool = pool;
    }

    public ConnectionPool pool() {
        return pool;
    }
}
