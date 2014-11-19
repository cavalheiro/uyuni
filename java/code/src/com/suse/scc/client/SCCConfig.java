/**
 * Copyright (c) 2014 SUSE
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */
package com.suse.scc.client;

import org.apache.commons.codec.binary.Base64;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * SCC configuration container class.
 */
public class SCCConfig {

    /** Default SCC URL. */
    protected static final URL DEFAULT_URL;
    // Fairly complex (yet valid) initialization code for the constant
    static {
        URL temp = null;
        try {
            temp = new URL("https://scc.suse.com");
        }
        catch (MalformedURLException e) {
            // never happens
        }
        DEFAULT_URL = temp;
    }

    /** The url. */
    private URL url;

    /** The username. */
    private String username;

    /** The password. */
    private String password;

    /** The client UUID for SCC debugging. */
    private String uuid;

    /** The local resource path for local access to SMT files. */
    private String localResourcePath;

    /** The proxy settings. */
    private SCCProxySettings proxySettings;

    /**
     * Instantiates a new SCC config to read from a local file.
     * @param localResourcePathIn the local resource path
     */
    public SCCConfig(String localResourcePathIn) {
        this(DEFAULT_URL, null, null, null, localResourcePathIn, null);
    }

    /**
     * Full constructor.
     * @param urlIn the url
     * @param usernameIn the username
     * @param passwordIn the password
     * @param uuidIn the UUID
     * @param localResourcePathIn the local resource path
     * @param proxySettingsIn the proxy settings
     */
    public SCCConfig(URL urlIn, String usernameIn, String passwordIn, String uuidIn,
            String localResourcePathIn, SCCProxySettings proxySettingsIn) {
        url = urlIn;
        username = usernameIn;
        password = passwordIn;
        uuid = uuidIn;
        localResourcePath = localResourcePathIn;
        proxySettings = proxySettingsIn;
    }

    /**
     * Gets the url.
     * @return the url
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Gets the username.
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password.
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the UUID.
     * @return the UUID
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * Gets the local resource path.
     * @return the local resource path
     */
    public String getLocalResourcePath() {
        return localResourcePath;
    }

    /**
     * Gets the proxy settings.
     * @return the proxy settings
     */
    public SCCProxySettings getProxySettings() {
        return proxySettings;
    }

    /**
     * Returns the encoded credentials or null.
     * @return credentials
     */
    public String getEncodedCredentials() {
        if (username != null && password != null) {
            byte[] encoded = Base64.encodeBase64((username + ":" + password).getBytes());
            return new String(encoded);
        }
        return null;
    }
}
