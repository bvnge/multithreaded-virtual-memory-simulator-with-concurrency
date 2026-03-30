import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LineGraphPanel extends JPanel {
    private Map<String, List<Double>> seriesData = new LinkedHashMap<>();
    private List<String> xAxisLabels = new ArrayList<>();
    private String title = "Comparison Graph";
    private String yAxisLabel = "Value";
    private String xAxisLabel = "Algorithm";

    public LineGraphPanel() {
        setBackground(Color.WHITE);
    }

    public void setGraphData(Map<String, Double> singlePointData, String title, String yAxisLabel) {
        this.seriesData.clear();
        this.xAxisLabels.clear();
        this.title = title;
        this.yAxisLabel = yAxisLabel;

        int index = 0;
        for (Map.Entry<String, Double> entry : singlePointData.entrySet()) {
            List<Double> points = new ArrayList<>();
            points.add(entry.getValue());
            seriesData.put(entry.getKey(), points);
            xAxisLabels.add(entry.getKey());
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (seriesData.isEmpty()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 80;
        int labelPadding = 40;
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 2 * padding;

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (width - titleWidth) / 2, 30);

        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(padding, height - padding, width - padding, height - padding);
        g2.drawLine(padding, padding, padding, height - padding);

        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString(yAxisLabel, 10, padding - 10);
        int xLabelWidth = g2.getFontMetrics().stringWidth(xAxisLabel);
        g2.drawString(xAxisLabel, (width - xLabelWidth) / 2, height - 10);

        double maxValue = 1;
        for (List<Double> points : seriesData.values()) {
            for (Double value : points) {
                maxValue = Math.max(maxValue, value);
            }
        }

        maxValue = Math.ceil(maxValue * 1.1);

        int numYTicks = 5;
        for (int i = 0; i <= numYTicks; i++) {
            int y = height - padding - (i * chartHeight / numYTicks);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(padding, y, width - padding, y);

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            String label = String.format("%.1f", (maxValue * i / numYTicks));
            int labelWidth = g2.getFontMetrics().stringWidth(label);
            g2.drawString(label, padding - labelWidth - 8, y + 4);
        }

        Color[] colors = {
            new Color(52, 152, 219),
            new Color(46, 204, 113),
            new Color(230, 126, 34),
            new Color(231, 76, 60),
            new Color(155, 89, 182),
            new Color(26, 188, 156)
        };

        int colorIndex = 0;
        List<String> algorithmNames = new ArrayList<>(seriesData.keySet());

        if (algorithmNames.size() > 1) {
            int pointSpacing = chartWidth / (algorithmNames.size() - 1);

            for (int i = 0; i < algorithmNames.size(); i++) {
                String algorithm = algorithmNames.get(i);
                List<Double> points = seriesData.get(algorithm);

                if (points.isEmpty()) continue;

                Color lineColor = colors[colorIndex % colors.length];
                g2.setColor(lineColor);
                g2.setStroke(new BasicStroke(3f));

                int x = padding + (i * pointSpacing);
                double value = points.get(0);
                int y = height - padding - (int) ((value / maxValue) * chartHeight);

                if (i > 0) {
                    String prevAlgorithm = algorithmNames.get(i - 1);
                    List<Double> prevPoints = seriesData.get(prevAlgorithm);
                    if (!prevPoints.isEmpty()) {
                        int prevX = padding + ((i - 1) * pointSpacing);
                        int prevY = height - padding - (int) ((prevPoints.get(0) / maxValue) * chartHeight);
                        g2.drawLine(prevX, prevY, x, y);
                    }
                }

                g2.fill(new Ellipse2D.Double(x - 6, y - 6, 12, 12));

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                String valueLabel = String.format("%.2f", value);
                int valueLabelWidth = g2.getFontMetrics().stringWidth(valueLabel);
                g2.drawString(valueLabel, x - valueLabelWidth / 2, y - 12);

                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                int algoLabelWidth = g2.getFontMetrics().stringWidth(algorithm);
                g2.drawString(algorithm, x - algoLabelWidth / 2, height - padding + 20);

                colorIndex++;
            }
        } else if (algorithmNames.size() == 1) {
            String algorithm = algorithmNames.get(0);
            List<Double> points = seriesData.get(algorithm);

            if (!points.isEmpty()) {
                Color lineColor = colors[0];
                g2.setColor(lineColor);

                int x = padding + chartWidth / 2;
                double value = points.get(0);
                int y = height - padding - (int) ((value / maxValue) * chartHeight);

                g2.fill(new Ellipse2D.Double(x - 8, y - 8, 16, 16));

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                String valueLabel = String.format("%.2f", value);
                int valueLabelWidth = g2.getFontMetrics().stringWidth(valueLabel);
                g2.drawString(valueLabel, x - valueLabelWidth / 2, y - 15);

                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                int algoLabelWidth = g2.getFontMetrics().stringWidth(algorithm);
                g2.drawString(algorithm, x - algoLabelWidth / 2, height - padding + 20);
            }
        }

        int legendX = width - padding - 150;
        int legendY = padding + 20;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));

        colorIndex = 0;
        for (String algorithm : algorithmNames) {
            Color lineColor = colors[colorIndex % colors.length];
            g2.setColor(lineColor);
            g2.fillRect(legendX, legendY + (colorIndex * 20), 12, 12);

            g2.setColor(Color.BLACK);
            g2.drawString(algorithm, legendX + 18, legendY + (colorIndex * 20) + 10);
            colorIndex++;
        }
    }
}
