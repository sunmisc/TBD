package sunmisc.db.models;

public interface Pet extends Animal {

    String name();

    Collar collar();

    Owner owner();
}
