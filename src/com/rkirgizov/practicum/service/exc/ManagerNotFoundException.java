package com.rkirgizov.practicum.service.exc;

public class ManagerNotFoundException extends RuntimeException {
    public ManagerNotFoundException(String message) { super(message); }
}
