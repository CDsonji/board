package org.example.model.message;

public class Response<T>  {
    private final String status;
    private final T data;
    private final String message;

    public Response(String status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }
}
