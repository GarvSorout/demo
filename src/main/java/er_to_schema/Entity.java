package er_to_schema;
import java.util.*;

class Entity {
    String name;
    List<String> attributes;

    public Entity(String name, List<String> attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return name + attributes;
    }
}

class Relationship {
    Entity entity1;
    Entity entity2;
    String type; // "one-to-one", "many-to-one" or "many-to-many"

    public Relationship(Entity e1, Entity e2, String type) {
        this.entity1 = e1;
        this.entity2 = e2;
        this.type = type;
    }

    @Override
    public String toString() {
        return entity1.name + " --(" + type + ")--> " + entity2.name;
    }
}


