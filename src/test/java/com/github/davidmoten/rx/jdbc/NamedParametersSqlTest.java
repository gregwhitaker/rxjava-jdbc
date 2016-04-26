package com.github.davidmoten.rx.jdbc;

import com.github.davidmoten.junit.Asserts;
import com.github.davidmoten.rx.jdbc.NamedParameters.JdbcQuery;
import org.h2.Driver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.DriverManager;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(QueryContext.class)
public class NamedParametersSqlTest {

    @Test
    public void testSelect() {
        QueryContext ctx = PowerMockito.mock(QueryContext.class);
        when(ctx.queryLanguage()).thenReturn(QueryLanguage.SQL);

        JdbcQuery r = NamedParameters.parse(
                "select a, b from tbl where a.name=:name and b.name=:name and c.description = :description", ctx);
        assertEquals("select a, b from tbl where a.name=? and b.name=? and c.description = ?", r.sql());
        assertEquals(Arrays.asList("name", "name", "description"), r.names());
    }

    @Test
    public void testSelectWithOneNamedParameterAndColonNameInQuotes() {
        QueryContext ctx = PowerMockito.mock(QueryContext.class);
        when(ctx.queryLanguage()).thenReturn(QueryLanguage.SQL);

        JdbcQuery r = NamedParameters
                .parse("select a, b from tbl where a.name=:name and b.name=':name'", ctx);
        assertEquals("select a, b from tbl where a.name=? and b.name=':name'", r.sql());
        assertEquals(Arrays.asList("name"), r.names());
    }

    @Test
    public void testSelectWithNoNamedParameters() {
        assertParseUnchanged("select a, b from tbl");
    }

    @Test
    public void testNamedParametersAllMissingParametersShouldDoNothing() throws Exception {
        DriverManager.registerDriver(new Driver());
        DatabaseCreator.db().select("select name from person where name = :name").count()
                .subscribe();
    }

    @Test
    public void testDoubleColonNotModified() {
        QueryContext ctx = PowerMockito.mock(QueryContext.class);
        when(ctx.queryLanguage()).thenReturn(QueryLanguage.SQL);

        JdbcQuery r = NamedParameters.parse("select a::varchar, b from tbl where a.name=:name", ctx);
        assertEquals("select a::varchar, b from tbl where a.name=?", r.sql());
        assertEquals(Arrays.asList("name"), r.names());
    }

    @Test
    public void testTripleColonNotModified() {
        QueryContext ctx = PowerMockito.mock(QueryContext.class);
        when(ctx.queryLanguage()).thenReturn(QueryLanguage.SQL);

        JdbcQuery r = NamedParameters.parse("select a:::varchar, b from tbl where a.name=:name", ctx);
        assertEquals("select a:::varchar, b from tbl where a.name=?", r.sql());
        assertEquals(Arrays.asList("name"), r.names());
    }

    @Test
    public void testTerminatingColonNotModified() {
        assertParseUnchanged("select a:");
    }

    @Test
    public void testParseColonFollowedByNonIdentifierCharacter() {
        assertParseUnchanged("select a:||c from blah");
    }

    @Test
    public void testIsFollowedOrPrefixedByColon() {
        assertTrue(NamedParameters.isFollowedOrPrefixedByColon("a:bc", 0));
        assertFalse(NamedParameters.isFollowedOrPrefixedByColon("a:bc", 1));
        assertTrue(NamedParameters.isFollowedOrPrefixedByColon("a:bc", 2));
        assertFalse(NamedParameters.isFollowedOrPrefixedByColon(":bc", 0));
        assertTrue(NamedParameters.isFollowedOrPrefixedByColon(":bc", 1));
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void testIsFollowedOrPrefixedByColonAtEndThrowsException() {
        NamedParameters.isFollowedOrPrefixedByColon("a:b", 2);
    }

    @Test
    public void testDoubleQuote() {
        assertParseUnchanged("select \":b\" from blah");
    }

    @Test
    public void testIsUtilityClass() {
        Asserts.assertIsUtilityClass(NamedParameters.class);
    }

    private static void assertParseUnchanged(String sql) {
        QueryContext ctx = PowerMockito.mock(QueryContext.class);
        when(ctx.queryLanguage()).thenReturn(QueryLanguage.SQL);

        JdbcQuery r = NamedParameters.parse(sql, ctx);
        assertEquals(sql, r.sql());
        assertTrue(r.names().isEmpty());
    }
}
