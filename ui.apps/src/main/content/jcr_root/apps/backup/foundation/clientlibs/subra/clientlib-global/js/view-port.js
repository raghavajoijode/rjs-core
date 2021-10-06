var sViewport = {
    breakpoint : {
        tn: {
            min:0,
            max:639
        },
        xs: {
            min:640,
            max:799
        },
        sm: {
            min:800,
            max:959
        },
        md: {
            min:960,
            max:1199
        },
        lg: {
            min:1200,
            max:null
        }
    },
    width: function() {
        return window.innerWidth;
    },
    height: function() {
        return $(window).height();
    },
    is: function(b) {
        var bp = b.replace(/<|>|=/g, '');
        var gt = false;
        var lt = false;
        var eq = true;
        var w = this.width();
        if (b.match('^>') !== null) {
            gt = true;
            if (b.match('^>=') === null) {
                eq = false;
            }
        }else if (b.match('^<') !== null) {
            lt = true;
            if (b.match('^<=') === null) {
                eq = false;
            }
        }
        if (typeof this.breakpoint[bp] !== 'undefined' && this.breakpoint[bp] !== null) {
            if (eq && ((w <= this.breakpoint[bp].max || this.breakpoint[bp].max === null) && w >= this.breakpoint[bp].min)) {
                 return true;
            }else if (lt && (w < this.breakpoint[bp].min)) {
                 return true;
            }else if (gt && (w > this.breakpoint[bp].max || this.breakpoint[bp].max === null)) {
                 return true;
            }else{
                return false;
            }
        }
    },
    getBreakpoint : function() {
        if (this.is('lg')) {
            return 'lg';
        } else if (this.is('md')) {
            return 'md';
        } else if (this.is('sm')) {
            return 'sm';
        } else if (this.is('xs')) {
            return 'xs';
        } else {
            return 'tn';
        }
    }
};
