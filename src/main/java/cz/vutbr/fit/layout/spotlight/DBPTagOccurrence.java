/**
 * DBPTagOccurrence.java
 *
 * Created on 26. 2. 2019, 10:30:06 by burgetr
 */
package cz.vutbr.fit.layout.spotlight;

import java.util.Arrays;
import java.util.List;

import org.fit.layout.classify.TagOccurrence;

/**
 * A tag occurrence with additional information about the occurrence mapping to DBPedia.
 * 
 * @author burgetr
 */
public class DBPTagOccurrence extends TagOccurrence
{
    private String uri;
    private List<String> types;

    public DBPTagOccurrence(String text, int position, float support, String uri, String[] types)
    {
        super(text, position, support);
        this.uri = new String(uri);
        this.types = Arrays.asList(types);
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public List<String> getTypes()
    {
        return types;
    }

    public void setTypes(List<String> types)
    {
        this.types = types;
    }

    @Override
    public String toString()
    {
        return super.toString() + types.toString();
    }

}
