package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import play.Configuration;
import play.Logger;
import play.Play;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.i18n.Messages;
import utils.Mail;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Hao on 2015/7/19.
 */
@Entity
@Table(name = "tokens")
public class Token extends Model {
    // Reset tokens will expire after a day.
    private static final int EXPIRATION_DAYS = 1;

    public enum TypeToken {
        password("reset"), email("email");
        private String urlPath;

        TypeToken(String urlPath) {
            this.urlPath = urlPath;
        }

    }

    @Id
    public String token;

    @Constraints.Required
    @Formats.NonEmpty
    public String userId;

    @Constraints.Required
    @Enumerated(EnumType.STRING)
    public TypeToken type;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date dateCreation;

    @Constraints.Required
    @Formats.NonEmpty
    public String email;

    //---------------------Queries--------------------------------
    public static EbeanServer currentServer(String database){

        return Ebean.getServer(database);

    }

//    @Override
//    public void save(String dbserver) {
//
//        Ebean.getServer(dbserver).save(this);
//        // super.save();
//    }
    /**
     * Retrieve a token by id and type.
     *
     * @param token token Id
     * @param type  type of token
     * @return a resetToken
     */
    public static Token findByTokenAndType(String token, TypeToken type,String database) {
        return currentServer(database).find(Token.class).where().eq("token", token).eq("type", type).findUnique();
    }

    public static User findUserByToken(Token token){

        User tokenuser=currentServer("global").find(User.class).where().eq("id",token.userId).findUnique();
        return tokenuser;
    }

    public static Token findTokenByUserId(String userid,String database){
        return currentServer(database).find(Token.class).where().eq("userId",userid).findUnique();
    }
    /**
     * @return true if the reset token is too old to use, false otherwise.
     */
    public boolean isExpired() {
        return dateCreation != null && dateCreation.before(expirationTime());
    }

    /**
     * @return a date before which the password link has expired.
     */
    private Date expirationTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, -EXPIRATION_DAYS);
        return cal.getTime();
    }

    /**
     * Return a new Token.
     *
     * @param user  user
     * @param type  type of token
     * @param email email for a token change email
     * @return a reset token
     */
    private static Token getNewToken(User user, TypeToken type, String email,String database) {
        Token token = new Token();
        token.token = UUID.randomUUID().toString();
        token.userId = user.id;
        token.type = type;
        token.email = email;
        token.dateCreation=new Date();
        token.save(database);
        return token;
    }

    /**
     * Send the Email to confirm ask new password.
     *
     * @param user the current user
     * @throws java.net.MalformedURLException if token is wrong.
     */
    public static void sendMailResetPassword(User user,String database) throws MalformedURLException {
        sendMail(user, TypeToken.password, null,database);
    }

    /**
     * Send the Email to confirm ask new password.
     *
     * @param user  the current user
     * @param email email for a change email token
     * @throws java.net.MalformedURLException if token is wrong.
     */
    public static void sendMailChangeMail(User user, @Nullable String email,String database) throws MalformedURLException {
        sendMail(user, TypeToken.email, email,database);
    }

    /**
     * Send the Email to confirm ask new password.
     *
     * @param user  the current user
     * @param type  token type
     * @param email email for a change email token
     * @throws java.net.MalformedURLException if token is wrong.
     */
    private static void sendMail(User user, TypeToken type, String email,String database) throws MalformedURLException {

        Token token = getNewToken(user, type, email,database);
        String externalServer = Configuration.root().getString("server.hostname");
        Logger.info("url is here " + Play.application().path().getPath());

        String subject = null;
        String message = null;
        String toMail = null;

        // Should use reverse routing here.
        String urlString = urlString = "http://" + externalServer + "/" + type.urlPath + "/" + token.token;
        URL url = new URL(urlString); // validate the URL

        switch (type) {
            case password:
                subject = Messages.get("mail.reset.ask.subject");
                message = Messages.get("mail.reset.ask.message", url.toString());
                toMail = user.email;
                break;
            case email:
                subject = Messages.get("mail.change.ask.subject");
                message = Messages.get("mail.change.ask.message", url.toString());
                toMail = token.email; // == email parameter
                break;
        }

        Logger.debug("sendMailResetLink: url = " + url);
        Mail.Envelop envelop = new Mail.Envelop(subject, message, toMail);
        Mail.sendMail(envelop);
    }
}
