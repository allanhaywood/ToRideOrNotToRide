package applications.haywood.torideornottoride;

/**
 * Created by Allan on 5/4/2015.
 */
public class ZipCode {

    static final String  ZipCodePattern = "\\d{5}(-\\d{4})?";

    private int zipCode;

    public void setZipCodeWithInt(String zipcode)
    {
        zipcode.matches(ZipCode.ZipCodePattern);
        this.zipCode = Integer.parseInt(zipcode);
    }



}
