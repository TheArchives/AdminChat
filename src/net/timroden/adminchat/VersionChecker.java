package net.timroden.adminchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionChecker {
	public AdminChat plugin;

	public boolean isLatestVersion = true;
	public String versionMessage = null;
	public String pName;

	public VersionChecker(AdminChat plugin) {
		this.plugin = plugin;
		
		pName = plugin.getDescription().getName();		
	}	

	public String getLatestVersion() {		
		String latestVersion = null;
		String uA = plugin.getDescription().getName() + " " + plugin.getDescription().getVersion();
		final String address = "http://dev.bukkit.org/server-mods/adminchat/files/";
		final URL url;
		URLConnection connection = null;
		BufferedReader bufferedReader = null;
		try {
			url = new URL(address.replace(" ", "%20"));
			connection = url.openConnection();
			connection.setConnectTimeout(8000);
			connection.setReadTimeout(15000);
			connection.setRequestProperty("User-agent", uA);
			bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			Pattern titleFinder = Pattern.compile("<td[^>]*><a[^>]*>(.*?)</a></td>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
			Matcher regexMatcher;
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				str = str.trim();
				regexMatcher = titleFinder.matcher(str);
				if (regexMatcher.find()) {
					latestVersion = regexMatcher.group(1);
					break;
				}
			}
			bufferedReader.close();
			connection.getInputStream().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return latestVersion;
	}

	public void versionCheck() {
		String curVersion = plugin.getDescription().getVersion();
		String latestVersion = getLatestVersion().toLowerCase().replace("adminchat ", "");

		String msg = null;
		if (latestVersion != null) {
			int compare = curVersion.compareTo(latestVersion);
			if (compare < 0) {
				msg = "The version of " + pName + " this server is running is out of date. Latest version: " + latestVersion;
				isLatestVersion = false;
				versionMessage = msg + " You can download the latest version at http://dev.bukkit.org/server-mods/adminchat/files/";
				plugin.log.warning("[" + pName + "] " + msg);
			} else if (compare == 0) { 
				msg = plugin.getDescription().getName() + " is up to date!";
				plugin.log.info("[" + pName + "] " + msg);
			} else {
				msg = "This server is running a Development version of " + pName + ". Expect bugs!";
				isLatestVersion = false;
				versionMessage = msg;
				plugin.log.warning("[" + pName + "] " + msg);
			}
		} else {
			msg = "Error retrieving latest version from BukkitDev.";
			plugin.log.warning("[AdminChat] " + msg);
		}
	}

	public static double getUnixTime() {
		return System.currentTimeMillis() / 1000D;
	}
}