import { useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { useTheme } from "../main";

export default function LandingPage() {
  const navigate = useNavigate();
  const { theme, toggleTheme } = useTheme();
  const boardRef = useRef(null);

  useEffect(() => {
    const link1 = document.createElement("link");
    link1.rel = "preconnect";
    link1.href = "https://fonts.googleapis.com";
    const link2 = document.createElement("link");
    link2.rel = "preconnect";
    link2.href = "https://fonts.gstatic.com";
    link2.crossOrigin = "anonymous";
    const link3 = document.createElement("link");
    link3.rel = "stylesheet";
    link3.href =
      "https://fonts.googleapis.com/css2?family=Bebas+Neue&family=IBM+Plex+Mono:wght@400;500;600&family=IBM+Plex+Sans:wght@400;500;600;700&display=swap";
    document.head.append(link1, link2, link3);

    return () => {
      link1.remove();
      link2.remove();
      link3.remove();
    };
  }, []);

  useEffect(() => {
    const words = [
      "SOFTWARE ENGINEER",
      "PRODUCT DESIGNER",
      "DATA ANALYST",
      "GROWTH MARKETER",
      "BACKEND DEVELOPER",
    ];
    const LEN = words.reduce((m, w) => Math.max(m, w.length), 0);
    const row = boardRef.current;
    if (!row) return;

    const pad = (w) => (w + " ".repeat(LEN)).slice(0, LEN);

    let current = pad(words[0]).split("");
    const chars = [];

    current.forEach((ch) => {
      const cell = document.createElement("div");
      cell.className = "flap-char" + (ch === " " ? " space" : "");
      const inner = document.createElement("div");
      inner.className = "flap-inner";
      inner.textContent = ch === " " ? "" : ch;
      cell.appendChild(inner);
      row.appendChild(cell);
      chars.push({ cell, inner });
    });

    const reduceMotion = window.matchMedia(
      "(prefers-reduced-motion: reduce)"
    ).matches;

    let idx = 0;
    if (!reduceMotion) {
      const interval = setInterval(() => {
        idx = (idx + 1) % words.length;
        const next = pad(words[idx]).split("");
        next.forEach((ch, i) => {
          const c = chars[i];
          const oldCh = c.inner.textContent;
          const newCh = ch === " " ? "" : ch;
          if (oldCh === newCh) return;
          c.cell.classList.toggle("space", ch === " ");
          if (reduceMotion) {
            c.inner.textContent = newCh;
            return;
          }
          c.cell.classList.add("flip");
          setTimeout(() => {
            c.inner.textContent = newCh;
          }, 280);
          setTimeout(() => {
            c.cell.classList.remove("flip");
          }, 600);
        });
      }, 2600);

      return () => clearInterval(interval);
    }
  }, []);

  const isDark = theme === "dark";
  const css = `
    :root {
      --ink: ${isDark ? "#F1F1F7" : "#14213D"};
      --ink-soft: ${isDark ? "#A0A0B0" : "#2A3557"};
      --paper: ${isDark ? "#0E0E17" : "#FBF7EF"};
      --paper-dim: ${isDark ? "#181826" : "#F1ECDF"};
      --marigold: ${isDark ? "#9C96F5" : "#F4A623"};
      --vermillion: ${isDark ? "#F87171" : "#E14F3D"};
      --emerald: ${isDark ? "#4ADE80" : "#1F6F5C"};
      --charcoal: ${isDark ? "#F1F1F7" : "#24211D"};
      --line: ${isDark ? "rgba(255,255,255,0.1)" : "rgba(20,33,61,0.16)"};
      --line-strong: ${isDark ? "rgba(255,255,255,0.2)" : "rgba(20,33,61,0.32)"};
      --nav-text: ${isDark ? "rgba(14,14,23,0.82)" : "rgba(251,247,239,0.82)"};
      --nav-border: ${isDark ? "rgba(14,14,23,0.12)" : "rgba(251,247,239,0.12)"};
      --nav-btn-border: ${isDark ? "rgba(14,14,23,0.4)" : "rgba(251,247,239,0.4)"};
      --hero-sub: ${isDark ? "rgba(14,14,23,0.78)" : "rgba(251,247,239,0.78)"};
      --hero-grid: ${isDark ? "rgba(14,14,23,0.035)" : "rgba(251,247,239,0.035)"};
      --board-bg: ${isDark ? "#24243A" : "#0E1730"};
      --board-border: ${isDark ? "rgba(255,255,255,0.14)" : "rgba(251,247,239,0.14)"};
      --board-label: ${isDark ? "rgba(255,255,255,0.5)" : "rgba(251,247,239,0.5)"};
      --ghost-text: ${isDark ? "rgba(14,14,23,0.65)" : "rgba(251,247,239,0.65)"};
      --ghost-border: ${isDark ? "rgba(14,14,23,0.4)" : "rgba(251,247,239,0.4)"};
      --footer-text: ${isDark ? "rgba(241,241,247,0.6)" : "rgba(251,247,239,0.6)"};
      --footer-muted: ${isDark ? "rgba(241,241,247,0.4)" : "rgba(251,247,239,0.4)"};
      --stub-shadow: ${isDark ? "rgba(0,0,0,0.25)" : "rgba(20,33,61,0.08)"};
      --btn-hover: ${isDark ? "rgba(156,150,245,0.08)" : "rgba(244,166,35,0.08)"};
      --btn-shadow: ${isDark ? "rgba(156,150,245,0.28)" : "rgba(244,166,35,0.28)"};
      --pulse-color: ${isDark ? "rgba(156,150,245,0.6)" : "rgba(244,166,35,0.6)"};
      --display: 'Bebas Neue', sans-serif;
      --body: 'IBM Plex Sans', sans-serif;
      --mono: 'IBM Plex Mono', monospace;
    }
    *{box-sizing:border-box;}
    html{scroll-behavior:smooth;}
    body{margin:0;background:var(--paper);color:var(--charcoal);font-family:var(--body);line-height:1.5;-webkit-font-smoothing:antialiased;}
    a{color:inherit;}
    :focus-visible{outline:3px solid var(--marigold);outline-offset:2px;}
    .lp-wrap{max-width:1180px;margin:0 auto;padding:0 28px;}
    header.lp-site{position:sticky;top:0;z-index:50;background:var(--ink);color:var(--paper);border-bottom:1px solid var(--nav-border);}
    nav.lp-wrap{display:flex;align-items:center;justify-content:space-between;height:68px;}
    .lp-logo{display:flex;align-items:center;gap:10px;font-family:var(--display);font-size:26px;letter-spacing:0.5px;text-decoration:none;color:var(--paper);cursor:pointer;}
    .lp-logo .stub{width:9px;height:22px;background:var(--marigold);border-radius:2px;transform:skewX(-12deg);}
    .lp-navlinks{display:flex;align-items:center;gap:34px;font-size:15px;}
    .lp-navlinks a{text-decoration:none;color:var(--nav-text);position:relative;padding:4px 0;transition:color .2s ease;cursor:pointer;}
    .lp-navlinks a:not(.btn-login):after{content:'';position:absolute;left:0;bottom:-2px;width:0;height:2px;background:var(--marigold);transition:width .25s ease;}
    .lp-navlinks a:not(.btn-login):hover{color:var(--paper);}
    .lp-navlinks a:not(.btn-login):hover:after{width:100%;}
    .btn-login{border:1px solid var(--nav-btn-border);padding:8px 18px;border-radius:3px;font-family:var(--mono);font-size:13px;letter-spacing:0.4px;transition:border-color .2s ease,background .2s ease;cursor:pointer;background:transparent;color:var(--nav-text);}
    .btn-login:hover{border-color:var(--marigold);background:var(--btn-hover);}
    @media (max-width:720px){.lp-navlinks{gap:16px;font-size:13px;}.lp-navlinks a:not(.btn-login){display:none;}}
    .lp-hero{background:var(--ink);color:var(--paper);padding:76px 0 88px;position:relative;overflow:hidden;}
    .lp-hero:before{content:'';position:absolute;inset:0;background-image:repeating-linear-gradient(90deg,var(--hero-grid) 0 1px,transparent 1px 64px);pointer-events:none;}
    .lp-hero .lp-wrap{position:relative;}
    .lp-eyebrow{font-family:var(--mono);font-size:12.5px;letter-spacing:2.5px;color:var(--marigold);text-transform:uppercase;display:flex;align-items:center;gap:10px;margin-bottom:22px;}
    .lp-eyebrow .dot{width:7px;height:7px;border-radius:50%;background:var(--marigold);box-shadow:0 0 0 0 var(--pulse-color);animation:pulse 2s infinite;}
    @keyframes pulse{0%{box-shadow:0 0 0 0 var(--pulse-color);}70%{box-shadow:0 0 0 8px rgba(0,0,0,0);}100%{box-shadow:0 0 0 0 rgba(0,0,0,0);}}
    h1.lp-headline{font-family:var(--display);font-size:clamp(44px,6.4vw,84px);line-height:0.98;letter-spacing:0.5px;margin:0 0 28px;max-width:820px;}
    h1.lp-headline .accent{color:var(--marigold);}
    .lp-sub{font-size:18px;color:var(--hero-sub);max-width:520px;margin:0 0 36px;}
    .lp-sub strong{color:var(--paper);font-weight:600;}
    .lp-hero-actions{display:flex;align-items:center;gap:22px;flex-wrap:wrap;margin-bottom:64px;}
    .ticket-btn{position:relative;display:inline-flex;align-items:center;gap:10px;background:var(--marigold);color:var(--ink);font-family:var(--body);font-weight:700;font-size:15.5px;padding:15px 26px 15px 22px;border-radius:4px;text-decoration:none;border:none;cursor:pointer;transition:transform .18s ease,box-shadow .18s ease;box-shadow:0 0 0 rgba(0,0,0,0);}
    .ticket-btn:before,.ticket-btn:after{content:'';position:absolute;top:50%;transform:translateY(-50%);width:14px;height:14px;background:var(--ink);border-radius:50%;}
    .ticket-btn:before{left:-7px;}
    .ticket-btn:after{right:-7px;}
    .ticket-btn:hover{transform:translateY(-2px);box-shadow:0 8px 18px var(--btn-shadow);}
    .link-ghost{font-family:var(--mono);font-size:13.5px;color:var(--ghost-text);text-decoration:none;border-bottom:1px dashed var(--ghost-border);padding-bottom:2px;cursor:pointer;}
    .link-ghost:hover{color:var(--paper);border-color:var(--paper);}
    .lp-board{background:var(--board-bg);border:1px solid var(--board-border);border-radius:6px;padding:20px 22px;max-width:640px;}
    .lp-board-label{font-family:var(--mono);font-size:11.5px;letter-spacing:2px;color:var(--board-label);text-transform:uppercase;margin-bottom:12px;display:flex;justify-content:space-between;}
    .lp-board-label span:last-child{color:var(--emerald);}
    .flap-row{display:flex;flex-wrap:wrap;gap:4px;}
    .flap-char{perspective:200px;width:22px;height:32px;background:var(--board-bg);border-radius:2px;display:flex;align-items:center;justify-content:center;font-family:var(--mono);font-weight:600;font-size:17px;color:var(--paper);box-shadow:inset 0 -1px 0 rgba(0,0,0,0.4),inset 0 1px 0 rgba(255,255,255,0.05);}
    @media (max-width:520px){.flap-char{width:16px;height:24px;font-size:12px;}}
    .flap-char.space{background:transparent;box-shadow:none;}
    .flap-inner{transform-style:preserve-3d;}
    .flap-char.flip .flap-inner{animation:flipChar 0.6s ease-in-out;}
    @keyframes flipChar{0%{transform:rotateX(0deg);}48%{transform:rotateX(-90deg);}52%{transform:rotateX(-90deg);}100%{transform:rotateX(0deg);}}
    @media (prefers-reduced-motion: reduce){.flap-char.flip .flap-inner{animation:none;}.lp-eyebrow .dot{animation:none;}}
    .lp-gates{background:var(--paper);padding:64px 0 56px;border-bottom:1px solid var(--line);}
    .lp-section-head{max-width:600px;margin-bottom:36px;}
    .lp-section-kicker{font-family:var(--mono);font-size:12px;letter-spacing:2px;color:var(--ink-soft);text-transform:uppercase;margin-bottom:10px;}
    .lp-section-kicker .n{color:var(--vermillion);}
    h2.lp-section-title{font-family:var(--display);font-size:clamp(30px,3.6vw,42px);letter-spacing:0.4px;margin:0;color:var(--ink);}
    .gate-strip{display:grid;grid-template-columns:repeat(4,1fr);gap:1px;background:var(--line);border:1px solid var(--line);border-radius:6px;overflow:hidden;}
    .lp-gate{background:var(--paper);padding:22px 20px;display:flex;flex-direction:column;gap:10px;}
    .gate-top{display:flex;align-items:center;justify-content:space-between;}
    .gate-num{font-family:var(--mono);font-size:12px;color:var(--ink-soft);}
    .gate-status{display:flex;align-items:center;gap:6px;font-family:var(--mono);font-size:10.5px;letter-spacing:1px;color:var(--emerald);text-transform:uppercase;}
    .gate-status .led{width:6px;height:6px;border-radius:50%;background:var(--emerald);animation:pulse2 2.4s infinite;}
    @keyframes pulse2{0%,100%{opacity:1;}50%{opacity:0.35;}}
    .gate-name{font-family:var(--body);font-weight:700;font-size:16.5px;color:var(--ink);}
    .gate-desc{font-size:13.5px;color:var(--ink-soft);line-height:1.5;}
    @media (max-width:820px){.gate-strip{grid-template-columns:repeat(2,1fr);}}
    @media (max-width:480px){.gate-strip{grid-template-columns:1fr;}}
    @media (prefers-reduced-motion: reduce){.gate-status .led{animation:none;}}
    .lp-features{padding:72px 0;background:var(--paper-dim);border-bottom:1px solid var(--line);}
    .feature-grid{display:grid;grid-template-columns:repeat(4,1fr);gap:20px;}
    .lp-stub{background:var(--paper);border:1px solid var(--line);border-radius:6px;padding:22px 20px 24px;position:relative;transition:transform .2s ease,box-shadow .2s ease,border-color .2s ease;}
    .lp-stub:hover{transform:translateY(-4px);box-shadow:0 12px 24px var(--stub-shadow);border-color:var(--line-strong);}
    .stub-perf{height:1px;margin:0 0 16px;background-image:repeating-linear-gradient(90deg,var(--line-strong) 0 6px,transparent 6px 12px);}
    .stub-class{font-family:var(--mono);font-size:10.5px;letter-spacing:1.4px;color:var(--vermillion);text-transform:uppercase;margin-bottom:12px;}
    .lp-stub h3{font-family:var(--body);font-size:17px;font-weight:700;margin:0 0 8px;color:var(--ink);}
    .lp-stub p{font-size:13.8px;color:var(--ink-soft);margin:0;line-height:1.55;}
    @media (max-width:940px){.feature-grid{grid-template-columns:repeat(2,1fr);}}
    @media (max-width:520px){.feature-grid{grid-template-columns:1fr;}}
    .lp-about{padding:76px 0;background:var(--paper);}
    .about-grid{display:grid;grid-template-columns:1.1fr 0.9fr;gap:56px;align-items:start;}
    .lp-about p.lead{font-size:19px;color:var(--charcoal);max-width:520px;margin:0 0 22px;line-height:1.6;}
    .lp-about p.body-text{font-size:15px;color:var(--ink-soft);max-width:520px;line-height:1.65;}
    .stat-plaque{border:1px solid var(--line);border-radius:6px;padding:26px 24px;background:var(--paper-dim);}
    .stat-row{display:flex;justify-content:space-between;align-items:baseline;padding:14px 0;border-bottom:1px dashed var(--line-strong);}
    .stat-row:last-child{border-bottom:none;}
    .stat-row .label{font-family:var(--mono);font-size:12.5px;color:var(--ink-soft);text-transform:uppercase;letter-spacing:0.6px;}
    .stat-row .value{font-family:var(--display);font-size:26px;color:var(--ink);letter-spacing:0.3px;}
    @media (max-width:820px){.about-grid{grid-template-columns:1fr;gap:32px;}}
    footer.lp-site{background:var(--ink);color:var(--footer-text);padding:28px 0;}
    footer .lp-wrap{display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:10px;}
    footer .fine{font-family:var(--mono);font-size:12px;}
    footer .fine.right{color:var(--footer-muted);}
  `;

  const scrollTo = (id) => {
    const el = document.getElementById(id);
    if (el) el.scrollIntoView({ behavior: "smooth" });
  };

  return (
    <>
      <style>{css}</style>
      <header className="lp-site">
        <nav className="lp-wrap">
          <a className="lp-logo">
            <span className="stub"></span>JobLelo
          </a>
          <div className="lp-navlinks">
            <a onClick={() => scrollTo("lp-gates")}>Platforms</a>
            <a onClick={() => scrollTo("lp-features")}>Features</a>
            <a onClick={() => scrollTo("lp-about")}>About</a>
            <button className="btn-login" onClick={toggleTheme} title="Toggle theme">
              {theme === "dark" ? (
                <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M12 3v2.25m6.364.386l-1.591 1.591M21 12h-2.25m-.386 6.364l-1.591-1.591M12 18.75V21m-4.773-4.227l-1.591 1.591M5.25 12H3m4.227-4.773L5.636 5.636M15.75 12a3.75 3.75 0 11-6 0 3.75 3.75 0 016 0z" />
                </svg>
              ) : (
                <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M21.752 15.002A9.718 9.718 0 0118 15.75c-5.385 0-9.75-4.365-9.75-9.75 0-1.33.266-2.597.748-3.752A9.753 9.753 0 003 11.25C3 16.635 7.365 21 12.75 21a9.753 9.753 0 009.002-5.998z" />
                </svg>
              )}
            </button>
            <button className="btn-login" onClick={() => navigate("/login")}>
              Login
            </button>
          </div>
        </nav>
      </header>

      <section className="lp-hero">
        <div className="lp-wrap">
          <div className="lp-eyebrow">
            <span className="dot"></span> Now Searching Every Source
          </div>
          <h1 className="lp-headline">
            One search.<br />Every platform<br />
            <span className="accent">arrives.</span>
          </h1>
          <p className="lp-sub">
            JobLelo runs your search across leading job platforms and your own
            inbox <strong>at the same time</strong> — then ranks what actually
            fits you,             so you check one place instead of many.
          </p>
          <div className="lp-hero-actions">
            <button className="ticket-btn" onClick={() => navigate("/login")}>
              Get Started →
            </button>
            <a className="link-ghost" onClick={() => scrollTo("lp-gates")}>
              see what gets searched ↓
            </a>
          </div>
          <div className="lp-board">
            <div className="lp-board-label">
              <span>Now Boarding</span>
              <span>● Live</span>
            </div>
            <div className="flap-row" ref={boardRef}></div>
          </div>
        </div>
      </section>

      <section className="lp-gates" id="lp-gates">
        <div className="lp-wrap">
          <div className="lp-section-head">
            <div className="lp-section-kicker">
              Every Source, One Boarding Pass
            </div>
            <h2 className="lp-section-title">Every gate, checked at once.</h2>
          </div>
          <div className="gate-strip">
            <div className="lp-gate">
              <div className="gate-top">
                <span className="gate-num">GATE 01</span>
                <span className="gate-status">
                  <span className="led"></span>Open
                </span>
              </div>
              <div className="gate-name">Open Listings</div>
              <div className="gate-desc">
                Broad postings across companies of every size, in one sweep.
              </div>
            </div>
            <div className="lp-gate">
              <div className="gate-top">
                <span className="gate-num">GATE 02</span>
                <span className="gate-status">
                  <span className="led"></span>Open
                </span>
              </div>
              <div className="gate-name">Aggregated Boards</div>
              <div className="gate-desc">
                Listings pulled together from boards you'd never think to check
                yourself.
              </div>
            </div>
            <div className="lp-gate">
              <div className="gate-top">
                <span className="gate-num">GATE 03</span>
                <span className="gate-status">
                  <span className="led"></span>Open
                </span>
              </div>
              <div className="gate-name">Remote-First</div>
              <div className="gate-desc">
                Remote roles, for when the office is wherever you are.
              </div>
            </div>
            <div className="lp-gate">
              <div className="gate-top">
                <span className="gate-num">GATE 04</span>
                <span className="gate-status">
                  <span className="led"></span>Open
                </span>
              </div>
              <div className="gate-name">Your Inbox</div>
              <div className="gate-desc">
                Alerts already sitting in your email, pulled in without
                forwarding a thing.
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="lp-features" id="lp-features">
        <div className="lp-wrap">
          <div className="lp-section-head">
            <div className="lp-section-kicker">What You Get</div>
            <h2 className="lp-section-title">Smarter than a search bar.</h2>
          </div>
          <div className="feature-grid">
            <div className="lp-stub">
              <div className="stub-perf"></div>
              <div className="stub-class">Class: AI Match</div>
              <h3>AI-Powered Matching</h3>
              <p>
                Results ranked by how well they fit your skills and preferences,
                not just which keywords line up.
              </p>
            </div>
            <div className="lp-stub">
              <div className="stub-perf"></div>
              <div className="stub-class">Class: Inbox Scan</div>
              <h3>Email Job Detection</h3>
              <p>
                Job alerts already sitting in your inbox get pulled into your
                search automatically.
              </p>
            </div>
            <div className="lp-stub">
              <div className="stub-perf"></div>
              <div className="stub-class">Class: Tracking</div>
              <h3>Job Saved &amp; Tracking</h3>
              <p>
                Save postings you like and track where each one stands, in one
                place instead of ten browser tabs.
              </p>
            </div>
            <div className="lp-stub">
              <div className="stub-perf"></div>
              <div className="stub-class">Class: Ranking</div>
              <h3>Smart Ranking</h3>
              <p>
                Every result sorted by how well it matches you, not by how
                recently it was posted.
              </p>
            </div>
          </div>
        </div>
      </section>

      <section className="lp-about" id="lp-about">
        <div className="lp-wrap about-grid">
          <div>
            <p className="lead">
              JobLelo searches every major job platform at once, so you check
              one place instead of five.
            </p>
            <p className="body-text">
              Most job hunts mean the same search typed into tab after tab,
              and an inbox full of alerts you never open. JobLelo runs that
              search once, pulls from every gate, and hands you back a single
              ranked list — built around what you're actually looking for, not
              what each platform thinks you should see.
            </p>
            <div
              className="lp-hero-actions"
              style={{ marginTop: 28, marginBottom: 0 }}
            >
              <button
                className="ticket-btn"
                style={{ background: "var(--ink)", color: "var(--paper)" }}
                onClick={() => navigate("/login")}
              >
                Get Started →
              </button>
            </div>
          </div>
          <div className="stat-plaque" style={{ borderColor: "var(--accent)" }}>
            <div className="stat-row">
              <span className="label">Contact</span>
              <span className="value" style={{ fontSize: 13, fontFamily: "var(--mono)", letterSpacing: "0.2px", color: "var(--accent)" }}>bharatmarwah4@gmail.com</span>
            </div>
            <div className="stat-row">
              <span className="label">License</span>
              <span className="value" style={{ fontSize: 18 }}>OPEN</span>
            </div>
            <div className="stat-row" style={{ borderBottom: "none", paddingBottom: 0 }}>
              <span className="label">Source</span>
              <span className="value" style={{ fontSize: 18 }}>
                <a
                  href="https://github.com/Bharatmarwah/JobLeLo-Ai.git"
                  target="_blank"
                  rel="noopener noreferrer"
                  style={{ color: "var(--accent)", textDecoration: "none", fontSize: 13 }}
                  onMouseEnter={(e) => { e.currentTarget.style.textDecoration = "underline"; }}
                  onMouseLeave={(e) => { e.currentTarget.style.textDecoration = "none"; }}
                >
                  GitHub ↗
                </a>
              </span>
            </div>
          </div>
        </div>
      </section>

      <footer className="lp-site">
        <div className="lp-wrap">
          <span className="fine">© 2026 JobLelo. Open source.</span>
          <span className="fine right">
            <a
              href="https://github.com/Bharatmarwah/JobLeLo-Ai.git"
              target="_blank"
              rel="noopener noreferrer"
              style={{ color: "var(--marigold)", textDecoration: "none" }}
              onMouseEnter={(e) => { e.currentTarget.style.textDecoration = "underline"; }}
              onMouseLeave={(e) => { e.currentTarget.style.textDecoration = "none"; }}
            >
              GitHub
            </a>
          </span>
        </div>
      </footer>
    </>
  );
}