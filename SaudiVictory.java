import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.*;

public class VictoryLight extends JPanel {
    float alpha = 0f;        // مستوى اللمعان
    boolean toSaudi = false; // متى تتغير الكلمة
    Timer timer;

    public VictoryLight() {
        setBackground(Color.black);

        // مؤقت يحرك الوميض والانتقال بين الكلمتين
        timer = new Timer(60, e -> {
            alpha += 0.03f;
            if (alpha >= 1f) {
                alpha = 0f;
                toSaudi = !toSaudi;
                playTone();
            }
            repaint();
        });
        timer.start();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        String text = toSaudi ? "سعودية العزّ 🇸🇦" : "نصر";
        int fontSize = toSaudi ? 60 : 100;
        g2.setFont(new Font("Arial", Font.BOLD, fontSize));

        // تأثير الوميض الأخضر
        float glow = Math.abs((float)Math.sin(alpha * Math.PI));
        Color glowColor = new Color(0f, 1f, 0f, glow);

        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = (getHeight() / 2) + fm.getAscent() / 4;

        g2.setColor(glowColor);
        g2.drawString(text, x, y);

        // النص الثابت الصغير في الأسفل
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.setColor(new Color(0, 255, 0, 180));
        String sub = "مبروك لمنتخبنا، رفعتوا الراس 💚";
        int sx = (getWidth() - g2.getFontMetrics().stringWidth(sub)) / 2;
        g2.drawString(sub, sx, getHeight() - 40);
    }

    // نغمة بسيطة (ضربة نصر)
    void playTone() {
        new Thread(() -> {
            try {
                float sr = 44100;
                byte[] buf = new byte[(int)(sr * 0.3)];
                for (int i = 0; i < buf.length / 2; i++) {
                    double angle = i * 2.0 * Math.PI * 880 / sr;
                    short val = (short) (Math.sin(angle) * 32767 * 0.4);
                    buf[2*i] = (byte) (val & 0xff);
                    buf[2*i+1] = (byte) ((val >> 8) & 0xff);
                }
                AudioFormat af = new AudioFormat(sr, 16, 1, true, false);
                try (SourceDataLine line = AudioSystem.getSourceDataLine(af)) {
                    line.open(af);
                    line.start();
                    line.write(buf, 0, buf.length);
                    line.drain();
                    line.stop();
                }
            } catch (Exception ignored) {}
        }).start();
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("ضوء النصر 🇸🇦");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(800, 500);
        f.setLocationRelativeTo(null);
        f.add(new VictoryLight());
        f.setVisible(true);
    }
}