package sunmisc.db.models;

public interface Collar {

    long id() throws Exception;

    Pet pet() throws Exception;

    String description() throws Exception;

    void updateDescription(String description) throws Exception;

    void invalidate() throws Exception;
}
