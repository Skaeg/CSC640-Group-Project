import java.util.Scanner;

public class TerminalInputOutput implements iTerminal
{
    static Scanner input = new Scanner(System.in);

    @Override
    public String getInput(String prompt)
    {
        System.out.println(prompt + "\r\n");
        return input.nextLine();
    }

    @Override
    public void sendOutput(String message)
    {
        System.out.println(message);
    }

}