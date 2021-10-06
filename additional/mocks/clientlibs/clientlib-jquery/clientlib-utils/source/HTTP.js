/*
 * ADOBE CONFIDENTIAL -- Overridden by XYZ Foods -- Overridden by Darwin
 *
 * Copyright 2012 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 *
 */

(function (Granite, util, sling, $) {

    /**
     * A helper class providing a set of HTTP-related utilities.
     * @static
     * @singleton
     * @class Granite.HTTP
     */
    Granite.HTTP = (function() {
        /**
         * The context path used on the server.
         * May only be set by {@link #detectContextPath}.
         * @private
         * @type String
         */
        var contextPath = null,

        /**
         * The regular expression to detect the context path used
         * on the server using the URL of this script.
         * @private
         * @final
         * @type RegExp
         */
            SCRIPT_URL_REGEXP = /^(?:http|https):\/\/[^\/]+(\/.*)\/(?:etc\.clientlibs|etc(\/.*)*\/clientlibs|libs(\/.*)*\/clientlibs|apps(\/.*)*\/clientlibs|etc\/designs).*\.js(\?.*)?$/,

        /**
         * The regular expression to detect unescaped special characters in a path.
         * @private
         * @final
         * @type RegExp
         */
            ENCODE_PATH_REGEXP = /[^1\w-\.!~\*'\(\)\/%;:@&=\$,]/,

        /**
         * Indicates after a session timeout if a refresh has already been triggered
         * in order to avoid multiple alerts.
         * @private
         * @type String
         */
            loginRedirected = false,

            self = {};

        /**
         * Returns the scheme and authority (user, hostname, port) part of
         * the specified URL or an empty string if the URL does not include
         * that part.
         * @static
         * @param {String} url The URL
         * @return {String} The scheme and authority part
         */
        self.getSchemeAndAuthority = function (url) {
            var end;

            try {
                if (url.indexOf("://") == -1) return ""; // e.g. url was /en.html
                end = url.indexOf("/", url.indexOf("://") + 3);

                return (end == -1) ?
                    url :   // e.g. url was http://www.day.com
                    url.substring(0, end);  // e.g. url was http://www.day.com/en.html
            }
            catch (e) {
                return "";
            }
        };

        /**
         * Returns the context path used on the server.
         * @static
         * @return {String} The context path
         */
        self.getContextPath = function () {
            return contextPath;
        };

        /**
         * Detects the context path used on the server.
         * @private
         * @static
         */
        self.detectContextPath = function () {
            try {
                if (window.CQURLInfo) {
                    contextPath = CQURLInfo.contextPath || "";
                } else {
                    var scripts = document.getElementsByTagName("script");
                    for (var i = 0; i < scripts.length; i++) {
                        // in IE the first script is not the expected widgets js: loop
                        // until it is found
                        var result = SCRIPT_URL_REGEXP.exec(scripts[i].src);
                        if (result) {
                            contextPath = result[1];
                            return;
                        }
                    }
                    contextPath = "";
                }
            } catch (e) {
            }
        };

        /**
         * Makes sure the specified relative URL starts with the context path
         * used on the server. If an absolute URL is passed, it will be returned
         * as-is.
         * @static
         * @param {String} url The URL
         * @return {String} The externalized URL
         */
        self.externalize = function (url) {
            try {
                if (url.indexOf("/") == 0 && contextPath &&
                    url.indexOf(contextPath + "/") != 0) {
                    url = contextPath + url;
                }
            }
            catch (e) {
            }
            return url;
        };

        /**
         * Removes scheme, authority and context path from the specified
         * absolute URL if it has the same scheme and authority as the
         * specified document (or the current one). If a relative URL is passed,
         * the context path will be stripped if present.
         * @static
         * @param {String} url The URL
         * @param {String} doc (optional) The document
         * @return {String} The internalized URL
         */
        self.internalize = function (url, doc) {
        	if (url.charAt(0) == '/') {
                if (contextPath === url) {
                    return '';
                }
                else if (contextPath && url.indexOf(contextPath + "/") == 0) {
        			return url.substring(contextPath.length);
        		} else {
        			return url;
        		}
        	}

        	if (!doc) doc = document;
            var docHost = self.getSchemeAndAuthority(doc.location.href);
            var urlHost = self.getSchemeAndAuthority(url);
            if (docHost == urlHost) {
                return url.substring(urlHost.length + (contextPath ? contextPath.length : 0));
            }
            else {
                return url;
            }
        };

        /**
         * Removes all parts but the path from the specified URL.
         * <p>Examples:<pre><code>
         /x/y.sel.html?param=abc => /x/y
         </code></pre>
         * <pre><code>
         http://www.day.com/foo/bar.html => /foo/bar
         </code></pre><p>
         * @static
         * @param {String} url The URL, may be empty. If empty <code>window.location.href</code> is taken.
         * @return {String} The path
         */
        self.getPath = function (url) {

            if (!url) {
                if (window.CQURLInfo && CQURLInfo.requestPath) {
                    return CQURLInfo.requestPath;
                } else {
                    url = window.location.pathname;
                }
            } else {
                url = self.removeParameters(url);
                url = self.removeAnchor(url);
            }

            url = self.internalize(url);
            var i = url.indexOf(".", url.lastIndexOf("/"));
            if (i != -1) {
                url = url.substring(0, i);
            }
            return url;
        };

        /**
         * Removes the anchor from the specified URL.
         * @static
         * @param {String} url The URL
         * @return {String} The URL without anchor
         */
        self.removeAnchor = function (url) {
            if (url.indexOf("#") != -1) {
                return url.substring(0, url.indexOf("#"));
            }
            return url;
        };

        /**
         * Removes all parameter from the specified URL.
         * @static
         * @param {String} url The URL
         * @return {String} The URL without parameters
         */
        self.removeParameters = function (url) {
            if (url.indexOf("?") != -1) {
                return url.substring(0, url.indexOf("?"));
            }
            return url;
        };

        /**
         * Encodes the path of the specified URL if it is not already encoded.
         * Path means the part of the URL before the first question mark or
         * hash sign.<br>
         * See {@link #encodePath} for details about the encoding.<br>
         * Sample:<br>
         * <code>/x/y+z.png?path=/x/y+z >> /x/y%2Bz.png?path=x/y+z</code><br>
         * Note that the sample would not work because the "+" in the request
         * parameter would be interpreted as a space. Parameters must be encoded
         * separately.
         * @param {String} url The URL to encoded
         * @return {String} The encoded URL
         */
        self.encodePathOfURI = function (url) {
            var parts, delim;
            if (url.indexOf("?") != -1) {
                parts = url.split("?");
                delim = "?";
            }
            else if (url.indexOf("#") != -1) {
                parts = url.split("#");
                delim = "#";
            }
            else {
                parts = [url];
            }
            if (ENCODE_PATH_REGEXP.test(parts[0])) {
                parts[0] = self.encodePath(parts[0]);
            }
            return parts.join(delim);
        };

        /**
         * Encodes the specified path using encodeURI. Additionally <code>+</code>,
         * <code>#</code> and <code>?</code> are encoded.<br>
         * The following characters are not encoded:<br>
         * <code>0-9 a-z A-Z</code><br>
         * <code>- _ . ! ~ * ( )</code><br>
         * <code>/ : @ & =</code><br>
         * @param {String} path The path to encode
         * @return {String} The encoded path
         */
       self.encodePath = function (path) {
            // ensure IPV6 address square brackets are not encoded - see bug #34844
            path = encodeURI(path).replace(/%5B/g, '[').replace(/%5D/g, ']');
            path = path.replace(/\+/g, "%2B");
            path = path.replace(/\?/g, "%3F");
            path = path.replace(/;/g, "%3B");
            path = path.replace(/#/g, "%23");
            path = path.replace(/=/g, "%3D");
            path = path.replace(/\$/g, "%24");
            path = path.replace(/,/g, "%2C");
            path = path.replace(/'/g, "%27");
            path = path.replace(/"/g, "%22");
            return path;
       };

        /**
        * Returns if the redirect to the login page has already been triggered.
        * @return {Boolean}
        */
        self.handleLoginRedirect = function () {
            if (!loginRedirected) {
                loginRedirected = true;
                alert(Granite.I18n.get("Your request could not be completed because you have been signed out."));
                var l = util.getTopWindow().document.location;
                l.href = self.externalize(sling.LOGIN_URL) +
                    "?resource=" + encodeURIComponent(l.pathname + l.search + l.hash);
            }
        };

        /**
        * Gets the XHR hooked URL if called in a portlet context
        * @param {String} url The URL to get
        * @param {String} method The method to use to retrieve the XHR hooked URL
        * @param {Object} params The parameters
        * @return {String} The XHR hooked URL if available, the provided URL otherwise
        */
        self.getXhrHook = function (url, method, params) {
            method = method || "GET";
            if (window.G_XHR_HOOK && $.isFunction(G_XHR_HOOK)) {
                var p = {
                    "url": url,
                    "method": method
                };
                if (params) {
                    p["params"] = params;
                }
                return G_XHR_HOOK(p);
            }
            return null;
        };

        /**
         * Evaluates and returns the body of the specified response object.
         * Alternatively, a URL can be specified, in which case it will be
         * requested using a synchornous {@link #get} in order to acquire
         * the response object.
         * @static
         * @param {Object/String} response The response object or URL
         * @return {Object} The evaluated response body
         * @since 5.3
         */
        self.eval = function(response) {
            if (typeof response != "object") {
                response = $.ajax({
                    url: response,
                    type: 'get',
                    async: false
                });
            }
            try {
                // support responseText for backward compatibility (pre 5.3)
                return eval("(" + (response.body ? response.body :
                    response.responseText) + ")");
            } catch (e) {
            }
            return null;
        };

        return self;
    }());

}(Granite, Granite.Util, Granite.Sling, jQuery));
