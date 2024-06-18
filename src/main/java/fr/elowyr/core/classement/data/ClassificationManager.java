package fr.elowyr.core.classement.data;

import fr.elowyr.core.utils.DbUtils;

import java.util.concurrent.*;

import java.util.stream.*;
import java.util.*;

public class ClassificationManager<U> {

    private final String tableName;
    private final Map<U, ClassificationParent<U>> parents;
    private final ClassificationList<U>[] lists;
    private final FieldKey[] keys;
    private final Set<ClassificationParent<U>> changes;
    
    public ClassificationManager(final String table, final FieldKey[] keys) {
        this.parents = new HashMap<>();
        this.tableName = table;
        this.keys = keys;
        this.lists = (ClassificationList<U>[])new ClassificationList[keys.length];
        this.changes = Collections.newSetFromMap(new ConcurrentHashMap<>());
        for (final FieldKey key : keys) {
            this.lists[key.getId()] = new ClassificationList<>(key);
        }
    }
    
    public void addLast(final ClassificationParent<U> parent) {
        this.parents.put(parent.getUuid(), parent);
        for (final FieldKey key : this.keys) {
            if (parent.get(key) > 0.0) {
                this.lists[key.getId()].addLast(parent);
            }
        }
    }
    
    public void remove(final ClassificationParent<U> parent) {
        this.parents.remove(parent.getUuid());
        for (final ClassificationList<U> list : this.lists) {
            list.remove(parent);
        }
    }
    
    public void addValue(final U uuid, final String display, final FieldKey key, final double value) {
        this.addValueNoUpdate(uuid, display, key, value);
        this.update(key);
    }
    
    public void addValueNoUpdate(final U uuid, final String display, final FieldKey key, final double value) {
        ClassificationParent<U> parent = this.getByUuid(uuid);
        if (parent == null) {
            parent = new ClassificationParent<>(null, uuid, display, this.newDoubles());
            parent.addValue(key, value);
            this.parents.put(uuid, parent);
            DbUtils.insertClassification(this, parent);
        }
        else {
            parent.addValue(key, value);
            this.changes.add(parent);
        }
        this.lists[key.getId()].addLast(parent);
    }
    
    public void update(final FieldKey key) {
        this.lists[key.getId()].update();
    }
    
    public ClassificationParent<U> getByUuid(final U uuid) {
        return this.parents.get(uuid);
    }
    
    public FieldKey[] getKeys() {
        return this.keys;
    }
    
    public Set<ClassificationParent<U>> getChanges() {
        synchronized (this.changes) {
            final HashSet<ClassificationParent<U>> changes = new HashSet<>(this.changes);
            this.changes.clear();
            return changes;
        }
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public ClassificationParent<U> getAt(final FieldKey key, final int id) {
        return this.lists[key.getId()].getAt(id);
    }
    
    public void reset(final FieldKey key) {
        final ClassificationList<U> listOfKey = this.lists[key.getId()];
        if (listOfKey != null) {
            listOfKey.clear();
        }
        if (Stream.of(this.lists).allMatch(ClassificationList::isEmpty)) {
            this.parents.clear();
            DbUtils.deleteClassifications(this);
        }
    }
    
    private double[] newDoubles() {
        final double[] fields = new double[this.keys.length];
        Arrays.fill(fields, 0.0);
        return fields;
    }
}
