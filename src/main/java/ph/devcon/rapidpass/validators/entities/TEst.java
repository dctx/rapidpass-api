package ph.devcon.rapidpass.validators.entities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TEst {
    public static void main(String[] args) {
        System.out.println("Hello World");
        String mobileNumber = "09662016919";
        System.out.println(isValidReferenceID(mobileNumber));
    }

    private static boolean isValidReferenceID(String mobileNumber){
    	final String MOBILE_NUMBER_REGEX = "^09\\d{9}$";
    	Pattern p = Pattern.compile(MOBILE_NUMBER_REGEX);
    	Matcher m = p.matcher(mobileNumber);
        return m.matches();
    }

}