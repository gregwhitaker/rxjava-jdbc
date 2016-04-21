package com.github.davidmoten.rx.jdbc;

/**
 * An enumeration of the query languages supported by this driver.
 */
public enum QueryLanguage {

    /**
     * Structured Query Language
     */
    SQL,

    /**
     * Graph query language supported by Neo4j
     */
    CYPHER
}
