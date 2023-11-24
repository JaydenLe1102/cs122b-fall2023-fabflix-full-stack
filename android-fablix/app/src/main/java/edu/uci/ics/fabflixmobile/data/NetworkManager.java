package edu.uci.ics.fabflixmobile.data;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.net.CookieHandler;
import java.net.CookieManager;

public class NetworkManager {

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    private static final String host = "13.57.219.206";
    private static final String port = "8443";
    private static final String domain = "2023-fall-cs122b-bobaholic";
    public static final String baseURL = "https://" + host + ":" + port + "/" + domain;

    private static NetworkManager instance = null;
    public RequestQueue queue;

    private NetworkManager() {
        NukeSSLCerts.nuke();  // disable ssl cert self-sign check
    }

    public static NetworkManager sharedManager(Context ctx) {
        if (instance == null) {
            instance = new NetworkManager();
            instance.queue = Volley.newRequestQueue(ctx.getApplicationContext());

            // Create a new cookie store, which handles sessions information with the server.
            // This cookie store will be shared across all the network requests.
            CookieHandler.setDefault(new CookieManager());
        }

        return instance;
    }
}
