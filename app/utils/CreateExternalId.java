package utils;

import java.util.UUID;

/**
 * Created by Hao on 2015/10/3.
 */
public class CreateExternalId {

    public static String generateId(){
        String numberpart= String.valueOf(UUID.randomUUID());
        return "extern"+numberpart;
    }
}
