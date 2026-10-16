package utils;

import org.openqa.selenium.*;

public final class Visual {
    private static final boolean ON = Boolean.parseBoolean(System.getProperty("visual", "false"));
    private Visual() {}

    public static void hud(WebDriver d, String scenario, String step) {
        if (!ON || d == null) return;
        try {
            ((JavascriptExecutor) d).executeScript(
                    "let b=document.getElementById('qa-hud');" +
                            "if(!b){b=document.createElement('div');b.id='qa-hud';" +
                            "b.style.cssText='position:fixed;top:8px;right:8px;z-index:2147483647;pointer-events:none;" +
                            "white-space:pre-line;background:linear-gradient(135deg,#1f2a5a,#5b2a86);color:#fff;" +
                            "font:600 13px Segoe UI,Arial;padding:8px 14px;border-radius:10px;" +
                            "box-shadow:0 4px 14px rgba(0,0,0,.35);max-width:420px';document.body.appendChild(b);}" +
                            "b.innerText=arguments[0]+'\\n▶ '+arguments[1];", scenario, step);
        } catch (Exception ignored) { }
    }

    /** Green flash on the element about to be used. */
    public static void highlight(WebDriver d, WebElement el) {
        if (!ON) return;
        try {
            ((JavascriptExecutor) d).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});" +
                            "arguments[0].style.outline='3px solid #22c55e';" +
                            "arguments[0].style.outlineOffset='2px';" +
                            "arguments[0].style.boxShadow='0 0 12px #22c55e';", el);
        } catch (Exception ignored) { }
    }
}