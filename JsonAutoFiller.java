import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JSON Auto Filler - Professional Desktop Edition
 * A high-performance, dark-themed workspace for intelligent JSON data population.
 */
public class JsonAutoFiller extends JFrame {

    // Theme Colors (Matches Website)
    private static final Color BG_MAIN = new Color(15, 23, 42);      // Deep Navy
    private static final Color BG_CARD = new Color(30, 41, 59);      // Charcoal
    private static final Color ACCENT_CYAN = new Color(34, 211, 238); // Neon Cyan
    private static final Color TEXT_MAIN = new Color(249, 250, 251); // Soft White
    private static final Color TEXT_MUTED = new Color(209, 213, 219); // Cool Gray
    private static final Color BORDER_GLOW = new Color(34, 211, 238, 80);

    private JTextArea inputArea;
    private JTextArea outputArea;
    private JCheckBox overwriteCheckbox;

    public JsonAutoFiller() {
        setTitle("JSON Auto Fill Generator");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_MAIN);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));

        // --- Header Section ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_MAIN);
        header.setBorder(new EmptyBorder(25, 30, 15, 30));

        JLabel titleLabel = new JLabel("JSON Auto Fill Generator");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(ACCENT_CYAN);
        header.add(titleLabel, BorderLayout.WEST);

        // Control Panel in Header
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        controls.setOpaque(false);

        overwriteCheckbox = new JCheckBox("Overwrite All");
        overwriteCheckbox.setOpaque(false);
        overwriteCheckbox.setForeground(TEXT_MUTED);
        overwriteCheckbox.setFocusPainted(false);
        
        ModernButton fillBtn = new ModernButton("Auto Fill Fields", true);
        fillBtn.addActionListener(e -> handleFill());

        ModernButton clearBtn = new ModernButton("Clear", false);
        clearBtn.addActionListener(e -> {
            inputArea.setText("");
            outputArea.setText("");
        });

        controls.add(overwriteCheckbox);
        controls.add(clearBtn);
        controls.add(fillBtn);
        header.add(controls, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // --- Main Content (Dual Pane) ---
        JPanel workspace = new JPanel(new GridLayout(1, 2, 20, 0));
        workspace.setBackground(BG_MAIN);
        workspace.setBorder(new EmptyBorder(0, 30, 30, 30));

        inputArea = createStyledTextArea("Input JSON Structure...");
        outputArea = createStyledTextArea("Filled Output Result...");
        outputArea.setEditable(false);

        workspace.add(createLabeledPanel("SOURCE SCHEMA", inputArea));
        workspace.add(createLabeledPanel("GENERATED DATA", outputArea));

        add(workspace, BorderLayout.CENTER);

        // --- Footer Section ---
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG_MAIN);
        footer.setBorder(new EmptyBorder(10, 30, 15, 30));

        ModernButton copyBtn = new ModernButton("Copy to Clipboard", false);
        copyBtn.addActionListener(e -> {
            if (!outputArea.getText().isEmpty()) {
                outputArea.selectAll();
                outputArea.copy();
                showCustomDialog("Success", "Copied to clipboard!");
            }
        });
        footer.add(copyBtn, BorderLayout.EAST);
        
        JLabel status = new JLabel("Status: System Ready");
        status.setForeground(TEXT_MUTED);
        status.setFont(new Font("SansSerif", Font.PLAIN, 12));
        footer.add(status, BorderLayout.WEST);

        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createLabeledPanel(String label, JTextArea area) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(ACCENT_CYAN);
        p.add(l, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(new LineBorder(BORDER_GLOW, 1, true));
        scroll.getViewport().setBackground(BG_CARD);
        p.add(scroll, BorderLayout.CENTER);
        
        return p;
    }

    private JTextArea createStyledTextArea(String placeholder) {
        JTextArea area = new JTextArea();
        area.setBackground(BG_CARD);
        area.setForeground(TEXT_MAIN);
        area.setCaretColor(ACCENT_CYAN);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setMargin(new Insets(15, 15, 15, 15));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private void handleFill() {
        String json = inputArea.getText().trim();
        if (json.isEmpty()) return;

        try {
            String result = processJson(json, overwriteCheckbox.isSelected());
            outputArea.setText(result);
        } catch (Exception ex) {
            showCustomDialog("Parsing Error", "Invalid JSON structure detected.");
        }
    }

    private String processJson(String json, boolean overwrite) {
        String patternString = overwrite ? "\"(\\w+)\"\\s*:\\s*(?:\"[^\"]*\"|null|\\d+|true|false)" 
                                        : "\"(\\w+)\"\\s*:\\s*(?:\"\"|null)";
        
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(json);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = "\"" + key + "\": " + formatValue(generateMockData(key));
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String formatValue(Object val) {
        if (val instanceof String) return "\"" + val + "\"";
        return String.valueOf(val);
    }

    private Object generateMockData(String key) {
        String k = key.toLowerCase();
        if (k.contains("limit")) return (int)(Math.random()*100) + "/" + (int)(Math.random()*100 + 100);
        if (k.contains("email")) return "dev_" + (int)(Math.random()*1000) + "@test-env.io";
        if (k.contains("name")) return "Alexander Pierce";
        if (k.contains("id") || k.contains("uuid")) return UUID.randomUUID().toString();
        if (k.contains("active") || k.contains("status")) return true;
        if (k.contains("count") || k.contains("amount")) return (int)(Math.random() * 999);
        if (k.contains("city")) return "Silicon Valley";
        return "Auto-Generated Content";
    }

    private void showCustomDialog(String title, String msg) {
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // --- Custom UI Components ---

    class ModernButton extends JButton {
        private boolean primary;
        private boolean hovering = false;

        public ModernButton(String text, boolean primary) {
            super(text);
            this.primary = primary;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(primary ? BG_MAIN : ACCENT_CYAN);
            setFont(new Font("SansSerif", Font.BOLD, 13));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hovering = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (primary) {
                g2.setColor(hovering ? ACCENT_CYAN.brighter() : ACCENT_CYAN);
            } else {
                g2.setColor(new Color(34, 211, 238, hovering ? 40 : 20));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 12, 12));
            }
            
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new JsonAutoFiller().setVisible(true);
        });
    }
}
