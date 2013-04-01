package edu.utdallas.os.project2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class PostOffice
{
    // Debug Switch, used for service time adjustment.
    // since 60s are too long. 
    public static final boolean DEBUG = true;
    
    // Binary Semaphore for only one scale.
    public static Semaphore Scale = new Semaphore(1);

    // Post office capacity
    public static Semaphore Capacity = new Semaphore(10);

    // # of Postal Workers as Resources
    public static Semaphore AvailableWorker = new Semaphore(3);

    // Indicating the postal worker object has been written in the shared
    // variable and is waiting in front of queue.
    public static Semaphore CustomerReady = new Semaphore(0);
    
    // Acknowledgment of information exchange.
    public static Semaphore Ack = new Semaphore(0);

    // Limit other customers to interfere the information exchange process.
    public static Semaphore customer_mutex = new Semaphore(1);
    
    // Limit other workers to interfere the information exchange process.
    public static Semaphore workerer_mutex = new Semaphore(1);
    
    // shared variables mutual exclusion lock
    public static Semaphore shared_mutex = new Semaphore(1);

    // Indicating the postal worker object has been written in the shared
    // variables
    public static Semaphore WorkerReady = new Semaphore(0);

    // shared variable for information exchange
    public static Customer customer;

    // shared variable for information exchange
    public static PostalWorker worker;

    // # of customers
    private final static int NoCustomer = 50;

    // # of Postal Workers
    private final static int NoPostalWorker = 3;

    public void simulate()
    {
        System.out.println("Simulating Post Office with " + NoCustomer
                + " customers and " + NoPostalWorker + " postal workers");
        List<Thread> customers = new ArrayList<Thread>();
        List<Thread> postalworkers = new ArrayList<Thread>();

        // initialize all the customer threads.
        for (int i = 0; i < NoCustomer; i++)
        {
            Thread th = new Thread(new Customer(i));
            customers.add(th);
        }
        // initialize all the postal worker threads.
        for (int i = 0; i < NoPostalWorker; i++)
        {
            Thread th = new Thread(new PostalWorker(i));
            postalworkers.add(th);
        }

        // Start postal worker threads first
        for (Thread th : postalworkers)
        {
            th.start();
        }
        // Then waiting for the customer coming.
        for (Thread th : customers)
        {
            th.start();
        }

        try
        {
            // All customers are served.
            for (Thread th : customers)
            {
                th.join();
            }
            // Then kill the child postal worker threads.
            for (Thread th : postalworkers)
            {
                th.interrupt();
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        new PostOffice().simulate();
    }
}
