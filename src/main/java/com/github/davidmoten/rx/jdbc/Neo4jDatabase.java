package com.github.davidmoten.rx.jdbc;

import rx.Scheduler;
import rx.functions.Func0;
import rx.functions.Func1;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Main entry point for manipulations of a Neo4j database using rx-java-jdbc style
 * queries.
 */
public class Neo4jDatabase extends Database {

    public Neo4jDatabase(ConnectionProvider cp, Func0<Scheduler> nonTransactionalSchedulerFactory) {
        super(cp, nonTransactionalSchedulerFactory);
    }

    public Neo4jDatabase(ConnectionProvider cp, Func0<Scheduler> nonTransactionalSchedulerFactory, Func1<ResultSet, ? extends ResultSet> resultSetTransform) {
        super(cp, nonTransactionalSchedulerFactory, resultSetTransform);
    }

    public Neo4jDatabase(ConnectionProvider cp) {
        super(cp);
    }

    public Neo4jDatabase(String url, String username, String password) {
        super(url, username, password);
    }

    public Neo4jDatabase(Connection con) {
        super(con);
    }

    @Override
    public QueryContext queryContext() {
        return super.queryContext();
    }

    @Override
    public QueryLanguage getLanguage() {
        return QueryLanguage.CYPHER;
    }
}
