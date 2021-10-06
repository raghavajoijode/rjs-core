/*
 * ADOBE CONFIDENTIAL -- Overridden by Darwin
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
(function (Granite, util, http, $) {

    /**
     * A tool to determine whether any opt-out cookie is set and whether a given cookie name
     * is white-listed. The opt-out and white-list cookie names are determined by a server
     * side configuration (com.adobe.granite.security.commons.OptOutService) and provided to
     * this tool by an optionally included component (/libs/granite/security/components/optout)
     * which provides a global JSON object named <code>GraniteOptOutConfig</code>.
     *
     * @static
     * @singleton
     * @class Granite.OptOutUtil
     */
    Granite.OptOutUtil = (function () {

        var self = {};

        /**
         * Contains the names of cookies the presence of which indicates the user has opted out.
         * @private
         * @type Array
         */
        var optOutCookieNames = [];

        /**
         * Contains the names of cookies which may still be set in spite of the user having opted out.
         * @private
         * @type Array
         */
        var whitelistedCookieNames = [];

        /**
         * Initializes this tool with an opt-out configuration. The following options are supported:
         * <ul>
         *     <li>cookieNames: an array of cookie names representing opt-out cookies. Defaults to empty.</li>
         *     <li>whitelistCookieNames: an array of cookies representing white-listed cookies. Defaults to empty.</li>
         * </ul>
         * Sample config:
         * <code>
         *     <pre>
         *         {
         *         "cookieNames":["omniture_optout","cq-opt-out"],
         *         "whitelistCookieNames":["someAppCookie", "anotherImportantAppCookie"]
         *         }
         *     </pre>
         * </code>
         * @param config The opt-out configuration
         */
        self.init = function (config) {
            if (config) {
                optOutCookieNames = config.cookieNames
                        ? config.cookieNames : optOutCookieNames;
                whitelistedCookieNames = config.whitelistCookieNames
                        ? config.whitelistCookieNames : whitelistedCookieNames;
            }
        };

        /**
         * Returns the array of configured cookie names representing opt-out cookies.
         * @static
         * @return {Array} The cookie names
         */
        self.getCookieNames = function () {
            return optOutCookieNames;
        };

        /**
         * Returns the array of configured cookie names representing white-listed cookies.
         * @static
         * @return {Array} The cookie names
         */
        self.getWhitelistCookieNames = function () {
            return whitelistedCookieNames;
        };

        /**
         * Determines whether the user (browser) has elected to opt-out. This is indicated by the presence of
         * one of the cookies retrieved through #getCookieNames().
         * @return {Boolean} True if an opt-cookie was found in the browser's cookies.
         */
        self.isOptedOut = function () {
            var browserCookies = document.cookie.split(";");
            for (var i = 0; i < browserCookies.length; i++) {
                var cookie = browserCookies[i];
                var cookieName = $.trim(cookie.split("=")[0]);
                if ($.inArray(cookieName, self.getCookieNames()) > -1) {
                    return true;
                }
            }

            return false;
        };

        /**
         * Determines whether the given <code>cookieName</code> may be used to set a cookie. This is the case
         * if either opt-out is inactive (#isOptedOut() == false) or it is active and the give cookie name was
         * found in the white-list (#getWhitelistCookieNames()).
         * @param cookieName The name of the cookie to check.
         * @return {Boolean} True if a cookie of this name may be used with respect to the opt-out status.
         */
        self.maySetCookie = function (cookieName) {
            return !(self.isOptedOut() && $.inArray(cookieName, self.getWhitelistCookieNames()) === -1);
        };

        return self;

    }());

}(Granite, Granite.Util, Granite.HTTP, jQuery));