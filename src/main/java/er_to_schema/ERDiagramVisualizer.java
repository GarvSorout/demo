package er_to_schema;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class ERDiagramVisualizer {
    public static void displayGraph(List<Entity> entities, List<Relationship> relationships) {
        System.setProperty("org.graphstream.ui", "swing");
        Graph graph = new SingleGraph("ER Diagram");

        Set<String> addedNodes = new HashSet<>(); // Track added nodes

        for (Entity entity : entities) {
            if (!addedNodes.contains(entity.name)) {
                Node node = graph.addNode(entity.name);
                node.setAttribute("ui.label", entity.name);
                addedNodes.add(entity.name);
            }
        }

        for (Relationship rel : relationships) {
            String relationshipNodeName = rel.entity1.name + "_" + rel.entity2.name + "_" + rel.type;
            if (!addedNodes.contains(relationshipNodeName)) { 
                Node relNode = graph.addNode(relationshipNodeName);
                relNode.setAttribute("ui.label", rel.type);
                addedNodes.add(relationshipNodeName);
            }

            Edge edge1 = graph.addEdge(rel.entity1.name + "_" + relationshipNodeName, rel.entity1.name, relationshipNodeName, true);
            Edge edge2 = graph.addEdge(relationshipNodeName + "_" + rel.entity2.name, relationshipNodeName, rel.entity2.name, true);

            if (rel.type.equals("many-to-one")) {
                edge1.setAttribute("ui.style", "arrow-shape: diamond;");
                edge2.setAttribute("ui.style", "arrow-shape: none;"); 
            } else if (rel.type.equals("one-to-one")) {
                edge1.setAttribute("ui.style", "arrow-shape: diamond;");
                edge2.setAttribute("ui.style", "arrow-shape: diamond;");
            } else if (rel.type.equals("many-to-many")) {
                // No arrows for many-to-many
                edge1.setAttribute("ui.style", "arrow-shape: none;");
                edge2.setAttribute("ui.style", "arrow-shape: none;");
            }
        }

        graph.display();
    }

    public static void printSchema(List<Entity> entities, List<Relationship> relationships) {
        for (Entity entity : entities) {
            System.out.println("CREATE TABLE " + entity.name + " (");
            // Assuming entities have attributes, I'll need to iterate through them here
            // Example:
            // for (Attribute attr : entity.attributes) {
            //     System.out.println("  " + attr.name + " " + attr.type + ","); 
            // }
            System.out.println("  PRIMARY KEY (...)"); // Define primary key based on your ER model
            System.out.println(");");
            System.out.println();
        }

        for (Relationship rel : relationships) {
            // Handle relationship-to-table mapping based on schema generation rules.
            // This will involve creating additional tables or adding foreign keys 
            // depending on the relationship type (one-to-one, one-to-many, many-to-many)
            // and database design choices. 
            // For simplicity, I'll be just print a comment indicating the relationship:
            System.out.println("-- Relationship: " + rel.toString()); 
        }
    }

    public static void main(String[] args) {
        List<Entity> entities = new ArrayList<>();
        List<Relationship> relationships = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Entity entity = ERGenerator.getRandomEntity();
            if (!entities.contains(entity)) { // Ensure unique entities
                entities.add(entity);
            }
        }

        for (int i = 0; i < 2; i++) {
            Relationship relationship = ERGenerator.getRandomRelationship(entities);

            // Add this check to prevent self-relationships:
            if (relationship.entity1 != relationship.entity2 && 
                !relationships.contains(relationship)) { 
                relationships.add(relationship);
            }
        }

        displayGraph(entities, relationships);
        printSchema(entities, relationships);
    }
}
