package pt.utl.ist.lucene.web.assessements;

/**
 * Created by IntelliJ IDEA.
 * User: jmachado
 * Date: 17/Fev/2010
 * Time: 12:35:16
 * To change this template use File | Settings | File Templates.
 */
public class User
{
    boolean admin;
    String username;

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" +
                "admin=" + admin +
                ", username='" + username + '\'' +
                '}';
    }
}
