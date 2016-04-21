package com.github.davidmoten.rx.jdbc;

import java.sql.ResultSet;

import rx.Scheduler;
import rx.functions.Func1;

/**
 * The threading and database connection context for multiple jdbc queries.
 */
final class QueryContext {

    private final Database db;

    /**
     * Constructor.
     */
    QueryContext(Database db) {
        this.db = db;
    }

    /**
     * Returns the scheduler service to use to run queries with this context.
     * 
     * @return
     */
    Scheduler scheduler() {
        return db.currentScheduler();
    }

    /**
     * Returns the connection provider for queries with this context.
     * 
     * @return
     */
    ConnectionProvider connectionProvider() {
        return db.connectionProvider();
    }

    /**
     * Returns the database's supported query language.
     *
     * @return
     */
    QueryLanguage queryLanguage() {
        return db.getLanguage();
    }

    void beginTransactionObserve() {
        db.beginTransactionObserve();

    }

    void beginTransactionSubscribe() {
        db.beginTransactionSubscribe();
    }

    void endTransactionSubscribe() {
        db.endTransactionSubscribe();
    }

    void endTransactionObserve() {
        db.endTransactionObserve();
    }

    Func1<ResultSet, ? extends ResultSet> resultSetTransform() {
        return db.getResultSetTransform();
    }
}