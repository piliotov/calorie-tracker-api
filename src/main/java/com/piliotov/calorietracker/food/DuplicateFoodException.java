package com.piliotov.calorietracker.food;

public class DuplicateFoodException extends RuntimeException {

    public DuplicateFoodException(String name) {
        super("Food '" + name + "' already exists");
    }
}
