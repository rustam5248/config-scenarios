package com.example.scenarios.http;

/** Client for the shipping service. */
public class ShippingClient {

    private final ConnectionPool pool;

    public ShippingClient(ConnectionPool pool) {
        this.pool = pool;
    }

    public ConnectionPool pool() {
        return pool;
    }
}
