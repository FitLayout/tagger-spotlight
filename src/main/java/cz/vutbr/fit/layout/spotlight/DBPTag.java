/**
 * DBPTag.java
 *
 * Created on 24. 2. 2019, 16:43:45 by burgetr
 */
package cz.vutbr.fit.layout.spotlight;

import org.fit.layout.classify.Tagger;
import org.fit.layout.classify.TextTag;

/**
 * A tag assigned by the Spotlight taggers.
 * 
 * @author burgetr
 */
public class DBPTag extends TextTag
{
    
    public DBPTag(String value, Tagger source)
    {
        super(value, source);
        setType("FitLayout.DBP");
    }

}
