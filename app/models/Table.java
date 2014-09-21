package models;

import java.util.List;

public class Table {
    public String v;
    public String j;
    public Double n;

    public Table() {}

    public Table(String v, String j, Double n) {
        this.v = v;
        this.j = j;
        this.n = n;
    }

    public void printTable(List<Table> data) {
        for (Table item: data) System.out.println(item.v + " " + item.j + " " + item.n.toString());
    }
}