package sunmisc.db.agents;

import sunmisc.db.models.Collar;
import sunmisc.db.models.Pet;

public interface Collars {

    Collar collar(Pet pet);


    void add(Pet pet, String description) throws Exception;

}
