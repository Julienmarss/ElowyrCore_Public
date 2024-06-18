package fr.elowyr.core.classement.data;

import java.util.*;

public class ClassificationParent<U> {

    private Long id;
    private String display;
    private final U uuid;
    private final double[] fields;
    private final int[] indexes;
    
    public ClassificationParent(final Long id, final U uuid, final String display, final double[] fields) {
        this.id = id;
        this.uuid = uuid;
        this.fields = fields;
        this.display = display;
        Arrays.fill(this.indexes = new int[fields.length], 0);
    }
    
    public void setId(final Long id) {
        this.id = id;
    }
    
    public Long getId() {
        return this.id;
    }
    
    public U getUuid() {
        return this.uuid;
    }
    
    public void setDisplay(final String display) {
        this.display = display;
    }
    
    public String getDisplay() {
        return this.display;
    }
    
    public void addValue(final FieldKey key, final double value) {
        final double[] fields = this.fields;
        final int id = key.getId();
        fields[id] += value;
    }
    
    public double get(final FieldKey key) {
        return this.fields[key.getId()];
    }
    
    public void setIndex(final FieldKey key, final int index) {
        this.indexes[key.getId()] = index;
    }
    
    public int getIndex(final FieldKey key) {
        return this.indexes[key.getId()];
    }
}
