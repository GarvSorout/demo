package er_to_schema;

import java.util.ArrayList;
import java.util.List;

public class SchemaGenerator {
    public static String generateSQLSchema(List<Entity> entities, List<Relationship> relationships) {
        StringBuilder schema = new StringBuilder();

        for (Entity entity : entities) {
            schema.append("CREATE TABLE ").append(entity.name).append(" (\n");
            for (String attr : entity.attributes) {
                schema.append("    ").append(attr).append(" VARCHAR(255),\n");
            }
            schema.append("    PRIMARY KEY (id)\n);\n\n");
        }

        for (Relationship rel : relationships) {
            if (rel.type.equals("one-to-many")) {
                schema.append("ALTER TABLE ").append(rel.entity2.name)
                        .append(" ADD FOREIGN KEY (").append(rel.entity1.name).append("_id) ")
                        .append("REFERENCES ").append(rel.entity1.name).append("(id);\n\n");
            } else {
                schema.append("CREATE TABLE ").append(rel.entity1.name).append("_").append(rel.entity2.name).append("_link (\n")
                        .append("    ").append(rel.entity1.name).append("_id INT,\n")
                        .append("    ").append(rel.entity2.name).append("_id INT,\n")
                        .append("    PRIMARY KEY (").append(rel.entity1.name).append("_id, ").append(rel.entity2.name).append("_id),\n")
                        .append("    FOREIGN KEY (").append(rel.entity1.name).append("_id) REFERENCES ").append(rel.entity1.name).append("(id),\n")
                        .append("    FOREIGN KEY (").append(rel.entity2.name).append("_id) REFERENCES ").append(rel.entity2.name).append("(id)\n")
                        .append(");\n\n");
            }
        }
        return schema.toString();
    }

    public static void main(String[] args) {
        List<Entity> entities = new ArrayList<>();
        List<Relationship> relationships = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            entities.add(ERGenerator.getRandomEntity());
        }

        for (int i = 0; i < 2; i++) {
            relationships.add(ERGenerator.getRandomRelationship(entities));
        }

        String schema = generateSQLSchema(entities, relationships);
        System.out.println(schema);
    }
}

