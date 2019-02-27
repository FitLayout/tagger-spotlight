/**
 * SpotlightClient.java
 *
 * Created on 25. 2. 2019, 15:24:20 by burgetr
 */
package cz.vutbr.fit.layout.spotlight;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author burgetr
 */
public class SpotlightClient
{
    private URL url;
    
    
    public SpotlightClient(URL url)
    {
        this.url = url;
    }

    public SpotlightClient(String urlString) throws MalformedURLException
    {
        this.url = new URL(urlString);
    }

    public List<DBPTagOccurrence> annotate(String text)
    {
        try
        {
            //setup parametres
            String params = "text=" + URLEncoder.encode(text, "UTF-8")
                            + "&confidence=0"
                            + "&support=0";
            System.out.println("PARAMS " + params);
            byte[] postData = params.getBytes(StandardCharsets.UTF_8);
            URL reqUrl = new URL(url, "/rest/annotate");
            
            //open the connection
            HttpURLConnection con = (HttpURLConnection) reqUrl.openConnection();
            con.setDoOutput(true);
            con.setInstanceFollowRedirects(false);
            con.setUseCaches(false);        
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
            con.setRequestProperty("Charset", "utf-8");
            con.setRequestProperty("Content-Length", Integer.toString(postData.length));
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write( postData );
            }
            
            //read and decode the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            JsonParser parser = new JsonParser();
            JsonElement root = parser.parse(reader);
            if (root != null && root.isJsonObject())
            {
                List<DBPTagOccurrence> occlist = decodeJsonOccurences(root.getAsJsonObject());
                return occlist;
            }
            else
                return Collections.emptyList();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
        
    }
    
    private List<DBPTagOccurrence> decodeJsonOccurences(JsonObject root)
    {
        final JsonElement resources = root.get("Resources");
        if (resources != null && resources.isJsonArray())
        {
            List<DBPTagOccurrence> ret = new ArrayList<>(resources.getAsJsonArray().size());
            for (JsonElement itemElement : resources.getAsJsonArray())
            {
                JsonObject item = itemElement.getAsJsonObject();
                String text = item.get("@surfaceForm").getAsString();
                int position = item.get("@offset").getAsInt();
                float support = item.get("@similarityScore").getAsFloat();
                String uri = item.get("@URI").getAsString();
                String typestr = item.get("@types").getAsString();
                String[] types = typestr.split(",");
                DBPTagOccurrence occ = new DBPTagOccurrence(text, position, support, uri, types);
                ret.add(occ);
            }
            return ret;
        }
        else
            return Collections.emptyList();
    }
    
 
    public static void main(String[] args) 
    {
        try
        {
            final String text = "Brazilian state-run giant oil company Petrobras signed a three-year technology and research cooperation agreement with oil service provider Halliburton.";
            
            SpotlightClient client = new SpotlightClient("http://localhost:2222");
            List<DBPTagOccurrence> occurrences = client.annotate(text);
            System.out.println(occurrences);
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    
}
