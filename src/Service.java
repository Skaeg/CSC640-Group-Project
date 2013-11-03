import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/16/13
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Service implements Serializable
{
    private int serviceCode;       //6 digits
    private String serviceName;      //6 digits
    private Double serviceFee;

    public Service(){}
    public Service(String serviceName, Double serviceFee, int serviceCode)
    {
        this.serviceName = serviceName;
        this.serviceFee = serviceFee;
        this.serviceCode = serviceCode;
    }

    public int getServiceCode(){
      return this.serviceCode;
    }

    public void setServiceCode(int serviceCode){
       this.serviceCode = serviceCode;
    }

    public String getServiceName(){
       return this.serviceName;
    }

    public void setServiceName(String serviceName){
        this.serviceName = serviceName;
    }

    public Double getServiceFee(){
        return this.serviceFee;
    }

    public void setServiceFee(Double serviceFee){
        this.serviceFee = serviceFee;
    }
}
