package org.example.model.message;

public class Request<T>  {
    private final String command;
    private final T payload;

    public Request(String command,T payload) {
        this.command = command;
        this.payload = payload;
    }

    public String getCommand() {
        return command;
    }

    public T getPayload() {
        return payload;
    }
}
