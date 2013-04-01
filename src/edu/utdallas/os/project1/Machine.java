package edu.utdallas.os.project1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The Computer class including I/O module.
 */
public class Machine
{
    // The Memory Process
    private Process memory = null;

    // The CPU Process
    private Process cpu = null;

    private Scanner scan = null;

    // These two methods are to emulate I/O functions.
    private int input(int port) throws InputMismatchException, IOException
    {
        if (1 == port)
        {
            // reads an int from the keyboard
            return scan.nextInt();
        }
        else if (2 == port)
        {
            // reads a char from the keyboard
            return System.in.read();
        }
        else
        {
            System.err.println("Invalid Port!");
            return Integer.MIN_VALUE;
        }
    }

    private void output(int port, int data)
    {
        if (1 == port)
        {
            // writes an int to the screen
            System.out.print(data);
        }
        else if (2 == port)
        {
            // writes a char to the screen
            System.out.print((char) data);
        }
        else
        {
            System.err.println("Invalid Port!");
        }
    }

    // Act as a system bus
    public synchronized boolean runProgram(String filename)
    {
        Runtime run = Runtime.getRuntime();
        String instruction = null;
        BufferedReader cpuBr = null;
        PrintWriter cpuWr = null;
        BufferedReader mmBr = null;
        PrintWriter mmWr = null;
        try
        {
            scan = new Scanner(System.in);
            // create a child process to emulate CPU
            cpu = run.exec("java -cp * edu.utdallas.os.CPU");
            // create a child process to emulate Memory
            memory = run.exec("java -cp * edu.utdallas.os.Memory " + filename);

            // get the input and output streams of the CPU process and memory
            // process.
            cpuBr = new BufferedReader(new InputStreamReader(
                    cpu.getInputStream()));
            cpuWr = new PrintWriter(cpu.getOutputStream());
            mmBr = new BufferedReader(new InputStreamReader(
                    memory.getInputStream()));
            mmWr = new PrintWriter(memory.getOutputStream());

            // read the instructions
            while (null != (instruction = cpuBr.readLine()))
            {
                String[] fn = instruction.split(" ");
                if (3 == fn.length && "io".equals(fn[0]) && "r".equals(fn[1]))
                {
                    // It is a IO READ command from CPU
                    // Then get an int or a char from Standard Input Stream.
                    cpuWr.println(String.valueOf(input(Integer.parseInt(fn[2]))));
                    cpuWr.flush();
                }
                else if (4 == fn.length && "io".equals(fn[0])
                        && "w".equals(fn[1]))
                {
                    // It is a IO WRITE command from CPU
                    // Then print an int or a char to Screen.
                    output(Integer.parseInt(fn[2]), Integer.parseInt(fn[3]));
                }
                else if (3 == fn.length && "mm".equals(fn[0])
                        && "r".equals(fn[1]))
                {
                    // It is a memory READ command
                    // Then pass the message to memory.
                    mmWr.println(instruction);
                    mmWr.flush();
                    cpuWr.println(mmBr.readLine());
                    cpuWr.flush();
                }
                else if (4 == fn.length && "mm".equals(fn[0])
                        && "w".equals(fn[1]))
                {
                    // It is a memory WRITE command
                    // Then pass the message to memory.
                    mmWr.println(instruction);
                    mmWr.flush();
                }
            }
            cpu.waitFor();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (InputMismatchException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            // kill all child processes.
            memory.destroy();
            cpu.destroy();
            scan.close();
            if (null != cpuBr)
            {
                try
                {
                    cpuBr.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (null != cpuWr)
            {
                cpuWr.close();
            }
            if (null != mmBr)
            {
                try
                {
                    mmBr.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (null != mmWr)
            {
                mmWr.close();
            }
        }

        return true;
    }

    public static void main(String[] args)
    {
        // If no arguments are provided
        // then as default load "program.txt"
        if (args.length <= 0)
        {
            new Machine().runProgram("program.txt");
        }
        else
        {
            new Machine().runProgram(args[0]);
        }
    }
}
