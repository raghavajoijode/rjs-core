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

/**
 * A helper class providing a set of Sling-related utilities.
 * @static
 * @singleton
 * @class Granite.Sling
 */
Granite.Sling = {

        /**
         * The selector for infinite hierarchy depth when retrieving
         * repository content.
         * @static
         * @final
         * @type String
         */
        SELECTOR_INFINITY: ".infinity",

        /**
         * The parameter name for the used character set.
         * @static
         * @final
         * @type String
         */
        CHARSET: "_charset_",

        /**
         * The parameter name for the status.
         * @static
         * @final
         * @type String
         */
        STATUS: ":status",

        /**
         * The parameter value for the status type "browser".
         * @static
         * @final
         * @type String
         */
        STATUS_BROWSER: "browser",

        /**
         * The parameter name for the operation.
         * @static
         * @final
         * @type String
         */
        OPERATION: ":operation",

        /**
         * The parameter value for the delete operation.
         * @static
         * @final
         * @type String
         */
        OPERATION_DELETE: "delete",

        /**
         * The parameter value for the move operation.
         * @static
         * @final
         * @type String
         */
        OPERATION_MOVE: "move",

        /**
         * The parameter name suffix for deleting.
         * @static
         * @final
         * @type String
         */
        DELETE_SUFFIX: "@Delete",

        /**
         * The parameter name suffix for setting a type hint.
         * @static
         * @final
         * @type String
         */
        TYPEHINT_SUFFIX: "@TypeHint",

        /**
         * The parameter name suffix for copying.
         * @static
         * @final
         * @type String
         */
        COPY_SUFFIX: "@CopyFrom",

        /**
         * The parameter name suffix for moving.
         * @static
         * @final
         * @type String
         */
        MOVE_SUFFIX: "@MoveFrom",

        /**
         * The parameter name for the ordering.
         * @static
         * @final
         * @type String
         */
        ORDER: ":order",

        /**
         * The parameter name for the replace flag.
         * @static
         * @final
         * @type String
         */
        REPLACE: ":replace",

        /**
         * The parameter name for the destination flag.
         * @static
         * @final
         * @type String
         */
        DESTINATION: ":dest",

        /**
         * The parameter name for the save parameter prefix.
         * @static
         * @final
         * @type String
         */
        SAVE_PARAM_PREFIX: ":saveParamPrefix",

        /**
         * The parameter name for input fields that should
         * be ignored by Sling.
         * @static
         * @final
         * @type String
         */
        IGNORE_PARAM: ":ignore",

        /**
         * The parameter name for login requests.
         * @static
         * @final
         * @type String
         */
        REQUEST_LOGIN_PARAM: "sling:authRequestLogin",

        /**
         * Login URL
         * @static
         * @final
         * @type String
         */
        LOGIN_URL: "/system/sling/login.html",

        /**
         * Logout URL
         * @static
         * @final
         * @type String
         */
        LOGOUT_URL: "/system/sling/logout.html"
};
