/**
 * SpotlightPlugin.java
 *
 * Created on 27. 2. 2019, 20:32:40 by burgetr
 */
package cz.vutbr.fit.layout.spotlight.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JToolBar;

import org.fit.layout.gui.Browser;
import org.fit.layout.gui.BrowserPlugin;
import org.fit.layout.model.Area;
import org.fit.layout.model.Tag;

/**
 * 
 * @author burgetr
 */
public class SpotlightPlugin implements BrowserPlugin
{
    private Browser browser;
    
    private JToolBar toolbar;
    private JButton tagsButton;

    @Override
    public boolean init(Browser browser)
    {
        this.browser = browser;
        this.browser.addToolBar(getToolbar());
        return true;
    }
    
    //=================================================================
    
    private JToolBar getToolbar()
    {
        if (toolbar == null)
        {
            toolbar = new JToolBar("Spotlight");
            toolbar.add(getTagsButton());
        }
        return toolbar;
    }

    private JButton getTagsButton()
    {
        if (tagsButton == null)
        {
            tagsButton = new JButton("DBP Tags");
            tagsButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent arg0)
                {
                    Area node = browser.getSelectedArea();
                    if (node != null)
                        colorizeTags(node, "FitLayout.DBP");
                }
            });
        }
        return tagsButton;
    }
    
    //=================================================================
    
    private void colorizeTags(Area root, String type)
    {
        recursiveColorizeTags(root, type);
        browser.updateDisplay();
    }
    
    private void recursiveColorizeTags(Area root, String type)
    {
        //find tags of the given type
        Set<Tag> tags = new HashSet<Tag>();
        for (Tag tag : root.getSupportedTags(0.3f)) //TODO make configurable?
        {
            if (tag.getType().equals(type))
                tags.add(tag);
        }
        //display the tags
        browser.getOutputDisplay().colorizeByTags(root, tags);
        for (Area child : root.getChildren())
            recursiveColorizeTags(child, type);
    }

}
