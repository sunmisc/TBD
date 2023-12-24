package sunmisc.db.models;

public interface Owner {

    Identification identification();

    long id() throws Exception;

    String phone() throws Exception;

    void add(Animal animal, String name) throws Exception;

    record Identification(String firstName, String lastName) { }
}
