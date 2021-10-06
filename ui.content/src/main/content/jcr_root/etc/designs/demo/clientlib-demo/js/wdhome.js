function viewport() {
    var a = window,
        b = "inner";
    return "innerWidth" in window || (b = "client", a = document.documentElement || document.body), {
        width: a[b + "Width"],
        height: a[b + "Height"]
    }
}
$(document).ready(function() {
    function b() {
        $(".path-red").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-yellow").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-pink").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-white").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-cyan").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-blue").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-green").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        })
    }

    function c() {
        $(".path-red").css({
            fill: "#F15A24",
            stroke: "#F15A24"
        }), $(".path-yellow").css({
            fill: "#F15A24",
            stroke: "#F15A24"
        }), $(".path-pink").css({
            fill: "#F15A24",
            stroke: "#F15A24"
        }), $(".path-white").css({
            fill: "#F15A24",
            stroke: "#F15A24"
        }), $(".path-cyan").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-blue").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-green").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".strategy-text").show(), $(".design-text").hide(), $(".tech-text").hide(), $(".strategy-text-wrapper").show(), $(".design-text-wrapper").hide(), $(".technology-text-wrapper").hide(), $(".insight-wrapper").hide(), $(".interaction-wrapper").hide(), $(".integration-wrapper").hide(), $(".cje-wrapper").hide()
    }

    function d() {
        $(".path-blue").css({
            fill: "#3FA9F5",
            stroke: "#3FA9F5"
        }), $(".path-yellow").css({
            fill: "#3FA9F5",
            stroke: "#3FA9F5"
        }), $(".path-cyan").css({
            fill: "#3FA9F5",
            stroke: "#3FA9F5"
        }), $(".path-white").css({
            fill: "#3FA9F5",
            stroke: "#3FA9F5"
        }), $(".path-pink").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-green").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-red").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".design-text").show(), $(".strategy-text").hide(), $(".tech-text").hide(), $(".strategy-text-wrapper").hide(), $(".design-text-wrapper").show(), $(".technology-text-wrapper").hide(), $(".insight-wrapper").hide(), $(".interaction-wrapper").hide(), $(".integration-wrapper").hide(), $(".cje-wrapper").hide()
    }

    function e() {
        $(".path-green").css({
            fill: "#7AC943",
            stroke: "#7AC943"
        }), $(".path-pink").css({
            fill: "#7AC943",
            stroke: "#7AC943"
        }), $(".path-cyan").css({
            fill: "#7AC943",
            stroke: "#7AC943"
        }), $(".path-white").css({
            fill: "#7AC943",
            stroke: "#7AC943"
        }), $(".path-yellow").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-blue").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-red").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".tech-text").show(), $(".strategy-text").hide(), $(".design-text").hide(), $(".strategy-text-wrapper").hide(), $(".design-text-wrapper").hide(), $(".technology-text-wrapper").show(), $(".insight-wrapper").hide(), $(".interaction-wrapper").hide(), $(".integration-wrapper").hide(), $(".cje-wrapper").hide()
    }

    function f() {
        $(".path-yellow").css({
            fill: "#FAED23",
            stroke: "#FAED23"
        }), $(".path-green").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-pink").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-cyan").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-white").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-blue").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-red").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".tech-text").hide(), $(".strategy-text").show(), $(".design-text").show(), $(".strategy-text-wrapper").hide(), $(".design-text-wrapper").hide(), $(".technology-text-wrapper").hide(), $(".insight-wrapper").hide(), $(".interaction-wrapper").show(), $(".integration-wrapper").hide(), $(".cje-wrapper").hide()
    }

    function g() {
        $(".path-pink").css({
            fill: "#F27AAA",
            stroke: "#F27AAA"
        }), $(".path-green").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-yellow").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-cyan").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-white").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-blue").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-red").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".tech-text").show(), $(".strategy-text").show(), $(".design-text").hide(), $(".strategy-text-wrapper").hide(), $(".design-text-wrapper").hide(), $(".technology-text-wrapper").hide(), $(".insight-wrapper").show(), $(".interaction-wrapper").hide(), $(".integration-wrapper").hide(), $(".cje-wrapper").hide()
    }

    function h() {
        $(".path-cyan").css({
            fill: "#6FCCDD",
            stroke: "#6FCCDD"
        }), $(".path-green").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-yellow").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-pink").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-white").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-blue").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-red").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-white").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".tech-text").show(), $(".strategy-text").hide(), $(".design-text").show(), $(".strategy-text-wrapper").hide(), $(".design-text-wrapper").hide(), $(".technology-text-wrapper").hide(), $(".insight-wrapper").hide(), $(".interaction-wrapper").hide(), $(".integration-wrapper").show(), $(".cje-wrapper").hide()
    }

    function i() {
        $(".path-yellow").css({
            fill: "#FAED23",
            stroke: "#FAED23"
        }), $(".path-pink").css({
            fill: "#F27AAA",
            stroke: "#F27AAA"
        }), $(".path-cyan").css({
            fill: "#6FCCDD",
            stroke: "#6FCCDD"
        }), $(".path-white").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-blue").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-red").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".path-green").css({
            fill: "transparent",
            stroke: "#B3B3B3"
        }), $(".tech-text").show(), $(".strategy-text").show(), $(".design-text").show(), $(".strategy-text-wrapper").hide(), $(".design-text-wrapper").hide(), $(".technology-text-wrapper").hide(), $(".insight-wrapper").hide(), $(".interaction-wrapper").hide(), $(".integration-wrapper").hide(), $(".cje-wrapper").show()
    }
    if ($("html, body").scrollTop(0), "undefined" != typeof window.innerWidth) var a = parseInt(window.innerWidth);
    $(".wd-navbar-container").css("background-color", "transparent"), $(window).scroll(function() {
        0 == $(window).scrollTop() ? $("#wdNavbar").is(":visible") ? a > 767 && $(".wd-navbar-container").css("background-color", "transparent") : $(".wd-navbar-container").css("background-color", "transparent") : $(".wd-navbar-container").css("background-color", "black")
    }), $("#wdNavbar").on("show.bs.collapse", function() {
        0 == $(window).scrollTop() && $(".wd-navbar-container").css("background-color", "black")
    }), $("#wdNavbar").on("hide.bs.collapse", function() {
        0 == $(window).scrollTop() && $(".wd-navbar-container").css("background-color", "transparent")
    }), $(".wd-navlogo-digital").hide(), $(window).scroll(function() {
        0 == $(window).scrollTop() ? a > 767 ? $("#wdNavbar").is(":visible") && $(".wd-navlogo-digital").stop().hide(100) : $("#wdNavbar").is(":visible") || $(".wd-navlogo-digital").stop().hide(100) : $(".wd-navlogo-digital").stop().show(100)
    }), $("#wdNavbar").on("show.bs.collapse", function() {
        0 == $(window).scrollTop() && $(".wd-navlogo-digital").stop().show(100)
    }), $("#wdNavbar").on("hide.bs.collapse", function() {
        0 == $(window).scrollTop() && $(".wd-navlogo-digital").stop().hide(100)
    }), $(function() {
        var a = window.location.hash;
        a && $('ul.nav a[href="' + a + '"]').tab("show"), $(".wdhome-canavlst a").click(function(a) {
            $(this).tab("show"), window.location.hash = this.hash, console.log(this.hash)
        })
    }), $("#AllcontntAggr").mason({
        itemSelector: ".allcaItem",
        ratio: 1.77,
        sizes: [
            [2, 2]
        ],
        promoted: [
            ["CAfeatlrg", 4, 4],
            ["CAfeatured", 4, 3],
            ["CAwidesmlr", 3, 2],
            ["CAwidefull", 6, 2],
            ["CAtalllrg", 2, 3],
            ["CAwidesml", 4, 2],
            ["CAStandard", 2, 2]
        ],
        columns: [
            [0, 1024, 1],
            [1025, 2560, 6]
        ],
        filler: {
            itemSelector: ".allwdfiller",
            filler_class: "mason-filler-wd"
        },
        gutter: 2.5,
        layout: "fluid"
    }, function() {
        $(".caItemTxt").show()
    }), $(".insCNavElem").on("shown.bs.tab", function() {
        $(".caItemTxt").hide(), $("#InscontntAggr").mason({
            itemSelector: ".inscaItem",
            ratio: 1.77,
            sizes: [
                [2, 2]
            ],
            promoted: [
                ["CAfeatlrg", 4, 4],
                ["CAfeatured", 4, 3],
                ["CAwidesmlr", 3, 2],
                ["CAwidefull", 6, 2],
                ["CAtalllrg", 2, 3],
                ["CAwidesml", 4, 2],
                ["CAStandard", 2, 2]
            ],
            columns: [
                [0, 1024, 1],
                [1025, 2560, 6]
            ],
            filler: {
                itemSelector: ".inswdfiller",
                filler_class: "mason-filler-wd"
            },
            gutter: 2.5,
            layout: "fluid"
        }, function() {
            $(".caItemTxt").show()
        })
    }), $(".casCNavElem").on("shown.bs.tab", function() {
        $(".caItemTxt").hide(), $("#CascontntAggr").mason({
            itemSelector: ".cascaItem",
            ratio: 1.77,
            sizes: [
                [2, 2]
            ],
            promoted: [
                ["CAfeatlrg", 4, 4],
                ["CAfeatured", 4, 3],
                ["CAwidesmlr", 3, 2],
                ["CAwidefull", 6, 2],
                ["CAtalllrg", 2, 3],
                ["CAwidesml", 4, 2],
                ["CAStandard", 2, 2]
            ],
            columns: [
                [0, 1024, 1],
                [1025, 2560, 6]
            ],
            filler: {
                itemSelector: ".caswdfiller",
                filler_class: "mason-filler-wd"
            },
            gutter: 2.5,
            layout: "fluid"
        }, function() {
            $(".caItemTxt").show()
        })
    }), $(".evtCNavElem").on("shown.bs.tab", function() {
        $(".caItemTxt").hide(), $("#EvtcontntAggr").mason({
            itemSelector: ".evtcaItem",
            ratio: 1.77,
            sizes: [
                [2, 2]
            ],
            promoted: [
                ["CAfeatlrg", 4, 4],
                ["CAfeatured", 4, 3],
                ["CAwidesmlr", 3, 2],
                ["CAwidefull", 6, 2],
                ["CAtalllrg", 2, 3],
                ["CAwidesml", 4, 2],
                ["CAStandard", 2, 2]
            ],
            columns: [
                [0, 1024, 1],
                [1025, 2560, 6]
            ],
            filler: {
                itemSelector: ".evtwdfiller",
                filler_class: "mason-filler-wd"
            },
            gutter: 2.5,
            layout: "fluid"
        }, function() {
            $(".caItemTxt").show()
        })
    }), $(".newCNavElem").on("shown.bs.tab", function() {
        $(".caItemTxt").hide(), $("#NewcontntAggr").mason({
            itemSelector: ".newcaItem",
            ratio: 1.77,
            sizes: [
                [2, 2]
            ],
            promoted: [
                ["CAfeatlrg", 4, 4],
                ["CAfeatured", 4, 3],
                ["CAwidesmlr", 3, 2],
                ["CAwidefull", 6, 2],
                ["CAtalllrg", 2, 3],
                ["CAwidesml", 4, 2],
                ["CAStandard", 2, 2]
            ],
            columns: [
                [0, 1024, 1],
                [1025, 2560, 6]
            ],
            filler: {
                itemSelector: ".newwdfiller",
                filler_class: "mason-filler-wd"
            },
            gutter: 2.5,
            layout: "fluid"
        }, function() {
            $(".caItemTxt").show()
        })
    }), $(".strategy-text").hide(), $(".design-text").hide(), $(".tech-text").hide(), $("#wdhome-circles").bind("inview", function(a, c) {
        1 == c && $("#wdhome-circles").hasClass("htc-animate") && ($("#wdhome-circles").removeClass("htc-animate"), b(), $(".cje-wrapper").fadeIn(), $(".path-red").css({
            fill: "#F15A24",
            stroke: "#F15A24"
        }), $(".path-yellow").css({
            fill: "#F15A24",
            stroke: "#F15A24"
        }), $(".path-pink").css({
            fill: "#F15A24",
            stroke: "#F15A24"
        }), $(".path-white").css({
            fill: "#F15A24",
            stroke: "#F15A24"
        }), setTimeout(function() {
            $(".path-red").css({
                fill: "transparent",
                stroke: "#B3B3B3"
            }), $(".path-yellow").css({
                fill: "transparent",
                stroke: "#B3B3B3"
            }), $(".path-pink").css({
                fill: "transparent",
                stroke: "#B3B3B3"
            }), $(".path-white").css({
                fill: "transparent",
                stroke: "#B3B3B3"
            }), b(), $(".path-blue").css({
                fill: "#3FA9F5",
                stroke: "#3FA9F5"
            }), $(".path-yellow").css({
                fill: "#3FA9F5",
                stroke: "#3FA9F5"
            }), $(".path-cyan").css({
                fill: "#3FA9F5",
                stroke: "#3FA9F5"
            }), $(".path-pink").css({
                fill: "transparent",
                stroke: "#B3B3B3"
            }), $(".path-white").css({
                fill: "#3FA9F5",
                stroke: "#3FA9F5"
            })
        }, 500), setTimeout(function() {
            $(".path-blue").css({
                fill: "transparent",
                stroke: "#B3B3B3"
            }), $(".path-yellow").css({
                fill: "transparent",
                stroke: "#B3B3B3"
            }), $(".path-cyan").css({
                fill: "transparent",
                stroke: "#B3B3B3"
            }), $(".path-white").css({
                fill: "transparent",
                stroke: "#B3B3B3"
            }), b(), $(".path-green").css({
                fill: "#7AC943",
                stroke: "#7AC943"
            }), $(".path-pink").css({
                fill: "#7AC943",
                stroke: "#7AC943"
            }), $(".path-cyan").css({
                fill: "#7AC943",
                stroke: "#7AC943"
            }), $(".path-yellow").css({
                fill: "transparent",
                stroke: "#B3B3B3"
            }), $(".path-white").css({
                fill: "#7AC943",
                stroke: "#7AC943"
            })
        }, 1e3), setTimeout(function() {
            $(".path-green").css({
                fill: "transparent",
                stroke: "#B3B3B3"
            }), $(".path-pink").css({
                fill: "transparent",
                stroke: "#B3B3B3"
            }), $(".path-cyan").css({
                fill: "transparent",
                stroke: "#B3B3B3"
            }), $(".path-white").css({
                fill: "transparent",
                stroke: "#B3B3B3"
            }), b(), $(".path-pink").css({
                fill: "#F27AAA",
                stroke: "#F27AAA"
            })
        }, 1500), setTimeout(function() {
            $(".path-pink").css({
                fill: "transparent",
                stroke: "#B3B3B3"
            }), b(), $(".path-yellow").css({
                fill: "#FAED23",
                stroke: "#FAED23"
            })
        }, 2e3), setTimeout(function() {
            $(".path-yellow").css({
                fill: "transparent",
                stroke: "#B3B3B3"
            }), b(), $(".path-cyan").css({
                fill: "#6FCCDD",
                stroke: "#6FCCDD"
            })
        }, 2500), setTimeout(function() {
            $(".path-cyan").css({
                fill: "transparent",
                stroke: "#B3B3B3"
            }), b(), $(".path-yellow").css({
                fill: "#FAED23",
                stroke: "#FAED23"
            }), $(".path-pink").css({
                fill: "#F27AAA",
                stroke: "#F27AAA"
            }), $(".path-cyan").css({
                fill: "#6FCCDD",
                stroke: "#6FCCDD"
            }), $(".path-white").css({
                stroke: "#transparent"
            }), $(".design-text").fadeIn(), $(".strategy-text").fadeIn(), $(".tech-text").fadeIn(), $(".three-circles-nav-dots").siblings().removeClass("selected"), $("#three-circles-cje").addClass("selected"), $(".three-circles-nav-dots-cntnr").fadeIn()
        }, 3e3))
    }), $(".three-circles-nav-dots").click(function() {
        var a = $(this).attr("id");
        switch ($(this).siblings().removeClass("selected"), $(this).addClass("selected"), a) {
            case "three-circles-strat":
                c();
                break;
            case "three-circles-design":
                d();
                break;
            case "three-circles-tech":
                e();
                break;
            case "three-circles-insights":
                g();
                break;
            case "three-circles-interaction":
                f();
                break;
            case "three-circles-integration":
                h();
                break;
            case "three-circles-strat":
                i();
                break;
            default:
                i()
        }
    }), $(".path-red").on("click", function () {
        c();
        $('#three-circles-strat').addClass('selected').siblings().removeClass('selected');
    }), $(".path-blue").on("click", function () {
        d();
        $('#three-circles-design').addClass('selected').siblings().removeClass('selected');
    }), $(".path-green").on("click", function () {
        e();
        $('#three-circles-tech').addClass('selected').siblings().removeClass('selected');
    }), $(".path-yellow").on("click", function () {
        f();
        $('#three-circles-interaction').addClass('selected').siblings().removeClass('selected');
    }), $(".path-pink").on("click", function () {
        g();
        $('#three-circles-insights').addClass('selected').siblings().removeClass('selected');
    }), $(".path-cyan").on("click", function () {
        h();
        $('#three-circles-integration').addClass('selected').siblings().removeClass('selected');
    }), $(".path-white").on("click", function () {
        i();
        $('#three-circles-cje').addClass('selected').siblings().removeClass('selected');
    })
});
