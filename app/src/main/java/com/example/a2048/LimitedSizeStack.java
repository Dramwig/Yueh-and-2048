package com.example.a2048;

import java.util.LinkedList;

public class LimitedSizeStack<T> {
    private static final int MAX_STACK_SIZE = 10;
    private LinkedList<T> stack = new LinkedList<>();

    public void push(T data) {
        stack.addLast(data);
        if (stack.size() > MAX_STACK_SIZE) {
            stack.removeFirst();  // Remove the oldest element from the front
        }
    }

    public T pop() {
        return stack.size() > 0 ? stack.removeLast() : null;  // Remove and return the last element
    }

    public int size() {
        return stack.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void clear() {
        stack.clear();
    }
}
