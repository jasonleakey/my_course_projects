package edu.utdallas.os.project2;

public class PostalWorker implements Runnable
{
    // Postal Worker ID
    private int id;

    // The customer who the postal worker is serving
    private Customer currentCustomer;

    public PostalWorker(int id)
    {
        this.id = id;
    }

    public final int getId()
    {
        return id;
    }

    @Override
    public void run()
    {
        System.out.println("Postal worker " + id + " created");
        try
        {
            while (true)
            {
                // Waiting the customer coming. <<<<<<<<
                PostOffice.CustomerReady.acquire();

                PostOffice.workerer_mutex.acquire();
                // Receive the customer object and send back
                // the worker object itself. <<<<<<<<<
                PostOffice.shared_mutex.acquire();
                currentCustomer = PostOffice.customer;
                PostOffice.worker = this;
                PostOffice.shared_mutex.release();
                // Notify the information is sent. >>>>>>>>
                PostOffice.WorkerReady.release();

                // information saved by the customer.
                PostOffice.Ack.acquire();
                PostOffice.workerer_mutex.release();

                // Greet to the customer >>>>>>>
                System.out.println("Postal worker " + id + " serving customer "
                        + currentCustomer.getId());
                currentCustomer.greeting.release();

                // Received a service request from the customer. <<<<<<
                currentCustomer.request.acquire();

                // start service
                switch (currentCustomer.getTaskType())
                {
                    case BUY_STAMP:
                        if (PostOffice.DEBUG)
                        {
                            Thread.sleep(60);
                            Thread.sleep(1);
                        }
                        else
                        {
                            Thread.sleep(60000);
                            Thread.sleep(1000);
                        }
                        break;
                    case MAIL_LETTER:
                        if (PostOffice.DEBUG)
                        {
                            Thread.sleep(60);
                            Thread.sleep(1);
                            Thread.sleep(30);
                        }
                        else
                        {
                            Thread.sleep(60000);
                            Thread.sleep(1000);
                            Thread.sleep(30000);
                        }
                        break;
                    case MAIL_PACKAGE:
                        PostOffice.Scale.acquire();
                        System.out.println("Scales in use by postal worker "
                                + id);
                        if (PostOffice.DEBUG)
                        {
                            Thread.sleep(60);
                            Thread.sleep(1);
                            Thread.sleep(60);
                            Thread.sleep(1);
                        }
                        else
                        {
                            Thread.sleep(60000);
                            Thread.sleep(1000);
                            Thread.sleep(60000);
                            Thread.sleep(1000);
                        }
                        System.out.println("Scales released by postal worker "
                                + id);
                        PostOffice.Scale.release();
                        break;
                }

                // Finish serving customer
                // notify service done. >>>>>>>>>>>>
                System.out.println("Postal worker " + id
                        + " finished serving customer "
                        + currentCustomer.getId());
                currentCustomer.finish.release();

                // "Next, Please..."
                PostOffice.AvailableWorker.release();
            }
        }
        catch (InterruptedException e)
        {
            System.out.println("Joined postal worker " + id);
            return;
        }
    }
}
