/*
 * PURPOSE -- TO be analyzed
 */

(function (document, Granite, util, http, $) {
    /**
     * A helper class providing a set of utilities related to internationalization (i18n).
     *
     * <h3>Locale Priorities</h3>
     * <p>The locale is read based on the following priorities:</p>
     * <ol>
     *   <li>manually specified locale</li>
     *   <li><code>document.documentElement.lang</code></li>
     *   <li><code>Granite.I18n.LOCALE_DEFAULT</code></li>
     * </ol>
     * 
     * <h3>Dictionary Priorities</h3>
     * <p>The dictionary URL is read based on the following priorities:</p>
     * <ol>
     *   <li>manually specified URL (<code>urlPrefix</code, <code>urlSuffix</code>)</li>
     *   <li><code>data-i18n-dictionary-src</code> attribute at &lt;html&gt; element, which has the type of <a href="http://tools.ietf.org/html/rfc6570">URI Template</a> string</li>
     *   <li>The URL resolved from default <code>urlPrefix</code> and <code>urlSuffix</code></li>
     * </ol>
     * 
     * <h3>URI Template of data-i18n-dictionary-src</h3>
     * <p>It expects the variable named <code>locale</code>, which will be fetched from the locale (based on priorities above).
     * E.g. <code>&lt;html lang="en" data-i18n-dictionary-src="/libs/cq/i18n/dict.{+locale}.json"&gt;</code>.</p>
     * 
     * @static
     * @singleton
     * @class Granite.I18n
     */
    Granite.I18n = (function() {

        /**
         * The map where the dictionaries are stored under their locale.
         * @private
         * @type Object
         */
        var dicts = {},

        /**
         * The prefix for the URL used to request dictionaries from the server.
         * @private
         * @type String
         */
            urlPrefix = "/libs/cq/i18n/dict.",

        /**
         * The suffix for the URL used to request dictionaries from the server.
         * @private
         * @type String
         */
            urlSuffix = ".json",

        /**
         * The manually specified locale as a String or a function that returns the locale as a string.
         * @private
         * @static
         * @type String
         */
            manualLocale = undefined,

        /**
         * If the current locale represents pseudo translations.
         * In that case the dictionary is expected to provide just a special
         * translation pattern to automatically convert all original strings.
         */
            pseudoTranslations = false,

            languages = null,

            self = {},
            
        /**
         * Indicates if the dictionary parameters are specified manually.
         */
            manualDictionary = false,
        
            getDictionaryUrl = function(locale) {
                if (manualDictionary) {
                    return urlPrefix + locale + urlSuffix;
                }
                
                var dictionarySrc = $("html").attr("data-i18n-dictionary-src");
                
                if (!dictionarySrc) {
                    return urlPrefix + locale + urlSuffix;
                }
                
                // dictionarySrc is a URITemplate
                // Use simple string replacement for now; for more complicated scenario, please use Granite.URITemplate
                return dictionarySrc.replace("{locale}", encodeURIComponent(locale)).replace("{+locale}", locale);
            };

        /**
         * The default locale (en).
         * @static
         * @final
         * @type String
         */
        self.LOCALE_DEFAULT = "en";

        /**
         * Language code for pseudo translations.
         * @static
         * @final
         * @type String
         */
        self.PSEUDO_LANGUAGE = "zz";

        /**
         * Dictionary key for pseudo translation pattern.
         * @static
         * @final
         * @type String
         */
        self.PSEUDO_PATTERN_KEY = "_pseudoPattern_";

        /**
         * Initializes I18n with the given config options:
         * <ul>
         * <li>locale: the current locale (defaults to "en")</li>
         * <li>urlPrefix: the prefix for the URL used to request dictionaries from
         * the server (defaults to "/libs/cq/i18n/dict.")</li>
         * <li>urlSuffix: the suffix for the URL used to request dictionaries from
         * the server (defaults to ".json")</li>
         * </ul>
         * Sample config. The dictioniary would be requested from
         * "/apps/i18n/dict.fr.json":
         <code><pre>{
         "locale": "fr",
         "urlPrefix": "/apps/i18n/dict.",
         "urlSuffix": ".json"
         }</pre></code>
         * @param {Object} config The config
         */
        self.init = function (config) {
            config  = config || {};
            
            this.setLocale(config.locale);
            this.setUrlPrefix(config.urlPrefix);
            this.setUrlSuffix(config.urlSuffix);
        };

        /**
         * Sets the current locale.
         * @static
         * @param {String/Function} locale The locale or a function that returns the locale as a string
         */
        self.setLocale = function (locale) {
            if (!locale) return;
            
            manualLocale = locale;
        };

        /**
         * Returns the current locale based on the priorities.
         * @static
         * @return {String} The locale
         */
        self.getLocale = function () {
            if ($.isFunction(manualLocale)) {
                // execute function first time only and store result in currentLocale
                manualLocale = manualLocale();
            }
            
            return manualLocale || document.documentElement.lang || self.LOCALE_DEFAULT;
        };

        /**
         * Sets the prefix for the URL used to request dictionaries from
         * the server. The locale and URL suffix will be appended.
         * @static
         * @param {String} prefix The URL prefix
         */
        self.setUrlPrefix = function (prefix) {
            if (!prefix) return;
            
            urlPrefix = prefix;
            manualDictionary = true;
        };

        /**
         * Sets the suffix for the URL used to request dictionaries from
         * the server. It will be appended to the URL prefix and locale.
         * @static
         * @param {String} suffix The URL suffix
         */
        self.setUrlSuffix = function (suffix) {
            if (!suffix) return;
            
            urlSuffix = suffix;
            manualDictionary = true;
        };

        /**
         * Returns the dictionary for the specified locale. This method
         * will request the dictionary using the URL prefix, the locale,
         * and the URL suffix. If no locale is specified, the current
         * locale is used.
         * @static
         * @param {String} locale (optional) The locale
         * @return {Object} The dictionary
         */
        self.getDictionary = function (locale) {
            locale = locale || self.getLocale();
            
            if (!dicts[locale]) {
                pseudoTranslations = (locale.indexOf(self.PSEUDO_LANGUAGE) == 0);

                try {
                    var response = $.ajax(getDictionaryUrl(locale), {
                        async: false,
                        dataType: "json"
                    });
                    dicts[locale] = $.parseJSON(response.responseText);
                } catch (e) {}
                if (!dicts[locale]) {
                    dicts[locale] = {};
                }
            }
            return dicts[locale];
        };

        /**
         * Translates the specified text into the current language.
         * @static
         * @param {String} text The text to translate
         * @param {String[]} snippets The snippets replacing <code>{n}</code> (optional)
         * @param {String} note A hint for translators (optional)
         * @return {String} The translated text
         */
        self.get = function (text, snippets, note) {
            var dict, newText, lookupText;

            dict = self.getDictionary();

            // note that pseudoTranslations is initialized in the getDictionary() call above
            lookupText = pseudoTranslations ? self.PSEUDO_PATTERN_KEY :
                note ? text + " ((" + note + "))" :
                    text;
            if (dict) {
                newText = dict[lookupText];
            }
            if (!newText) {
                newText = text;
            }
            if (pseudoTranslations) {
                newText = newText.replace("{string}", text).replace("{comment}", note ? note : "");
            }
            return util.patchText(newText, snippets);
        };

        /**
         * Translates the specified text into the current language. Use this
         * method to translate String variables, e.g. data from the server.
         * @static
         * @param {String} text The text to translate
         * @param {String} note A hint for translators (optional)
         * @return {String} The translated text
         */
        self.getVar = function (text, note) {
            if (!text) {
                return null;
            }
            return self.get(text, null, note);
        };

        /**
         * Returns the available languages, including a "title" property with a display name:
         * for instance "German" for "de" or "German (Switzerland)" for "de_ch".
         * @static
         * @return {Object} An object with language codes as keys and an object with "title",
         *                  "language", "country" and "defaultCountry" members.
         */
        self.getLanguages = function () {
            if (!languages) {
                try {
                    // use overlay servlet so customers can define /apps/wcm/core/resources/languages
                    var json = http.eval("/libs/wcm/core/resources/languages.overlay.infinity.json"); // TODO: broken!!!
                    $.each(json, function(name, lang) {
                        lang.title = self.getVar(lang.language);
                        if (lang.title && lang.country && lang.country != "*") {
                            lang.title += " ("+self.getVar(lang.country)+")";
                        }
                    });
                    languages = json;
                } catch (e) {
                    languages = {};
                }
            }
            return languages;
        };

        /**
         * Parses a language code string such as "de_CH" and returns an object with
         * language and country extracted. The delimiter can be "_" or "-".
         * @static
         * @param {String} langCode a language code such as "de" or "de_CH" or "de-ch"
         * @return {Object} an object with "code" ("de_CH"), "language" ("de") and "country" ("CH")
         *                  (or null if langCode was null)
         */
        self.parseLocale = function (langCode) {
            if (!langCode) {
                return null;
            }
            var pos = langCode.indexOf("_");
            if (pos < 0) {
                pos = langCode.indexOf("-");
            }

            var language, country;
            if (pos < 0) {
                language = langCode;
                country = null;
            } else {
                language = langCode.substring(0, pos);
                country = langCode.substring(pos + 1);
            }
            return {
                code: langCode,
                language: language,
                country: country
            };
        };

        return self;

    }());

}(document, Granite, Granite.Util, Granite.HTTP, jQuery));
