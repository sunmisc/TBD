package sunmisc.db.models;

public interface Owner {

    Identification identification() throws Exception;

    long id() throws Exception;

    String phone() throws Exception;

    void updatePhone(String newPhoneNumber) throws Exception;

    void add(Animal animal, String name) throws Exception;

    record Identification(String firstName, String lastName) { }
}
