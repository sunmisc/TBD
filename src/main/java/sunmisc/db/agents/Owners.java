package sunmisc.db.agents;

import sunmisc.db.models.Owner;

import java.util.Optional;
import java.util.stream.Stream;

public interface Owners {

    Owner owner(long id);

    void add(Owner.Identification identifier,
             String phone) throws Exception;

    Optional<Owner> owner(String phone);

    Stream<Owner> owners(Owner.Identification identifier);



}
