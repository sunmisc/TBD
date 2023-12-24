package sunmisc.db.agents;

import sunmisc.db.models.Pet;

public interface Collars {

    void add(Pet pet, String description) throws Exception;
}
