package com.rkirgizov.practicum.service.impl;

import com.rkirgizov.practicum.model.Task;
import com.rkirgizov.practicum.service.HistoryManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryManagerImpl<T extends Task> implements HistoryManager<T> {
    private final Map<Integer, Node<T>> history = new HashMap<>();
    private Node<T> first;
    private Node<T> last;

    @Override
    public void addHistory(T task) {
        if (task == null) {
            return;
        }
        Node<T> node = history.get(task.getId());
        if (node != null) {
            removeNode(node);
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
    }

    @Override
    public List<T> getHistory() {
        return getTasks();
    }

    private void linkLast(T element) {
        final Node<T> oldLast = last;
        final Node<T> newNode = new Node<>(oldLast, element, null);
        last = newNode;
        history.put(element.getId(), newNode);
        if (oldLast == null) {
            first = newNode;
        } else {
            oldLast.next = newNode;
        }
    }

    private List<T> getTasks() {
        List<T> tasks = new ArrayList<>();
        Node<T> currentNode = first;
        while (!(currentNode == null)) {
            tasks.add(currentNode.task);
            currentNode = currentNode.next;
        }
        return tasks;
    }

    private void removeNode(Node<T> node) {
        if (!(node == null)) {
            final Node<T> next = node.next;
            final Node<T> prev = node.prev;
            history.remove(node.task.getId());
            if (first == node && last == node) {
                first = null;
                last = null;
            } else if (first == node) {
                first = next;
                first.prev = null;
            } else if (last == node) {
                last = prev;
                last.next = null;
            } else {
                prev.next = next;
                next.prev = prev;
            }
        }
    }

    public static class Node<T> {
        public T task;
        public Node<T> prev;
        public Node<T> next;

        public Node(Node<T> prev, T task, Node<T> next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }

}
