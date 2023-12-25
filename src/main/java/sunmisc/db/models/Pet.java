package sunmisc.db.models;

public interface Pet extends Animal {

    String name() throws Exception;

    Collar collar() throws Exception;

    Owner owner() throws Exception;
}
