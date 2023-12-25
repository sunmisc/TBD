package sunmisc.db.agents;

import sunmisc.db.models.Owner;

import java.util.Optional;
import java.util.stream.Stream;

public interface Owners {

    void add(Owner.Identification identifier,
             String phone) throws Exception;

    Stream<Owner> owners(Owner.Identification identifier);


    Optional<Owner> owner(String phone);
}
