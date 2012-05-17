/**
 * NightimeDrunk is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  NightimeDrunk is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with NightimeDrunk.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.nijokkin.nightimedrunk;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.nijokkin.nightimedrunk.Metrics.Graph;

public class NightimeDrunk extends JavaPlugin
{
	
	public static String VERSION;
	
	public void onEnable()
	{
		PluginDescriptionFile pdf = getDescription();
		
		VERSION = pdf.getVersion();
		
		setupConfig();
		//checkNightimeDrunk();
		createMetrics();
	}
	
	public void setupConfig()
	{
		getConfig().addDefault("use-auto-updater", true);
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	public void createMetrics()
	{
		try
		{
			Metrics metrics = new Metrics(this);
			
			Graph g = metrics.createGraph("Using Nijokkin-Updater");
			
			g.addPlotter(new Metrics.Plotter("Uses Auto-Updater")
			{
				
				public int getValue()
				{
					if(getConfig().getBoolean("use-auto-updater"))
					{
						return 1;
					} else {
						return 0;
					}
				}				
				
			});
			
			metrics.start();
		} catch (IOException e)
		{
			
		}
	}
	
	public void checkNightimeDrunk()
	{
		String latestVersion = null;
		
		try {
			URL url = new URL("http://www.spetznazam.com/NightimeDrunk/version.txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			
			latestVersion = in.readLine();
			
			in.close();
			} catch (Exception e) {
				System.out.println("[NightimeDrunk] There was a problem reading from the online file.");
			}
		
		if(latestVersion == VERSION && getConfig().getBoolean("use-auto-updater"))
		{
			return;
		} else {
			System.out.println("VERSION: " + VERSION + ", LATESTVERSION: " + latestVersion);
			System.out.println("[NightimeDrunk] Updating NightimeDrunk to version " + latestVersion);
			updateNightimeDrunk();
		}
	}

	public void updateNightimeDrunk()
	{
		try
		{
			BufferedInputStream in = null;
			FileOutputStream fout = null;
			try
			{
				in = new BufferedInputStream(new URL("http://www.spetznazam.com/NightimeDrunk/NightimeDrunk.jar").openStream());
				fout = new FileOutputStream("plugins/NightimeDrunk.jar");

				byte[] data = new byte[1024];
				int count;
				while ((count = in.read(data, 0, 1024)) != -1)
				{
					fout.write(data, 0, count);
				}
			}
			finally
			{
				if (in != null)
					in.close();
				if (fout != null)
					fout.close();
			}
		} catch(Exception e)
		{
			Logger log = Logger.getLogger("Minecraft");
			log.info("[NightimeDrunk] There was a problem updating NightimeDrunk.");
		}
	}
}