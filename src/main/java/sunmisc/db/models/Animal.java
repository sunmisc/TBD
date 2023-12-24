package sunmisc.db.models;

public interface Animal {

    Live live() throws Exception;

    long id() throws Exception;

    void die() throws Exception;

    String type() throws Exception;
}
