package sunmisc.db.agents;

import sunmisc.db.models.Animal;

import java.util.stream.Stream;

public interface Animals {

    void add(String type) throws Exception;


    Stream<Animal> animalsByType(String type);
}
