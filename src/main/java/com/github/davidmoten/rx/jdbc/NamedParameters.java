package com.github.davidmoten.rx.jdbc;

import java.util.ArrayList;
import java.util.List;

public final class NamedParameters {

    private NamedParameters() {
        // disallow instantiation
    }

    public static JdbcQuery parse(String query, QueryContext ctx) {
        switch (ctx.queryLanguage()) {
            case SQL:
                return parseSql(query);
            case CYPHER:
                return parseCypher(query);
            default:
                throw new RuntimeException("Unsupported query language!");
        }
    }

    static JdbcQuery parseSql(String namedSql) {
        // was originally using regular expressions, but they didn't work well
        // for ignoring parameter-like strings inside quotes.
        List<String> names = new ArrayList<String>();
        int length = namedSql.length();
        StringBuilder parsedQuery = new StringBuilder(length);
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        for (int i = 0; i < length; i++) {
            char c = namedSql.charAt(i);
            if (inSingleQuote) {
                if (c == '\'') {
                    inSingleQuote = false;
                }
            } else if (inDoubleQuote) {
                if (c == '"') {
                    inDoubleQuote = false;
                }
            } else {
                if (c == '\'') {
                    inSingleQuote = true;
                } else if (c == '"') {
                    inDoubleQuote = true;
                } else if (c == ':' && i + 1 < length && !isFollowedOrPrefixedByColon(namedSql, i)
                        && Character.isJavaIdentifierStart(namedSql.charAt(i + 1))) {
                    int j = i + 2;
                    while (j < length && Character.isJavaIdentifierPart(namedSql.charAt(j))) {
                        j++;
                    }
                    String name = namedSql.substring(i + 1, j);
                    c = '?'; // replace the parameter with a question mark
                    i += name.length(); // skip past the end if the parameter
                    names.add(name);
                }
            }
            parsedQuery.append(c);
        }
        return new JdbcQuery(parsedQuery.toString(), names);
    }

    static JdbcQuery parseCypher(String cypher) {
        List<String> names = new ArrayList<String>();

        int length = cypher.length();
        StringBuilder parsedQuery = new StringBuilder(length);

        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        for (int i = 0; i < length; i++) {
            char c = cypher.charAt(i);
            if (inSingleQuote) {
                // Checking for end of the single quote
                if (c == '\'') {
                    inSingleQuote = false;
                }
            } else if (inDoubleQuote) {
                // Checking for end of the double quote
                if (c == '"') {
                    inDoubleQuote = false;
                }
            } else {
                if (c == '\'') {
                    // Skip over anything in single quotes
                    inSingleQuote = true;
                } else if (c == '"') {
                    // Skip over anything in double quotes
                    inDoubleQuote = true;
                } else if (c == ':' && i + 1 < length && !isFollowedOrPrefixedByColon(cypher, i)
                        && cypher.charAt(i + 1) == '{') {
                    parsedQuery.append(":");
                    int j = i + 2;
                    while (j < length && Character.isJavaIdentifierPart(cypher.charAt(j)) && cypher.charAt(j) != '}') {
                        j++;
                    }
                    String name = cypher.substring(i + 2, j);
                    c = '?'; // replace the parameter with a question mark
                    i += name.length() + 2; // skip past the end if the parameter
                    names.add(name);
                }
            }
            parsedQuery.append(c);
        }
        return new JdbcQuery(parsedQuery.toString(), names);
    }



    // Visible for testing
    static boolean isFollowedOrPrefixedByColon(String sql, int i) {
        return ':' == sql.charAt(i + 1) || (i > 0 && ':' == sql.charAt(i - 1));
    }

    public static class JdbcQuery {
        private final String sql;
        private final List<String> names;

        public JdbcQuery(String sql, List<String> names) {
            this.sql = sql;
            this.names = names;
        }

        public String sql() {
            return sql;
        }

        public List<String> names() {
            return names;
        }

    }

}