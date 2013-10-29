/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/16/13
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Service
{
    private int serviceCode = -1;       //6 digits
    private String serviceName = "";      //6 digits
    private Double serviceFee = -1.0d;

    public Service(String serviceName, Double serviceFee, int serviceCode)
    {
        this.serviceName = serviceName;
        this.serviceFee = serviceFee;
        this.serviceCode = serviceCode;
    }

    int getServiceCode(){
      return this.serviceCode;
    }

    void setServiceCode(int serviceCode){
       this.serviceCode = serviceCode;
    }


    String getServiceName(){
       return this.serviceName;
    }
    void setServiceName(String serviceName){
        this.serviceName = serviceName;
    }

    Double getServiceFee(){
        return this.serviceFee;
    }
    void setServiceFee(Double serviceFee){
        this.serviceFee = serviceFee;
    }
}
