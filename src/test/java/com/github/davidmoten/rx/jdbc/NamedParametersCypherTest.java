package com.github.davidmoten.rx.jdbc;

import com.github.davidmoten.rx.jdbc.NamedParameters.JdbcQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(QueryContext.class)
public class NamedParametersCypherTest {

    @Test
    public void testMatchWithNoNamedParameters() {
        QueryContext ctx = PowerMockito.mock(QueryContext.class);
        when(ctx.queryLanguage()).thenReturn(QueryLanguage.CYPHER);

        String query = "MATCH (n) RETURN n";

        JdbcQuery r = NamedParameters.parse(query, ctx);
        assertEquals("MATCH (n) RETURN n", r.sql());
        assertTrue(r.names().isEmpty());
    }

    @Test
    public void testMatchWithNamedParameter() {
        QueryContext ctx = PowerMockito.mock(QueryContext.class);
        when(ctx.queryLanguage()).thenReturn(QueryLanguage.CYPHER);

        String query = "MATCH (p {id:{id}}) RETURN p";

        JdbcQuery r = NamedParameters.parse(query, ctx);
        assertEquals("MATCH (p {id:?}) RETURN p", r.sql());
        assertEquals(Arrays.asList("id"), r.names());
    }

    @Test
    public void testMatchWithMultipleNamedParameters() {
        QueryContext ctx = PowerMockito.mock(QueryContext.class);
        when(ctx.queryLanguage()).thenReturn(QueryLanguage.CYPHER);

        String query = "MATCH (p {id:{id}, value:{value}}) RETURN p";

        JdbcQuery r = NamedParameters.parse(query, ctx);
        assertEquals("MATCH (p {id:?, value:?}) RETURN p", r.sql());
        assertEquals(Arrays.asList("id", "value"), r.names());
    }

    @Test
    public void testMatchWithLabel() {
        QueryContext ctx = PowerMockito.mock(QueryContext.class);
        when(ctx.queryLanguage()).thenReturn(QueryLanguage.CYPHER);

        String query = "MATCH (p:Label) RETURN p";

        JdbcQuery r = NamedParameters.parse(query, ctx);
        assertEquals("MATCH (p:Label) RETURN p", r.sql());
        assertTrue(r.names().isEmpty());
    }

    @Test
    public void testMatchWithLabelAndNamedParameter() {
        QueryContext ctx = PowerMockito.mock(QueryContext.class);
        when(ctx.queryLanguage()).thenReturn(QueryLanguage.CYPHER);

        String query = "MATCH (p:Label {id:{id}}) RETURN p";

        JdbcQuery r = NamedParameters.parse(query, ctx);
        assertEquals("MATCH (p:Label {id:?}) RETURN p", r.sql());
        assertEquals(Arrays.asList("id"), r.names());
    }

    @Test
    public void testMatchWithLabelAndMultipleNamedParameters() {
        QueryContext ctx = PowerMockito.mock(QueryContext.class);
        when(ctx.queryLanguage()).thenReturn(QueryLanguage.CYPHER);

        String query = "MATCH (p:Label {id:{id}, value:{value}}) RETURN p";

        JdbcQuery r = NamedParameters.parse(query, ctx);
        assertEquals("MATCH (p:Label {id:?, value:?}) RETURN p", r.sql());
        assertEquals(Arrays.asList("id", "value"), r.names());
    }

    @Test
    public void testMatchWithTraversalAndNamedParameter() {
        QueryContext ctx = PowerMockito.mock(QueryContext.class);
        when(ctx.queryLanguage()).thenReturn(QueryLanguage.CYPHER);

        String query = "MATCH (p {id:{id}})-[:EDGE]->(v) RETURN v";

        JdbcQuery r = NamedParameters.parse(query, ctx);
        assertEquals("MATCH (p {id:?})-[:EDGE]->(v) RETURN v", r.sql());
        assertEquals(Arrays.asList("id"), r.names());
    }
}
