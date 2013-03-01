package edu.utdallas.os;

import java.util.Scanner;

/**
 * The CPU module
 *
 */
public class CPU
{
    // six registers
    private int PC = 0;

    // SP is initialized to the end of Memory.
    private int SP = Memory.MM_SIZE - 1;

    private int IR = 0;

    private int AC = 0;

    private int X = 0;

    private int Y = 0;

    // use Scanner for input
    private Scanner scan = null;

    /**
     * fetch an instruction
     */
    public void fetch()
    {
        // get an instrution from memory and PC increment by 1
        IR = readMM(PC++);
    }

    /**
     * execute an instruction
     * 
     * @return return false if the program reaches the end or encounters an
     *         invalid instruction code, otherwise return true.
     */
    public boolean exec()
    {
        switch (IR)
        {
            case 1:
                // Load the value into the AC
                AC = readMM(PC++);
                break;
            case 2:
                // Load the value at the address into the AC
                AC = readMM(readMM(PC++));
                break;
            case 3:
                // Store the value in the AC into the address
                writeMM(readMM(PC++), AC);
                break;
            case 4:
                // Add the value in X to the AC
                AC += X;
                break;
            case 5:
                // Add the value in Y to the AC
                AC += Y;
                break;
            case 6:
                // Subtract the value in X from the AC
                AC -= X;
                break;
            case 7:
                // Subtract the value in Y from the AC
                AC -= Y;
                break;
            case 8:
                // If port=1, reads an int from the keyboard, stores it in the AC
                // If port=2, reads a char from the keyboard, stores it in the AC
                int port = readMM(PC++);
                AC = readIO(port);
                break;
            case 9:
                // If port=1, writes an int to the screen
                // If port=2, writes a char to the screen
                port = readMM(PC++);
                writeIO(port, AC);
                break;
            case 10:
                // Copy the value in the AC to X
                X = AC;
                break;
            case 11:
                // Copy the value in the AC to Y
                Y = AC;
                break;
            case 12:
                // Copy the value in X to the AC
                AC = X;
                break;
            case 13:
                // Copy the value in Y to the AC
                AC = Y;
                break;
            case 14:
                // Jump to the address
                PC = readMM(PC);
                break;
            case 15:
                // Jump to the address only if the value in the AC is zero
                if (0 == AC)
                {
                    PC = readMM(PC);
                }
                else
                {
                    PC++;
                }
                break;
            case 16:
                // Jump to the address only if the value in the AC is not zero
                if (0 != AC)
                {
                    PC = readMM(PC);
                }
                else
                {
                    PC++;
                }
                break;
            case 17:
                // Push return address onto stack, jump to the address
                int oldPC = PC + 1;
                writeMM(SP--, oldPC);
                PC = readMM(PC);
                break;
            case 18:
                // Pop return address from the stack, jump to the address
                PC = readMM(++SP);
                break;
            case 19:
                // Increment the value in X
                X++;
                break;
            case 20:
                // Decrement the value in X
                X--;
                break;
            case 30:
                // End execution
                return false;
            default:
                // invalid instruction code. 
                System.err.println("Unsupported Instruction Code!");
                return false;
        }
        // successfully executed the instruction, then return true
        return true;
    }

    // use streams instead of direct function invocation to communicate with memory 
    // here read from memory. 
    private int readMM(int addr)
    {
        System.out.println("mm r " + String.valueOf(addr));
        return scan.nextInt();
    }

    // use streams instead of direct function invocation to communicate with memory 
    // here write data to memory. 
    private void writeMM(int addr, int data)
    {
        System.out.println("mm w " + String.valueOf(addr) + " "
                + String.valueOf(data));
    }

    // use streams instead of direct function invocation to communicate with I/O 
    // here read data to I/O. 
    private int readIO(int port)
    {
        System.out.println("io r " + String.valueOf(port));
        return scan.nextInt();
    }

    // use streams instead of direct function invocation to communicate with I/O 
    // here write data to I/O 
    private void writeIO(int port, int data)
    {
        System.out.println("io w " + String.valueOf(port) + " "
                + String.valueOf(data));
    }

    public synchronized void run()
    {
        scan = new Scanner(System.in);
        // fetch and execute instructions until the end of program. 
        boolean isEnd = false;
        do
        {
            fetch();
            isEnd = exec();
        }
        while (isEnd);
        scan.close();
    }

    public static void main(String[] args)
    {
        CPU cpu = new CPU();
        cpu.run();
    }
}
