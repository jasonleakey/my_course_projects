package edu.utdallas.os.project2;

import java.util.concurrent.Semaphore;

/**
 * Customer Thread 
 * @author Jason Huang
 *
 */
public class Customer implements Runnable
{
    // service finished
    public Semaphore finish = new Semaphore(0);
    
    // request for service
    public Semaphore request = new Semaphore(0);
    
    // greeting from the postal worker
    public Semaphore greeting = new Semaphore(0);

    // Task
    private TaskType taskType;
    
    // The postal worker who serves me
    private PostalWorker currentPostalWorker;
    
    // Customer ID
    private int id;

    public Customer(int id)
    {
        taskType = TaskType.getRandType();
        this.id = id;
    }
    
    public TaskType getTaskType()
    {
        return taskType;
    }
    
    public final int getId()
    {
        return id;
    }
    
    @Override
    public void run()
    {
        try
        {
            System.out.println("Customer " + id + " created");
            // request to enter the post office <<<<<<<
            PostOffice.Capacity.acquire();
            System.out.println("Customer " + id + " enters post office");
            
            // Waiting in the queue <<<<<<<
            PostOffice.AvailableWorker.acquire();
            
            /* Exchange Information */
            PostOffice.customer_mutex.acquire();
            
            // Send the customer object to the worker
            // Through Global variables. >>>>>>
            PostOffice.shared_mutex.acquire();
            PostOffice.customer = this;
            PostOffice.shared_mutex.release();
            // To notify information sent. >>>>>>
            PostOffice.CustomerReady.release();
            
            // waiting the worker object. <<<<<<<
            PostOffice.WorkerReady.acquire();
            PostOffice.shared_mutex.acquire();
            currentPostalWorker = PostOffice.worker;
            PostOffice.shared_mutex.release();
            
            // information saved. 
            PostOffice.Ack.release();
            
            /* End of Exchange Information */
            PostOffice.customer_mutex.release();
            
            // Waiting the greeting from the postal worker
            // who serves the customer <<<<<<<
            greeting.acquire();
            System.out.println("Customer " + id
                    + " asks postal worker " + currentPostalWorker.getId() + " to " + taskType);
            
            // Ask for service >>>>>>
            request.release();
            
            // Waiting service to be done. <<<<<<<<
            finish.acquire();
            System.out.println("Customer " + id + " finished " + taskType);
            
            // Walk out the hall
            System.out.println("Customer " + id + " leaves post office");
            PostOffice.Capacity.release();
            
            // Customer thread dies. 
            System.out.println("Joined customer " + id);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            return;
        }
    }

}
