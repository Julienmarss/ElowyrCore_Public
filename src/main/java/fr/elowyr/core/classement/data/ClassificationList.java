package fr.elowyr.core.classement.data;

import java.util.*;

public class ClassificationList<U> {

    private final FieldKey key;
    private final List<ClassificationParent<U>> parents;
    
    public ClassificationList(final FieldKey key) {
        this.key = key;
        this.parents = new LinkedList<>();
    }
    
    public void addLast(final ClassificationParent<U> parent) {
        if (!this.parents.contains(parent)) {
            parent.setIndex(this.key, this.parents.size());
            this.parents.add(parent);
        }
    }
    
    public void remove(final ClassificationParent<U> parent) {
        int index = parent.getIndex(this.key);
        final Iterator<ClassificationParent<U>> iter = this.parents.iterator();
        while (iter.hasNext()) {
            if (iter.next() == parent) {
                iter.remove();
                break;
            }
        }
        while (iter.hasNext()) {
            iter.next().setIndex(this.key, index++);
        }
    }
    
    public void update() {
        this.parents.sort((a, b) -> Double.compare(b.get(this.key), a.get(this.key)));
        int index = 0;
        for (final ClassificationParent<U> parent : this.parents) {
            parent.setIndex(this.key, index++);
        }
    }
    
    public ClassificationParent<U> getAt(final int id) {
        return (id >= 0 && id < this.parents.size()) ? this.parents.get(id) : null;
    }
    
    public void clear() {
        this.parents.clear();
    }
    
    public boolean isEmpty() {
        return this.parents.isEmpty();
    }
}
