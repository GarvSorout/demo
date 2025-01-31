package er_to_schema;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

class ERGenerator {
    private static final String[] entityNames = {"User", "Order", "Product", "Category"};
    private static final String[][] attributes = {
            {"id", "name", "email"},
            {"id", "order_date", "total"},
            {"id", "name", "price"},
            {"id", "category_name"}
    };
    private static final String[] relationshipTypes = {"many-to-one", "many-to-many"};

    public static Entity getRandomEntity() {
        int index = new Random().nextInt(entityNames.length);
        List<String> randomAttrs = Arrays.asList(attributes[index]);
        return new Entity(entityNames[index], randomAttrs);
    }

    public static Relationship getRandomRelationship(List<Entity> entities) {
        Random rand = new Random();
        Entity e1 = entities.get(rand.nextInt(entities.size()));
        Entity e2 = entities.get(rand.nextInt(entities.size()));

        while (e1 == e2) { // Avoid self-relationships
            e2 = entities.get(rand.nextInt(entities.size()));
        }

        String type = relationshipTypes[rand.nextInt(relationshipTypes.length)];
        return new Relationship(e1, e2, type);
    }
}
