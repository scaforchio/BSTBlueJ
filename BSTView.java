 

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class BSTView extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener {
    Boolean tabella = false;
    final FinestraBT fbt;
    ArrayList<Riga> BSTTab;
    ArrayList<NodoGrafico> ElencoNodi;
    ArrayList<Arco> ElencoArchi;
    JTable table;
    JScrollPane scrollPane;
    JPanel tablePanel;
    JLabel zoomLabel;
    double zoomFactor = 1;
    boolean dragger;
    double xOffset = 0;
    double yOffset = 0;
    int xDiff;
    int yDiff;
    Point startPoint;

    float tableFontSize = 14f;
    static final float TABLE_FONT_MIN = 8f;
    static final float TABLE_FONT_MAX = 36f;

    static final Color BG_CANVAS      = Color.white;
    static final Color NODE_DEFAULT_FILL  = new Color(245, 247, 255);
    static final Color NODE_DEFAULT_BORDER = new Color(100, 130, 200);
    static final Color NODE_DEFAULT_TEXT   = new Color(30, 40, 80);
    static final Color EDGE_COLOR          = new Color(130, 150, 190);

    static final Color TBL_BG          = Color.white;
    static final Color TBL_HEADER_BG   = new Color(235, 240, 255);
    static final Color TBL_ROW_ODD     = Color.white;
    static final Color TBL_ROW_EVEN    = new Color(245, 248, 255);
    static final Color TBL_GRID        = new Color(210, 215, 230);
    static final Color TBL_FG          = new Color(40, 50, 80);
    static final Color TBL_HEADER_FG   = new Color(60, 80, 160);
    static final Color TBL_SELECT_BG   = new Color(180, 200, 255);
    static final Color TBL_SELECT_FG   = new Color(20, 30, 80);
    static final Color ZOOMBAR_BG      = new Color(248, 249, 255);
    static final Color ZOOMBAR_BORDER  = new Color(210, 215, 235);

    public BSTView(ArrayList<Riga> BSTTab, ArrayList<NodoGrafico> ng, ArrayList<Arco> ar, FinestraBT fbt) {
        this.BSTTab = BSTTab;
        this.ElencoArchi = ar;
        this.ElencoNodi = ng;
        this.fbt = fbt;
        setLayout(new BorderLayout());
        setBackground(BG_CANVAS);

        table = new JTable(new ModelloBSTTab(BSTTab));
        styleTable();

        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(TBL_GRID, 1));
        scrollPane.getViewport().setBackground(TBL_BG);

     
        JPanel zoomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        zoomBar.setBackground(ZOOMBAR_BG);
        zoomBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ZOOMBAR_BORDER));

        JLabel lblZoom = new JLabel("Table Zoom:");
        lblZoom.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblZoom.setForeground(new Color(100, 110, 150));

        JButton btnZoomOut   = makeZoomBtn("−");
        JButton btnZoomIn    = makeZoomBtn("+");
        JButton btnZoomReset = makeZoomBtn("100%");

        zoomLabel = new JLabel("100%");
        zoomLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        zoomLabel.setForeground(new Color(80, 100, 170));
        zoomLabel.setPreferredSize(new Dimension(44, 20));
        zoomLabel.setHorizontalAlignment(SwingConstants.CENTER);

        btnZoomIn.addActionListener(e    -> { tableFontSize = Math.min(tableFontSize + 2, TABLE_FONT_MAX); applyTableZoom(); });
        btnZoomOut.addActionListener(e   -> { tableFontSize = Math.max(tableFontSize - 2, TABLE_FONT_MIN); applyTableZoom(); });
        btnZoomReset.addActionListener(e -> { tableFontSize = 14f; applyTableZoom(); });

        zoomBar.add(lblZoom);
        zoomBar.add(btnZoomOut);
        zoomBar.add(zoomLabel);
        zoomBar.add(btnZoomIn);
        zoomBar.add(btnZoomReset);

        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.white);
        tablePanel.add(zoomBar, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.setVisible(false);
        add(tablePanel, BorderLayout.CENTER);

        addMouseWheelListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        // Ctrl+ / Ctrl- zoom globale (funziona anche quando il focus è su altri componenti)
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() != KeyEvent.KEY_PRESSED) return false;
            if (!e.isControlDown()) return false;
            int code = e.getKeyCode();
            char ch  = e.getKeyChar();
            boolean zoomIn  = (code == KeyEvent.VK_EQUALS || code == KeyEvent.VK_ADD
                                || code == KeyEvent.VK_PLUS || ch == '+' || ch == '=');
            boolean zoomOut = (code == KeyEvent.VK_MINUS || code == KeyEvent.VK_SUBTRACT || ch == '-');
            if (!zoomIn && !zoomOut) return false;
            if (tabella) {
                if (zoomIn)  { tableFontSize = Math.min(tableFontSize + 2, TABLE_FONT_MAX); applyTableZoom(); }
                else         { tableFontSize = Math.max(tableFontSize - 2, TABLE_FONT_MIN); applyTableZoom(); }
            } else {
                // zoom centrato al centro del pannello
                double cx = getWidth()  / 2.0;
                double cy = getHeight() / 2.0;
                double oldZoom = zoomFactor;
                if (zoomIn) zoomFactor *= 1.15;
                else        zoomFactor /= 1.15;
                xOffset = cx - (cx - xOffset) * (zoomFactor / oldZoom);
                yOffset = cy - (cy - yOffset) * (zoomFactor / oldZoom);
                repaint();
            }
            e.consume();
            return true;
        });
    }

    private void styleTable() {
        table.setBackground(TBL_ROW_ODD);
        table.setForeground(TBL_FG);
        table.setGridColor(TBL_GRID);
        table.setFont(new Font("Segoe UI", Font.PLAIN, (int) tableFontSize));
        table.setRowHeight((int)(tableFontSize * 2.2f));
        table.setSelectionBackground(TBL_SELECT_BG);
        table.setSelectionForeground(TBL_SELECT_FG);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setFillsViewportHeight(true);
        table.setBorder(null);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                if (sel) {
                    setBackground(TBL_SELECT_BG);
                    setForeground(TBL_SELECT_FG);
                } else {
                    setBackground(row % 2 == 0 ? TBL_ROW_EVEN : TBL_ROW_ODD);
                    setForeground(col == 1 ? new Color(50, 80, 180) : TBL_FG);
                }
                setFont(new Font("Segoe UI", Font.PLAIN, (int) tableFontSize));
                setHorizontalAlignment(col == 0 ? CENTER : LEFT);
                setBorder(new EmptyBorder(2, 8, 2, 8));
                return this;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setBackground(TBL_HEADER_BG);
        header.setForeground(TBL_HEADER_FG);
        header.setFont(new Font("Segoe UI", Font.BOLD, (int) tableFontSize));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(160, 180, 230)));
        header.setReorderingAllowed(false);

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
    }

    private void applyTableZoom() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, (int) tableFontSize));
        table.setRowHeight((int)(tableFontSize * 2.2f));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, (int) tableFontSize));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                if (sel) { setBackground(TBL_SELECT_BG); setForeground(TBL_SELECT_FG); }
                else { setBackground(row % 2 == 0 ? TBL_ROW_EVEN : TBL_ROW_ODD); setForeground(col == 1 ? new Color(50, 80, 180) : TBL_FG); }
                setFont(new Font("Segoe UI", Font.PLAIN, (int) tableFontSize));
                setHorizontalAlignment(col == 0 ? CENTER : LEFT);
                setBorder(new EmptyBorder(2, 8, 2, 8));
                return this;
            }
        });
        int pct = Math.round((tableFontSize / 14f) * 100);
        zoomLabel.setText(pct + "%");
        table.revalidate(); table.repaint();
    }

    private JButton makeZoomBtn(String txt) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(new Color(70, 90, 160));
        b.setBackground(Color.white);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(190, 200, 230), 1),
            new EmptyBorder(2, 10, 2, 10)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(new Color(230, 235, 255)); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(Color.white); }
        });
        return b;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (tabella) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(Color.white);
        g2.fillRect(0, 0, getWidth(), getHeight());

    
        double tx = xOffset + (dragger ? xDiff : 0);
        double ty = yOffset + (dragger ? yDiff : 0);

        AffineTransform at = new AffineTransform();
        at.translate(tx, ty);
        at.scale(zoomFactor, zoomFactor);
        g2.transform(at);

        disegnaArchi(g2);
        disegnaNodi(g2);
    }

   
    private Point2D screenToWorld(int sx, int sy) {
        double wx = (sx - xOffset) / zoomFactor;
        double wy = (sy - yOffset) / zoomFactor;
        return new Point2D.Double(wx, wy);
    }

    public void ridisegna(boolean tabella) {
        this.tabella = tabella;
        if (tabella) {
            tablePanel.setVisible(true);
            table.setModel(new ModelloBSTTab(BSTTab));
            styleTable();
            table.revalidate(); table.repaint();
        } else {
            tablePanel.setVisible(false);
        }
        repaint();
    }

    public void disegnaNodi(Graphics2D g2) {
        for (NodoGrafico n : ElencoNodi) {
            int lx = n.getX() - n.getLarghezza() / 2;
            int ly = n.getY() - n.getAltezza() / 2;
            int lw = n.getLarghezza();
            int lh = n.getAltezza();

            Color fill, border, textCol;
            if (n.getColore() == Color.white || n.getColore().equals(Color.white)) {
                fill    = NODE_DEFAULT_FILL;
                border  = NODE_DEFAULT_BORDER;
                textCol = NODE_DEFAULT_TEXT;
            } else if (n.getColore().equals(Color.green)) {
                fill    = new Color(210, 245, 220);
                border  = new Color(50, 170, 90);
                textCol = new Color(20, 100, 50);
            } else if (n.getColore().equals(Color.orange)) {
                fill    = new Color(255, 235, 200);
                border  = new Color(220, 140, 30);
                textCol = new Color(140, 70, 0);
            } else if (n.getColore().equals(Color.yellow)) {
                fill    = new Color(255, 250, 200);
                border  = new Color(200, 180, 0);
                textCol = new Color(100, 80, 0);
            } else if (n.getColore().equals(Color.red)) {
                fill    = new Color(255, 220, 220);
                border  = new Color(200, 60, 60);
                textCol = new Color(140, 20, 20);
            } else if (n.getColore().equals(Color.blue)) {
                fill    = new Color(215, 225, 255);
                border  = new Color(70, 110, 220);
                textCol = new Color(30, 60, 170);
            } else {
                fill    = n.getColore().brighter();
                border  = n.getColore();
                textCol = Color.BLACK;
            }

   
            g2.setColor(new Color(0, 0, 0, 25));
            g2.fillOval(lx + 2, ly + 3, lw, lh);

    
            g2.setColor(fill);
            g2.fillOval(lx, ly, lw, lh);

     
            g2.setColor(border);
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawOval(lx, ly, lw, lh);

            
            g2.setColor(textCol);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(n.getContenuto());
            g2.drawString(n.getContenuto(), n.getX() - tw / 2, n.getY() + fm.getAscent() / 2 - 1);
        }
    }

    public void disegnaArchi(Graphics2D g2) {
        g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (Arco a : ElencoArchi) {
            Color ec = a.getColore().equals(Color.black) ? EDGE_COLOR : a.getColore();
            g2.setColor(ec);
            g2.drawLine(a.getxStart(), a.getyStart(), a.getxEnd(), a.getyEnd());
            if (!a.getLabel().isEmpty()) {
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.setColor(new Color(120, 130, 160));
                g2.drawString(a.getLabel(), (a.getxStart() + a.getxEnd()) / 2 + 10, (a.getyStart() + a.getyEnd()) / 2);
            }
        }
    }

    public void zoomIn()  { zoomFactor *= 1.1; repaint(); }
    public void zoomOut() { zoomFactor /= 1.1; repaint(); }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        if (fbt.JTBTab.isEnabled()) {
    
            Point2D world = screenToWorld(mouseEvent.getX(), mouseEvent.getY());
            int coordX = (int) world.getX();
            int coordY = (int) world.getY();
            for (NodoGrafico n : ElencoNodi) {
                if ((coordX > n.getX() - n.getLarghezza() / 2) & (coordY > n.getY() - n.getAltezza() / 2) &
                    (coordX < n.getX() + n.getLarghezza() / 2) & (coordY < n.getY() + n.getAltezza() / 2)) {
                    if (!n.getColore().equals(Color.green)) {
                        n.setColore(Color.green);
                        if (fbt.JCBNum.isSelected()) fbt.cambiaListaSelezionati("Add", Double.parseDouble(n.getContenuto()));
                        else fbt.cambiaListaSelezionati("Add", n.getContenuto());
                        fbt.resettaSuccPred(); repaint();
                    } else {
                        n.setColore(Color.white);
                        if (fbt.JCBNum.isSelected()) fbt.cambiaListaSelezionati("Del", Double.parseDouble(n.getContenuto()));
                        else fbt.cambiaListaSelezionati("Del", n.getContenuto());
                        repaint(); fbt.resettaSuccPred();
                    }
                }
            }
        }
    }

    @Override public void mousePressed(MouseEvent e)  { startPoint = MouseInfo.getPointerInfo().getLocation(); }
    @Override public void mouseReleased(MouseEvent e) {
        if (dragger) {
            xOffset += xDiff;
            yOffset += yDiff;
            xDiff = 0;
            yDiff = 0;
            dragger = false;
            repaint();
        }
    }
    @Override public void mouseEntered(MouseEvent e)  {}
    @Override public void mouseExited(MouseEvent e)   {}

    @Override
    public void mouseDragged(MouseEvent e) {
        if (tabella) return;
        Point curPoint = e.getLocationOnScreen();
        xDiff = curPoint.x - startPoint.x;
        yDiff = curPoint.y - startPoint.y;
        dragger = true; repaint();
    }
    @Override public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (tabella) {
            if (e.isControlDown()) {
                if (e.getWheelRotation() < 0) { tableFontSize = Math.min(tableFontSize + 1, TABLE_FONT_MAX); applyTableZoom(); }
                else                           { tableFontSize = Math.max(tableFontSize - 1, TABLE_FONT_MIN); applyTableZoom(); }
            } else {
                scrollPane.getVerticalScrollBar().setValue(
                    scrollPane.getVerticalScrollBar().getValue() + e.getWheelRotation() * 16);
            }
        } else {
           
            double mouseX = e.getX();
            double mouseY = e.getY();
            double oldZoom = zoomFactor;
            if (e.getWheelRotation() < 0) zoomFactor *= 1.1;
            else                           zoomFactor /= 1.1;
    
            xOffset = mouseX - (mouseX - xOffset) * (zoomFactor / oldZoom);
            yOffset = mouseY - (mouseY - yOffset) * (zoomFactor / oldZoom);
            repaint();
        }
    }
}
