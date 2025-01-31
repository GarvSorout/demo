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
            for (int i = 0; i < entity.attributes.size(); i++) {
                String attribute = entity.attributes.get(i);
                System.out.print("  " + attribute);
                if (i == 0) { // Assuming the first attribute is always the primary key
                    System.out.println(" INT PRIMARY KEY,"); 
                } else {
                    System.out.println(" VARCHAR(255),"); 
                }
            }
            System.out.println(");");
            System.out.println();
        }


        for (Relationship rel : relationships) {
            if (rel.type.equals("many-to-many")) {
                // Create a junction table for many-to-many relationships
                System.out.println("CREATE TABLE " + rel.entity1.name + "_" + rel.entity2.name + " (");
                System.out.println("  " + rel.entity1.name.toLowerCase() + "_id INT,");
                System.out.println("  " + rel.entity2.name.toLowerCase() + "_id INT,");
                System.out.println("  PRIMARY KEY (" + rel.entity1.name.toLowerCase() + "_id, " + 
                                                   rel.entity2.name.toLowerCase() + "_id),");
                System.out.println("  FOREIGN KEY (" + rel.entity1.name.toLowerCase() + "_id) REFERENCES " + 
                                                   rel.entity1.name + "(id),");
                System.out.println("  FOREIGN KEY (" + rel.entity2.name.toLowerCase() + "_id) REFERENCES " + 
                                                   rel.entity2.name + "(id)");
                System.out.println(");");
                System.out.println();
            } else {
                // For one-to-one and many-to-one, add a foreign key to the "many" side 
                Entity manySide = (rel.type.equals("one-to-many")) ? rel.entity2 : rel.entity1; 
                System.out.println("ALTER TABLE " + manySide.name + " ADD COLUMN " + 
                                   rel.entity1.name.toLowerCase() + "_id INT,");
                System.out.println("ADD FOREIGN KEY (" + rel.entity1.name.toLowerCase() + "_id) REFERENCES " + 
                                   rel.entity1.name + "(id);"); 
                System.out.println();
            }
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
