/**
 * Created with IntelliJ IDEA.
 * User: Steve
 * Date: 10/16/13
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Service
{
    private int serviceCode = -1;
    private String serviceName = "";
    private Double serviceFee = -1.0d;

    int getServiceCode(){
      return this.serviceCode;
}
    String getServiceName(){
       return this.serviceName;
    }

    Double getServiceFee(){
        return this.serviceFee;
    }
}
