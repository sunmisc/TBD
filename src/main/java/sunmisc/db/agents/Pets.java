package sunmisc.db.agents;

import sunmisc.db.models.Animal;
import sunmisc.db.models.Owner;
import sunmisc.db.models.Pet;

import java.util.stream.Stream;

public interface Pets {

    Pet pet(long id);

    void add(Owner owner,
             Animal animal,
             String name
    ) throws Exception;

    Stream<Pet> pets(Owner owner);
}
