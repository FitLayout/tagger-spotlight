/**
 * SpotlightTagger.java
 *
 * Created on 24. 2. 2019, 16:35:11 by burgetr
 */
package cz.vutbr.fit.layout.spotlight.taggers;

import java.util.ArrayList;
import java.util.List;

import org.fit.layout.classify.TagOccurrence;
import org.fit.layout.classify.TextTag;
import org.fit.layout.classify.taggers.BaseTagger;
import org.fit.layout.model.Area;
import org.fit.layout.model.Tag;

import cz.vutbr.fit.layout.spotlight.DBPTag;

/**
 * 
 * @author burgetr
 */
public class SpotlightConceptTagger extends BaseTagger
{
    private String conceptName;
    

    public SpotlightConceptTagger()
    {
        conceptName = "Thing";
    }
    
    public SpotlightConceptTagger(String conceptName)
    {
        this.conceptName = new String(conceptName);
    }
    
    @Override
    public String getId()
    {
        return "FITLayout.Tag.DBP." + conceptName;
    }

    @Override
    public String getName()
    {
        return "dbp:" + conceptName;
    }

    @Override
    public String getDescription()
    {
        return "DBPedia Spotlight Tagger for " + conceptName;
    }
    
    @Override
    public TextTag getTag()
    {
        return new DBPTag(conceptName, this);
    }

    @Override
    public float belongsTo(Area node)
    {
        //TODO
        return 0.0f;
    }
    
    public boolean allowsContinuation(Area node)
    {
        return false;
    }

    public boolean mayCoexistWith(Tag other)
    {
        return true;
    }
    
    public boolean allowsJoining()
    {
        return true;
    }
    
    public List<TagOccurrence> extract(String src)
    {
        //TODO
        return null;
    }
    
    @Override
    public List<String> split(String src)
    {
        // TODO splitting is not implemented for this tagger; the whole string is returned
        List<String> ret = new ArrayList<String>(1);
        ret.add(src);
        return ret;
    }

}
