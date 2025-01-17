package myproject;

import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class ProximitySensor {
    private static int currentDistance = 0; // Sensör verisi

    public static void main(String[] args) {
        // Seri portu seç
        SerialPort serialPort = SerialPort.getCommPort("COM6"); // Arduino'nun bağlı olduğu port
        serialPort.setComPortParameters(9600, 8, 1, 0); // Baud hızı ve diğer seri port ayarları
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);

        if (!serialPort.openPort()) {
            System.out.println("Seri port açılamadı!");
            return;
        }

        // Bar grafik paneli
        BarGraphPanel graphPanel = new BarGraphPanel();

        // GUI oluştur
        JFrame frame = new JFrame("Proximity Sensor - Bar Grafik");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLayout(new BorderLayout());
        frame.add(graphPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        // Seri porttan veri okuma ve grafiği güncelleme
        new Thread(() -> {
            try (Scanner scanner = new Scanner(serialPort.getInputStream())) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    try {
                        currentDistance = Integer.parseInt(line); // Mesafeyi oku
                        SwingUtilities.invokeLater(graphPanel::repaint); // Grafiği güncelle
                    } catch (NumberFormatException e) {
                        System.out.println("Geçersiz veri: " + line);
                    }
                }
            }
        }).start();
    }

    // Bar grafiği çizmek için özel JPanel
    static class BarGraphPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Arka plan rengi
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Çubuğun yüksekliği
            int barHeight = (int) ((getHeight() - 50) * (currentDistance / 100.0)); // Yüksekliği %100'e göre ayarla
            int barWidth = getWidth() / 3;

            // Çubuğun rengi belirle
            Color barColor;
            if (currentDistance >= 0 && currentDistance <= 20) {
                barColor = Color.RED; // 0-20 cm kırmızı
            } else if (currentDistance > 20 && currentDistance <= 40) {
                barColor = Color.ORANGE; // 20-40 cm turuncu
            } else if (currentDistance > 40 && currentDistance <= 60) {
                barColor = Color.YELLOW; // 40-60 cm sarı
            } else {
                barColor = Color.GREEN; // 60 cm'den sonrası yeşil
            }

            // Çubuğu çiz
            g2d.setColor(barColor);
            g2d.fillRect((getWidth() - barWidth) / 2, getHeight() - barHeight - 30, barWidth, barHeight);

            // Çubuğun üstündeki değer
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString(currentDistance + " cm", (getWidth() - barWidth) / 2 + barWidth / 4, getHeight() - barHeight - 35);

            // Y ekseni etiketleri
            g2d.drawString("0 cm", 20, getHeight() - 30);
            g2d.drawString("100 cm", 20, 20);
        }
    }
}
