! function a(b, c, d) {
    function e(g, h) {
        if (!c[g]) {
            if (!b[g]) {
                var i = "function" == typeof require && require;
                if (!h && i) return i(g, !0);
                if (f) return f(g, !0);
                var j = new Error("Cannot find module '" + g + "'");
                throw j.code = "MODULE_NOT_FOUND", j
            }
            var k = c[g] = {
                exports: {}
            };
            b[g][0].call(k.exports, function(a) {
                var c = b[g][1][a];
                return e(c ? c : a)
            }, k, k.exports, a, b, c, d)
        }
        return c[g].exports
    }
    for (var f = "function" == typeof require && require, g = 0; g < d.length; g++) e(d[g]);
    return e
}({
    1: [function(a, b, c) {
        ! function(a) {
            return a.fn.mason = function(b, c) {
                var d, e, f, g, h, i;
                return g = {
                    itemSelector: "",
                    ratio: 0,
                    sizes: [],
                    columns: [
                        [0, 480, 1],
                        [480, 780, 2],
                        [780, 1080, 3],
                        [1080, 1320, 4],
                        [1320, 1680, 5]
                    ],
                    promoted: [],
                    filler: {
                        itemSelector: b.itemSelector,
                        filler_class: "mason_filler",
                        keepDataAndEvents: !1
                    },
                    randomSizes: !1,
                    randomFillers: !1,
                    layout: "none",
                    gutter: 0,
                    debug: !1
                }, d = null, f = {
                    container: a("<div id='debug'></div>"),
                    block: "<div class='mason-debug' style='background-color: rgba(244, 67, 54, .5); float: left;'></div>"
                }, i = "<div class='mason_clear' style='clear:both; position:relative;'></div>", c && (e = {
                    complete: c
                }), h = {
                    block: {
                        height: 0,
                        width: 0
                    },
                    matrix: [],
                    startWidth: 0
                }, this.each(function() {
                    var j, k, l, m, n, o, p, q, r;
                    return p = a.extend(g, b), j = a.extend(e, c), d = a(this), q = function() {
                        return p.debug && console.log("SETUP"), d.children(".mason_clear").length < 1 && d.append(i), h.block.height = Math.round(parseFloat(d.width() / k() / p.ratio)).toFixed(2), h.block.width = Math.round(parseFloat(d.width() / k())).toFixed(2), h.startWidth = d.width(), r(), p.debug ? (console.log("############## Running In Debug Mode ##############"), l()) : void 0
                    }, r = function() {
                        var b;
                        return 1 !== k() ? (d.children("" + p.itemSelector, "." + p.filler.filler_class).each(function() {
                            var c, d, e, f, g, i, j;
                            for (b = a(this), d = 0, e = !1, f = 0; d < p.promoted.length;) b.hasClass(p.promoted[d][0]) && (e = !0, f = d), d++;
                            return e ? (i = p.promoted[f], b.data("size", f), b.data("promoted", !0), c = parseFloat(h.block.height * i[2]).toFixed(0), c -= 2 * p.gutter, j = parseFloat(h.block.width * i[1]).toFixed(0), j -= 2 * p.gutter) : (g = Math.floor(Math.random() * p.sizes.length), i = p.sizes[g], b.data("size", g), c = parseFloat(h.block.height * i[1]).toFixed(0), c -= 2 * p.gutter, j = parseFloat(h.block.width * i[0]).toFixed(0), j -= 2 * p.gutter), b.height(c + "px"), b.width(j + "px"), b.css({
                                margin: p.gutter
                            })
                        }), n()) : (b = d.children("" + p.itemSelector), b.height(h.block.height - 2 * p.gutter), b.width(h.block.width - 2 * p.gutter), b.css({
                            margin: p.gutter
                        }), "undefined" != typeof j.complete ? j.complete() : void 0)
                    }, n = function() {
                        var b, c, e, f, g;
                        for (e = k(), f = d.height(), b = Math.round(f / h.block.height), h.matrix = [], g = 0; b > g;) {
                            for (h.matrix[g] = [], c = 0; e > c;) h.matrix[g][c] = !1, c++;
                            g++
                        }
                        return d.children("" + p.itemSelector).each(function() {
                            var b, c, d, e, f, i, j, k, l, m;
                            for (b = a(this), i = Math.round(b.position().left / h.block.width), l = Math.round(b.position().top / h.block.height), k = parseFloat(b.data("size")), b.data("promoted") ? (f = p.promoted[k][2], m = p.promoted[k][1], c = f * m) : (f = p.sizes[k][1], m = p.sizes[k][0], c = f * m), g = 0, j = []; c > g;) {
                                for (d = 0; f > d;) {
                                    for (e = 0, h.matrix[l + d][i] = !0; m > e;) h.matrix[l + d][i + e] = !0, e++;
                                    d++
                                }
                                j.push(g++)
                            }
                            return j
                        }), m()
                    }, m = function() {
                        var b, c, e, f, g, i, k, l, m;
                        for (i = 0, f = a("" + p.filler.itemSelector).not("." + p.filler.filler_class).length, e = -1; i < h.matrix.length;) {
                            for (c = 0; c < h.matrix[i].length;) h.matrix[i][c] || (g = h.block.height, k = h.block.width, l = i * g + p.gutter, m = c * k + p.gutter, g -= 2 * p.gutter, k -= 2 * p.gutter, p.randomFillers ? e = Math.floor(Math.random() * f) : (f > e && e++, e === f && (e = 0)), b = a("" + p.filler.itemSelector).not("." + p.filler.filler_class).eq(e).clone(p.filler.keepDataAndEvents), b.addClass(p.filler.filler_class), b.css({
                                position: "absolute",
                                top: l + "px",
                                left: m + "px",
                                height: g + "px",
                                width: k + "px",
                                margin: "0px"
                            }), b.appendTo(d)), c++;
                            i++
                        }
                        return d.width() < h.startWidth ? a(window, d).trigger("resize") : "undefined" != typeof j.complete ? j.complete() : void 0
                    }, k = function() {
                        var a, b, c, e;
                        if (e = parseFloat(d.width()), a = 0, b = p.columns.length - 1, e >= p.columns[b[1]]) a = p.columns[b[2]];
                        else
                            for (c = 0; b >= c;) e > p.columns[c][0] && p.columns[c][1] && (a = p.columns[c][2]), c++;
                        return Math.floor(a)
                    }, l = function() {
                        var b, c, e, g, j, l, m;
                        for (b = d.parent(), j = k(), l = d.height(), e = l / h.block.height, f.container.css({
                                position: "absolute",
                                top: "0",
                                left: "0",
                                height: d.height(),
                                width: d.width()
                            }), m = 0; e > m;) {
                            for (g = 0; j > g;) c = a(f.block), c.css({
                                height: h.block.height - 2 * p.gutter,
                                width: h.block.width - 2 * p.gutter,
                                margin: p.gutter
                            }), f.container.append(c), g++;
                            m++
                        }
                        return f.container.append(i), b.prepend(f.container)
                    }, "fluid" === p.layout && (o = null, a(window, d).on("resize", function(b) {
                        return function(b) {
                            return a("." + p.filler.filler_class).remove(), h.matrix = [], clearTimeout(o), o = null, o = setTimeout(function() {
                                return q()
                            }, 0)
                        }
                    }(this))), q()
                }), {
                    destroy: function() {
                        return a(window, d).off("resize")
                    }
                }
            }
        }(jQuery)
    }, {}]
}, {}, [1]);