package org.nrg.xapi.rest.dicomweb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidmaffitt on 8/9/17.
 */
public class SqlSelectStatement {
    protected List<String> columnNames;
    protected List<String> tables;
    protected List<String> andClauses;
    protected List<String> orClauses;

    public SqlSelectStatement() {
        this.columnNames = new ArrayList<>();
        this.tables = new ArrayList<>();
        this.andClauses = new ArrayList<>();
        this.orClauses = new ArrayList<>();
    }

    public void addColumnName( String s) {
        columnNames.add(s);
    }

    public void addTable( String s) {
        tables.add(s);
    }

    public String formatClause( String name, String value) {
        StringBuilder clause = new StringBuilder();
        clause.append(name).append("=").append("'").append(value).append("'");
        return clause.toString();
    }

    public void addAndClause( String name, String value) {
        andClauses.add( formatClause( name, value));
    }

    public void addOrClause( String name, String value) {
        orClauses.add( formatClause( name, value));
    }

    public String getStatement() {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        for( String s: columnNames) {
            sb.append( s).append(",");
        }
        sb.deleteCharAt( sb.length()-1);
        sb.append(" from ");
        for( String s: tables) {
            sb.append( s).append(",");
        }
        sb.deleteCharAt( sb.length()-1);

        boolean firstClause = true;
        for( String s: andClauses) {
            if( firstClause) {
                sb.append(" where ").append(s).append(" ");
                firstClause = false;
            }
            else
                sb.append(" and ").append(s).append(" ");
        }
        for( String s: orClauses) {
            if( firstClause) {
                sb.append(" where ").append(s).append(" ");
                firstClause = false;
            }
            else
                sb.append(" or ").append(s).append(" ");
        }
        sb.append(";");
        return sb.toString();
    }
}
