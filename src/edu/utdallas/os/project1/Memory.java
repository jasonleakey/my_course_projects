package edu.utdallas.os.project1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * The memory module
 * @author Jason Huang
 *
 */
public class Memory
{
    public static final int MM_SIZE = 1000;

    // memory capacity: 1000
    private int[] M = new int[MM_SIZE];

    // read the program line by line. 
    // and store instructions to the memory from Address 0
    public void loadProgram(String filename)
    {
        BufferedReader br = null;
        try 
        {
            br = new BufferedReader(new FileReader(filename));
            String line = new String();

            int addr = 0;
            while ((line = br.readLine()) != null)
            {
                M[addr++] = Integer.parseInt(line);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != br)
            {
                try
                {
                    br.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public int read(int addr)
    {
        return M[addr];
    }

    public void write(int addr, int data)
    {
        M[addr] = data;
    }

    public synchronized void run()
    {
        String cmd = null;
        Scanner scan = new Scanner(System.in);
        while ((cmd = scan.nextLine()) != null)
        {
            // analyse the command from CPU
            String[] fn = cmd.split(" ");
            if (3 == fn.length && "mm".equals(fn[0]) && "r".equals(fn[1]))
            {
                // It is a READ command
                // Then print out the value. 
                System.out
                        .println(String.valueOf(read(Integer.parseInt(fn[2]))));
            }
            else if (4 == fn.length && "mm".equals(fn[0]) && "w".equals(fn[1]))
            {
                // It is a WRITE command 
                // Then write data to the specified address 
                write(Integer.parseInt(fn[2]), Integer.parseInt(fn[3]));
            }
            else
            {
                scan.close();
                throw new IllegalArgumentException(
                        "Invalid command from CPU to Memory!");
            }
        }
        scan.close();
    }

    public static void main(String[] args)
    {
        Memory mm = new Memory();
        mm.loadProgram(args[0]);
        mm.run();
    }
}
