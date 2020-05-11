package com.madarasz.knowthemeta.database;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

// Superclass for all entities
@NodeEntity
public class Entity {
    @Id @GeneratedValue private Long graphId;

    public Entity() {
    }
}