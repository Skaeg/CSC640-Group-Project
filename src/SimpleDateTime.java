import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 11/12/13
 * Time: 7:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleDateTime implements Serializable
{
    private int hour = 0;
    private int minute = 0;
    private int second = 0;
    private int date = 0;
    private int month = 0;
    private int year = 0;

    public SimpleDateTime(){};

    public SimpleDateTime(int month, int date, int year)
    {
        this.hour = 0;
        this.minute = 0;
        this.second = 0;
        this.date = date;
        this.month = month;
        this.year = year;
    }

    public SimpleDateTime(int hour, int minute, int second, int date, int month, int year)
    {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.date = date;
        this.month = month;
        this.year = year;
    }

    public int getHour()
    {
        return hour;
    }

    public void setHour(int hour)
    {
        this.hour = hour;
    }

    public int getMinute()
    {
        return minute;
    }

    public void setMinute(int minute)
    {
        this.minute = minute;
    }

    public int getSecond()
    {
        return second;
    }

    public void setSecond(int second)
    {
        this.second = second;
    }

    public int getDate()
    {
        return date;
    }

    public void setDate(int date)
    {
        this.date = date;
    }

    public int getMonth()
    {
        return month;
    }

    public void setMonth(int month)
    {
        this.month = month;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    @Override
    public String toString()
    {
        return String.format("%0d-%0d-%d %0d:%0d:%0d", month, date, year, hour, minute, second);
    }
}
