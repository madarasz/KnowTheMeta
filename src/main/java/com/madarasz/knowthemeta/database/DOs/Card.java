package com.madarasz.knowthemeta.database.DOs;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Card {
    @Id @GeneratedValue private Long id;
}