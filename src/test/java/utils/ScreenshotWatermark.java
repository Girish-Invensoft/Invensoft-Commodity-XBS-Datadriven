package utils;

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Stamps the Invensoft logo + a small text watermark in the bottom-right corner of a PNG screenshot.
 * Works fully in memory (byte[] in, byte[] out), so it adds no file I/O or waits.
 * The logo is read from src/test/resources/invensoft-logo.png (classpath root) once and cached.
 * If the logo is missing, the text-only watermark is used.
 */
public final class ScreenshotWatermark {

    private static final String TEXT = "XBS Automation \u1D33\u1D37 | \u00A9 2026";   // ᴳᴷ
    private static final String FALLBACK_TEXT = "XBS Automation GK | \u00A9 2026";   // if font lacks superscripts
    private static final String LOGO_PATH = "/invensoft-logo.png";

    // Change these two if the logo is dark and gets lost on the dark pill
    private static final Color PILL_COLOR = Color.BLACK;
    private static final Color TEXT_COLOR = Color.WHITE;

    private static final int MARGIN = 10;
    private static final int PADDING = 6;
    private static final int GAP = 8;   // space between logo and text

    private static final BufferedImage LOGO = loadLogo();

    private ScreenshotWatermark() {
    }

    private static BufferedImage loadLogo() {
        try (InputStream in = ScreenshotWatermark.class.getResourceAsStream(LOGO_PATH)) {
            return in == null ? null : ImageIO.read(in);
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    public static byte[] apply(byte[] pngBytes) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(pngBytes));
            if (image == null) {
                return pngBytes;
            }

            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            int fontSize = Math.max(12, image.getWidth() / 90);
            Font font = new Font("SansSerif", Font.BOLD, fontSize);
            g.setFont(font);

            String text = font.canDisplayUpTo(TEXT) == -1 ? TEXT : FALLBACK_TEXT;

            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int contentHeight = (int) Math.round(fm.getHeight() * 1.4);

            // Scale the logo to the content height, keeping its aspect ratio
            int logoWidth = 0;
            int logoHeight = 0;
            if (LOGO != null && LOGO.getHeight() > 0) {
                logoHeight = contentHeight;
                logoWidth = (int) Math.round((double) LOGO.getWidth() * logoHeight / LOGO.getHeight());
            }

            int contentWidth = (logoWidth > 0 ? logoWidth + GAP : 0) + textWidth;
            int boxWidth = contentWidth + PADDING * 2;
            int boxHeight = contentHeight + PADDING * 2;
            int boxX = image.getWidth() - boxWidth - MARGIN;
            int boxY = image.getHeight() - boxHeight - MARGIN;

            // Semi-transparent pill so the watermark is readable on any background
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.55f));
            g.setColor(PILL_COLOR);
            g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 10, 10);

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.95f));

            int cursorX = boxX + PADDING;
            if (logoWidth > 0) {
                g.drawImage(LOGO, cursorX, boxY + PADDING, logoWidth, logoHeight, null);
                cursorX += logoWidth + GAP;
            }

            g.setColor(TEXT_COLOR);
            int textY = boxY + PADDING + (contentHeight - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(text, cursorX, textY);

            g.dispose();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);
            return out.toByteArray();
        } catch (IOException | RuntimeException e) {
            // Never let watermarking break the test or hide the original screenshot
            return pngBytes;
        }
    }
}