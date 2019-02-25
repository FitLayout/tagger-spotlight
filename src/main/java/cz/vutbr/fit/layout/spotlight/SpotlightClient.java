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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * 
 * @author burgetr
 */
public class SpotlightClient
{
    private URL url;
    
    
    public SpotlightClient(String urlString) throws MalformedURLException
    {
        url = new URL(urlString);
    }

    public void annotate(String text)
    {
        try
        {
            //setup parametres
            String params = "text=" + URLEncoder.encode(text, "UTF-8")
                            + "&confiednce=0"
                            + "&support=0";
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
            System.out.println(root);
            reader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
 
    public static void main(String[] args) 
    {
        try
        {
            final String text = "Brazilian state-run giant oil company Petrobras signed a three-year technology and research cooperation agreement with oil service provider Halliburton.";
            
            SpotlightClient client = new SpotlightClient("http://localhost:2222");
            client.annotate(text);
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    
}
