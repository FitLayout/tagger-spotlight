/**
 * SpotlightTagger.java
 *
 * Created on 24. 2. 2019, 16:35:11 by burgetr
 */
package cz.vutbr.fit.layout.spotlight.taggers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.fit.layout.api.Parameter;
import org.fit.layout.classify.TagOccurrence;
import org.fit.layout.classify.TextTag;
import org.fit.layout.classify.taggers.BaseTagger;
import org.fit.layout.impl.ParameterString;
import org.fit.layout.model.Area;
import org.fit.layout.model.Tag;

import cz.vutbr.fit.layout.spotlight.DBPTag;
import cz.vutbr.fit.layout.spotlight.DBPTagOccurrence;
import cz.vutbr.fit.layout.spotlight.SpotlightClient;

/**
 * 
 * @author burgetr
 */
public class SpotlightConceptTagger extends BaseTagger
{
    private String conceptType;
    private URL serverUrl;
    
    private Map<Area, List<TagOccurrence>> ocurrences;

    public SpotlightConceptTagger()
    {
        conceptType = "DBPedia:Agent";
    }
    
    public SpotlightConceptTagger(String conceptName)
    {
        this.conceptType = new String(conceptName);
    }
    
    @Override
    public String getId()
    {
        return "FITLayout.Tag.DBP." + conceptType;
    }

    @Override
    public String getName()
    {
        return "dbp:" + conceptType;
    }

    @Override
    public String getDescription()
    {
        return "DBPedia Spotlight Tagger for " + conceptType;
    }
    
    @Override
    public List<Parameter> defineParams()
    {
        List<Parameter> ret = new ArrayList<>(1);
        ret.add(new ParameterString("serverUrl"));
        return ret;
    }
    
    public String getServerUrl()
    {
        return serverUrl.toString();
    }

    public void setServerUrl(String urlString) throws MalformedURLException
    {
        this.serverUrl = new URL(urlString);
    }

    @Override
    public TextTag getTag()
    {
        return new DBPTag(conceptType, this);
    }

    @Override
    public float belongsTo(Area node)
    {
        if (node.isRoot())
        {
            buildOccurrenceIndex(node);
            return 0.0f;
        }
        else if (node.isLeaf())
        {
            List<TagOccurrence> occlist = ocurrences.get(node);
            if (occlist != null)
            {
                //some ocurrences found, return the maximal support
                float max = 0.0f;
                for (TagOccurrence occ : occlist)
                    if (occ.getSupport() > max)
                        max = occ.getSupport();
                return max;
            }
            else
                return 0.0f;
        }
        else
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
        return Collections.emptyList();
    }
    
    @Override
    public List<String> split(String src)
    {
        // TODO splitting is not implemented for this tagger; the whole string is returned
        List<String> ret = new ArrayList<String>(1);
        ret.add(src);
        return ret;
    }

    //========================================================================================
    
    private void buildOccurrenceIndex(Area node)
    {
        //build the complete text and the offset index
        final List<IndexItem> offsetIndex = new ArrayList<>();
        StringBuilder completeText = new StringBuilder();
        recursiveIndexAreas(node, offsetIndex, completeText);
        
        //annotate the text
        SpotlightClient client = new SpotlightClient(serverUrl);
        List<DBPTagOccurrence> occlist = client.annotate(completeText.toString());
        
        //match the occurrences and the areas
        ocurrences = new HashMap<>();
        Iterator<IndexItem> indexit = offsetIndex.iterator();
        IndexItem ii = indexit.next();
        for (DBPTagOccurrence occ : occlist)
        {
            //find the index item for the ocurrence
            while (ii != null && ii.end <= occ.getPosition())
                ii = indexit.next();
            if (ii != null && ii.start <= occ.getPosition() && occ.hasType(conceptType))
            {
                //ii found for the ocurrence
                TagOccurrence newocc = new TagOccurrence(occ.getText(), occ.getPosition() - ii.start, occ.getSupport());
                List<TagOccurrence> newOccList = ocurrences.get(ii.area);
                if (newOccList == null)
                {
                    newOccList = new ArrayList<>();
                    ocurrences.put(ii.area, newOccList);
                }
                newOccList.add(newocc);
            }
        }
    }
    
    private void recursiveIndexAreas(Area root, List<IndexItem> offsetIndex, StringBuilder completeText)
    {
        if (root.isLeaf())
        {
            final String text = root.getText();
            IndexItem ii = new IndexItem();
            ii.area = root;
            ii.start = completeText.length();
            completeText.append(text);
            ii.end = completeText.length();
            offsetIndex.add(ii);
        }
        else
        {
            for (int i = 0; i < root.getChildCount(); i++)
                recursiveIndexAreas(root.getChildAt(i), offsetIndex, completeText);
        }
    }
    
    private static class IndexItem
    {
        public int start;
        public int end;
        public Area area;
    }
    
}
