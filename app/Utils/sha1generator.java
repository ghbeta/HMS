package Utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Hao on 2015/5/17.
 */
public class sha1generator {

    public static String generateSha1(String matrikelnummer){
        String sha1="";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.reset();
            md.update(matrikelnummer.getBytes("UTF-8"));
            sha1= byteToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return sha1;

    }

    public static String byteToHex(byte[] input){
        String output = "";
        for (int i=0;i<input.length;i++){
            output += Integer.toString((input[i] & 0xff) + 0x100,16).substring(1);
        }
        return output;
    }
}
