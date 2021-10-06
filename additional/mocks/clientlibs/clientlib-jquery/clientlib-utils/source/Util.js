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
(function (Granite, $) {
    /**
     * A helper class providing a set of general utilities.
     * @static
     * @singleton
     * @class Granite.Util
     */
    Granite.Util = (function() {

        var self = {

            /**
             * Replaces occurrences of <code>{n}</code> in the specified text with
             * the texts from the snippets.
             * <p>Example 1 (single snippet):<pre><code>
    var text = Granite.Util.patchText("{0} has signed in.", "Jack");
               </code></pre>Result 1:<pre><code>
    Jack has signed in.
               </code></pre></p>
             * <p>Example 2 (multiple snippets):<pre><code>
    var text = "{0} {1} has signed in from {2}.";
    text = Granite.Util.patchText(text, ["Jack", "McFarland", "x.x.x.x"]);
               </code></pre>Result 2:<pre><code>
    Jack McFarland has signed in from x.x.x.x.
               </code></pre></p>
             * @static
             * @param {String} text The text
             * @param {String/String[]} snippets The text(s) replacing
             *        <code>{n}</code>
             * @return {String} The patched text
             */
            patchText: function(text, snippets) {
                if (snippets) {
                    if (!$.isArray(snippets)) {
                        text = text.replace("{0}", snippets);
                    } else {
                        for (var i=0; i < snippets.length; i++) {
                            text = text.replace(("{" + i + "}"), snippets[i]);
                        }
                    }
                }
                return text;
            },

            /**
             * Returns the top most accessible window. Check {@link setIFrameMode} to avoid security exception message
             * on WebKit browsers if this method is called in an iFrame included in a window from different domain.
             * @static
             * @return {Window} The top window
             */
            getTopWindow: function() {
                var win = window;
                if( this.iFrameTopWindow ) {
                    return this.iFrameTopWindow;
                }
                try {
                    // try to access parent
                    // win.parent.location.href throws an exception if not authorized (e.g. different location in a portlet)
                    while(win.parent && win !== win.parent && win.parent.location.href) {
                        win = win.parent;
                    }
                } catch( error) {}
                return win;
            },

            /**
             * Allows to define if Granite.Util is running in an iFrame and parent window is in another domain
             * (and optionally define what would be the top window in that case.
             * This is necessary to use {@link getTopWindow} in a iFrame on WebKit based browsers because
             * {@link getTopWindow} iterates on parent windows to find the top one which triggers a security exception
             * if one parent window is in a different domain. Exception cannot be caught but is not breaking the JS
             * execution.
             * @param {Object} topWindow (optional) The iFrame top window. Must be running on the same host to avoid
             * security exception. Defaults to window.
             */
            setIFrameMode: function(topWindow) {
                this.iFrameTopWindow = topWindow || window;
            },

            /**
             * Applies default properties if inexistent inzo the base object.
             * Child objects are merged recursively.
             * REMARK: 
             *   - objects are recursively merged
             *   - simple type obejct properties are copied over the base
             *   - arrays are cloned and override the base (no value merging)
             * 
             * @static
             * @param {Object} base object
             * @param {Object} pass objects to be copied onto the base
             * @return {Object} The base object with defaults
             */
            applyDefaults: function() {
                var override, base = arguments[0] || {};

                for (var i=1; i < arguments.length; i++) {
                    override = arguments[i];

                    for (var name in override) {
                        var value = override[name];

                        if (override.hasOwnProperty(name) && value !== undefined) {

                            if (value !== null && typeof value === "object" && !(value instanceof Array)) {
                                // nested object
                                base[name] = self.applyDefaults(base[name], value);
                            } else if (value instanceof Array) {
                                //override array
                                base[name] = value.slice(0);
                            } else {
                                // simple type
                                base[name] = value;
                            }
                        }
                        
                    }
                }

                return base;
            },

            /**
             * Get keycode from event
             * @param event Event
             * @returns {Number} Keycode
             */
            getKeyCode: function(event) {
                return event.keyCode ? event.keyCode : event.which;
            }

        };

        return self;

    }());

}(Granite, jQuery));