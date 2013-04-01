package edu.utdallas.os.project2;

import java.util.Random;

public enum TaskType
{
    BUY_STAMP(0), MAIL_LETTER(1), MAIL_PACKAGE(2);
    
    private int type;
    
    private TaskType(int type)
    {
        this.type = type;
    }
    
    public static TaskType getRandType()
    {
        Random rand = new Random();
        int x = rand.nextInt(3);
        switch (x)
        {
            case 0:
                return TaskType.BUY_STAMP;
            case 1:
                return TaskType.MAIL_LETTER;
            case 2:
                return TaskType.MAIL_PACKAGE;
            default:
                return null;
        }
    }
    
    @Override
    public String toString()
    {
        switch (type)
        {
            case 0:
                return "buy stamps";
            case 1:
                return "mail a letter";
            case 2:
                return "mail a package";
            default:
                return null;
        }
    }
}
