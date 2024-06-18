package fr.elowyr.core.classement.data;

public interface FieldKey
{
    int getId();
    
    String getName();
    
    String format(final Double p0);
}
