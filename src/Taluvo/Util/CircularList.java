package Taluvo.Util;

public class CircularList<T>
{
    private Node head;
    private Node tail;
    private Node current;

    public CircularList(T head)
    {
        Node node = new Node(head);
        this.tail = this.head = this.current = node;
        this.tail.next = this.tail;
    }

    public void add(T value)
    {
        this.tail.next = new Node(value);
        this.tail = this.tail.next;
        this.tail.next = this.head;
    }

    public T next()
    {
        T ret = current.value;
        current = current.next;
        return ret;
    }

    public T peek()
    {
        return current.value;
    }

    private class Node
    {
        Node next;
        T value;

        Node(T value)
        {
            this.value = value;
        }
    }
}
