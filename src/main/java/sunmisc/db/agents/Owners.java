package sunmisc.db.agents;

import sunmisc.db.models.Owner;

public interface Owners {

    void add(Owner.Identification identifier,
             String phone) throws Exception;

    Owner owner(Owner.Identification identifier);
}
