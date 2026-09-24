import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FinestraBT extends JFrame implements ActionListener, ComponentListener, DocumentListener, KeyListener {
    JTextField JTFNodiDaElaborare;
    JCheckBox JTBTab;
    JCheckBox JCBNum;
    JCheckBox JCDPredecessor;
    JEditorPane JEPConsole;
    JButton JBAdd;
    JButton JBDel;
    JButton JBSearch;
    JButton JBPredecessore;
    JButton JBSuccessore;
    JButton JBPreorder;
    JButton JBPostorder;
    JButton JBInorder;
    JButton JBAddRandom;
    JButton JBReset;
    JButton JBBil;
    String ultimoAttraversato="";
    String nodoDaCercare;
    BSTView v;
    BST albero;
    Timer timerAttraversamento;
    Timer timerRicerca;
    int poslista=-1;
    int pos = 0;
    public static ArrayList<Comparable> listaSelezionati= new ArrayList<>();
    ArrayList<Riga> BSTTab = new ArrayList<Riga>();
    ArrayList<NodoGrafico> ElencoNodi = new ArrayList<NodoGrafico>();
    ArrayList<Arco> ElencoArchi = new ArrayList<Arco>();
    ArrayList<Comparable> lista;


    static final Color COLOR_BG_PANEL     = new Color(248, 249, 253);
    static final Color COLOR_BG_PANEL2    = new Color(238, 241, 252);
    static final Color COLOR_TITLE_BAR    = new Color(255, 255, 255);
    static final Color COLOR_TEXT_DIM     = new Color(100, 110, 150);
    static final Color COLOR_ACCENT       = new Color(60, 100, 200);
    static final Color COLOR_BORDER       = new Color(210, 215, 235);
    static final Color COLOR_BTN_SUCCESS  = new Color(48, 160, 95);
    static final Color COLOR_BTN_DANGER   = new Color(200, 65, 65);
    static final Color COLOR_BTN_PRIMARY  = new Color(55, 115, 210);
    static final Color COLOR_BTN_WARNING  = new Color(195, 135, 30);
    static final Color COLOR_BTN_PURPLE   = new Color(120, 80, 200);
    static final Color COLOR_BTN_TEAL     = new Color(32, 160, 145);
    static final Color COLOR_BTN_NAV      = new Color(80, 100, 185);
    static final Color COLOR_BTN_NEUTRAL  = new Color(130, 140, 170);
    static final Color COLOR_CONSOLE_BG   = new Color(252, 253, 255);

    public FinestraBT(BST albero) throws Exception {
        this.albero = albero;
        setSize(new Dimension(1200, 900));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        addComponentListener(this);
        setTitle("Binary Search Tree Visualizer");

        v = new BSTView(BSTTab, ElencoNodi, ElencoArchi, this);
        JScrollPane SP = new JScrollPane(v);
        SP.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        SP.getViewport().setBackground(Color.white);

        Container CP = getContentPane();
        CP.setBackground(Color.white);
        CP.setLayout(new BorderLayout(0, 0));
        CP.add(SP, BorderLayout.CENTER);

        JPanel JPComandi = new JPanel(new BorderLayout());
        JPComandi.setBackground(COLOR_BG_PANEL);
        JPComandi.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));


        JPanel titleBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        titleBar.setBackground(COLOR_TITLE_BAR);
        titleBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));
        JLabel titleLabel = new JLabel("Binary search tree");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(COLOR_ACCENT);
        titleBar.add(titleLabel);
        JPComandi.add(titleBar, BorderLayout.NORTH);

    
        JPanel JPCostruzione = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 9));
        JPCostruzione.setBackground(COLOR_BG_PANEL);
        JPCostruzione.setBorder(new EmptyBorder(2, 12, 2, 12));

        JLabel JLNodi = new JLabel("Nodes (separated by commas):");
        JLNodi.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLNodi.setForeground(COLOR_TEXT_DIM);

        JCBNum = new JCheckBox("Numeric");
        styleCheckBox(JCBNum);

        JTFNodiDaElaborare = new JTextField();
        JTFNodiDaElaborare.setColumns(26);
        JTFNodiDaElaborare.setFont(new Font("Consolas", Font.PLAIN, 14));
        JTFNodiDaElaborare.setBackground(Color.white);
        JTFNodiDaElaborare.setForeground(new Color(30, 40, 80));
        JTFNodiDaElaborare.setCaretColor(COLOR_ACCENT);
        JTFNodiDaElaborare.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            new EmptyBorder(4, 8, 4, 8)));
        Document doc = JTFNodiDaElaborare.getDocument();
        doc.addDocumentListener(this);
        JTFNodiDaElaborare.addKeyListener(this);

        JBAdd    = makeButton("+ Add",      COLOR_BTN_SUCCESS);  JBAdd.addActionListener(this);    JBAdd.setEnabled(false);
        JBDel    = makeButton("- Delete",   COLOR_BTN_DANGER);   JBDel.addActionListener(this);    JBDel.setEnabled(false);
        JBSearch = makeButton("Search",     COLOR_BTN_PRIMARY);  JBSearch.addActionListener(this); JBSearch.setEnabled(false);

        JCDPredecessor = new JCheckBox("Del by Predecessor");
        styleCheckBox(JCDPredecessor);
        JCDPredecessor.addActionListener(this);

        JBAddRandom = makeButton("Random Tree", COLOR_BTN_WARNING); JBAddRandom.addActionListener(this);
        JBReset     = makeButton("Reset",        COLOR_BTN_NEUTRAL); JBReset.addActionListener(this);

        JPCostruzione.add(JLNodi);
        JPCostruzione.add(JCBNum);
        JPCostruzione.add(JTFNodiDaElaborare);
        JPCostruzione.add(JBAdd);
        JPCostruzione.add(JBDel);
        JPCostruzione.add(JCDPredecessor);
        JPCostruzione.add(JBSearch);
        JPCostruzione.add(Box.createHorizontalStrut(12));
        JPCostruzione.add(JBAddRandom);
        JPCostruzione.add(JBReset);


        JPanel JPEsercizi = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JPEsercizi.setBackground(COLOR_BG_PANEL2);
        JPEsercizi.setBorder(new EmptyBorder(2, 12, 2, 12));

        JLabel lblOps = new JLabel("Opzioni:");
        lblOps.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblOps.setForeground(COLOR_TEXT_DIM);

        JBBil = makeButton("Balance", COLOR_BTN_TEAL); JBBil.addActionListener(this);

        JTBTab = new JCheckBox("Table View");
        styleCheckBox(JTBTab);
        JTBTab.addActionListener(this);

        JBInorder   = makeButton("Inorder",   COLOR_BTN_PURPLE); JBInorder.addActionListener(this);
        JBPreorder  = makeButton("Preorder",  COLOR_BTN_PURPLE); JBPreorder.addActionListener(this);
        JBPostorder = makeButton("Postorder", COLOR_BTN_PURPLE); JBPostorder.addActionListener(this);

        JBPredecessore = makeButton("Predecessor", COLOR_BTN_NAV); JBPredecessore.addActionListener(this); JBPredecessore.setEnabled(false);
        JBSuccessore   = makeButton("Successor",   COLOR_BTN_NAV); JBSuccessore.addActionListener(this);   JBSuccessore.setEnabled(false);

        JLabel JLZoom = new JLabel("    Scroll / Ctrl+- = zoom  |  Drag = pan");
        JLZoom.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        JLZoom.setForeground(new Color(170, 175, 200));

        JPEsercizi.add(lblOps);
        JPEsercizi.add(JBBil);
        JPEsercizi.add(JTBTab);
        JPEsercizi.add(Box.createHorizontalStrut(8));
        JPEsercizi.add(JBInorder);
        JPEsercizi.add(JBPreorder);
        JPEsercizi.add(JBPostorder);
        JPEsercizi.add(Box.createHorizontalStrut(8));
        JPEsercizi.add(JBPredecessore);
        JPEsercizi.add(JBSuccessore);
        JPEsercizi.add(JLZoom);

        JPanel rowsPanel = new JPanel(new BorderLayout());
        rowsPanel.setBackground(COLOR_BG_PANEL);
        rowsPanel.add(JPCostruzione, BorderLayout.NORTH);
        rowsPanel.add(JPEsercizi, BorderLayout.SOUTH);
        JPComandi.add(rowsPanel, BorderLayout.CENTER);
        getRootPane().setDefaultButton(JBAdd);

        JEPConsole = new JEditorPane();
        JEPConsole.setContentType("text/html");
        JEPConsole.setText("<html><body style='font-family:Segoe UI;color:#AABBCC;background:#FAFBFF;padding:5px'><i>Console output will appear here...</i></body></html>");
        JEPConsole.setEditable(false);
        JEPConsole.setBackground(COLOR_CONSOLE_BG);
        JEPConsole.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER),
            new EmptyBorder(4, 14, 4, 14)));
        JEPConsole.setPreferredSize(new Dimension(0, 50));

        CP.add(JPComandi, BorderLayout.NORTH);
        CP.add(JEPConsole, BorderLayout.SOUTH);

        setVisible(true);
        timerAttraversamento = new Timer(1000, this);
        timerAttraversamento.setActionCommand("TimeAttr");
        timerRicerca = new Timer(1000, this);
        timerRicerca.setActionCommand("TimeRice");
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = isEnabled() ? bg : new Color(190, 195, 210);
                if (getModel().isPressed())       g2.setColor(base.darker());
                else if (getModel().isRollover()) g2.setColor(base.brighter());
                else                              g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        return btn;
    }

    private void styleCheckBox(JCheckBox cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setForeground(COLOR_TEXT_DIM);
        cb.setBackground(null);
        cb.setOpaque(false);
        cb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String comando = actionEvent.getActionCommand();
        int dimx = 0;
        switch (comando) {
            case "+ Add":
                if (!JTFNodiDaElaborare.getText().trim().equals("")) {
                    aggiungiNodi(JTFNodiDaElaborare.getText());
                    dimx = this.getWidth() / 2;
                    creaAlberoGrafico(albero.getRadice(), dimx, 40, 50, dimx / 2);
                }
                break;
            case "- Delete":  eliminaNodi(); break;
            case "Search":
                nodoDaCercare = JTFNodiDaElaborare.getText();
                ricerca();
                break;
            case "Random Tree":
                aggiungiNodi(creaNodiCasuali(1));
                dimx = this.getWidth() / 2;
                creaAlberoGrafico(albero.getRadice(), dimx, 40, 50, dimx / 2);
                break;
            case "Reset":
                albero.setRadice(null);
                listaSelezionati.clear();
                abilitaDisabilitaPulsanti();
                ElencoArchi.clear();
                ElencoNodi.clear();
                v.ridisegna(JTBTab.isSelected());
                break;
            case "Balance":
                bilanciaAlbero();
                ElencoArchi.clear();
                dimx = this.getWidth() / 2;
                creaAlberoGrafico(albero.getRadice(), dimx, 40, 50, dimx / 2);
                break;
            case "Table View":  visualizzaTabella(); break;
            case "Inorder":     attraversamento("IN"); break;
            case "Preorder":    attraversamento("PRE"); break;
            case "Postorder":   attraversamento("POST"); break;
            case "Predecessor": predecessore(); break;
            case "Successor":   successore(); break;
            case "TimeAttr":    scorriPerAttraversamento(); break;
            case "TimeRice":    scorriPerRicerca(); break;
        }
        JTFNodiDaElaborare.setText("");
        JCBNum.setEnabled(albero.getRadice() == null);
        JTFNodiDaElaborare.requestFocus();
    }

    private void scorriPerRicerca() {
        poslista++;
        if (poslista == lista.size()) {
            timerRicerca.stop();
            for (Comparable n : lista)
                if (!nodoDaCercare.equals(normalizzaDouble(n.toString())))
                    segnala(normalizzaDouble(n.toString()), Color.white);
                else {
                    segnala(normalizzaDouble(n.toString()), Color.green);
                    if (!JCBNum.isSelected()) cambiaListaSelezionati("Add", normalizzaDouble(n.toString()));
                    else cambiaListaSelezionati("Add", Double.parseDouble(normalizzaDouble(n.toString())));
                }
            JBInorder.setEnabled(true); JBPostorder.setEnabled(true); JBPreorder.setEnabled(true);
            JBAddRandom.setEnabled(true); JTBTab.setEnabled(true); JBBil.setEnabled(true); JBReset.setEnabled(true);
            ultimoAttraversato = "";
        } else {
            segnala(ultimoAttraversato, Color.yellow);
            segnala(normalizzaDouble(lista.get(poslista).toString()), Color.orange);
            ultimoAttraversato = normalizzaDouble(lista.get(poslista).toString());
        }
    }

    private void scorriPerAttraversamento() {
        poslista++;
        if (poslista == lista.size()) {
            timerAttraversamento.stop();
            for (Comparable n : lista) segnala(normalizzaDouble(n.toString()), Color.white);
            JBInorder.setEnabled(true); JBPostorder.setEnabled(true); JBPreorder.setEnabled(true);
            JBAddRandom.setEnabled(true); JTBTab.setEnabled(true); JBBil.setEnabled(true); JBReset.setEnabled(true);
            ultimoAttraversato = "";
        } else {
            segnala(ultimoAttraversato, Color.yellow);
            segnala(normalizzaDouble(lista.get(poslista).toString()), Color.orange);
            ultimoAttraversato = normalizzaDouble(lista.get(poslista).toString());
        }
    }

    private void aggiungiNodi(String nodiDaAggiungere) {
        String[] elencoNodi = nodiDaAggiungere.split(",");
        for (String info : elencoNodi) {
            info = info.trim();
            if (!JCBNum.isSelected()) albero.inserisciNodo(info);
            else { try { albero.inserisciNodo(Double.parseDouble(info)); } catch (Exception e) { System.out.println("Valore non numerico!"); } }
        }
        tabella(albero); v.ridisegna(JTBTab.isSelected()); svuotaConsole();
    }

    private void eliminaNodi(String nodiDaEliminare) {
        String[] elencoNodi = nodiDaEliminare.split(",");
        JTFNodiDaElaborare.setText("");
        for (String info : elencoNodi) {
            info = info.trim();
            if (JCBNum.isSelected()) {
                if (JCDPredecessor.isSelected()) { albero.cancellaNodo(Double.parseDouble(info), true); eliminaNodoGrafico(normalizzaDouble("" + Double.parseDouble(info))); }
                albero.cancellaNodo(Double.parseDouble(info)); eliminaNodoGrafico(normalizzaDouble("" + Double.parseDouble(info)));
            } else {
                if (JCDPredecessor.isSelected()) { albero.cancellaNodo(info, true); eliminaNodoGrafico(info); }
                albero.cancellaNodo(info); eliminaNodoGrafico(info);
            }
        }
        tabella(albero); v.ridisegna(JTBTab.isSelected()); svuotaConsole();
    }

    private void bilanciaAlbero() { albero.bilanciamento(); tabella(albero); v.ridisegna(JTBTab.isSelected()); svuotaConsole(); }
    private void visualizzaTabella() { tabella(albero); v.ridisegna(JTBTab.isSelected()); svuotaConsole(); }

    public void tabella(BST t) {
        BSTTab.clear(); pos = 0;
        NodoBT r = t.getRadice();
        if (r != null) tabella(r);
    }

    private void tabella(NodoBT r) {
        pos++;
        if (r != null) {
            if (r.getSinistra() == null && r.getDestra() == null) BSTTab.add(new Riga(pos, r.getInfo().toString(), 0, 0));
            if (r.getSinistra() != null && r.getDestra() == null) { BSTTab.add(new Riga(pos, r.getInfo().toString(), pos + 1, 0)); tabella(r.getSinistra()); }
            if (r.getSinistra() == null && r.getDestra() != null) { BSTTab.add(new Riga(pos, r.getInfo().toString(), 0, (pos + 1 + BST.numeroNodi(r.getSinistra())))); tabella(r.getDestra()); }
            if (r.getSinistra() != null && r.getDestra() != null) { BSTTab.add(new Riga(pos, r.getInfo().toString(), pos + 1, (pos + 1 + BST.numeroNodi(r.getSinistra())))); tabella(r.getSinistra()); tabella(r.getDestra()); }
        }
    }

    public void ricerca() {
        poslista = -1; lista = new ArrayList<>();
        if (JCBNum.isSelected()) lista = albero.cercaPercorso(Double.parseDouble(JTFNodiDaElaborare.getText()));
        else lista = albero.cercaPercorso(JTFNodiDaElaborare.getText());
        String msg = "<html><body style='font-family:Segoe UI;color:#3355AA;background:#FAFBFF;padding:4px'><b>SEARCH PATH:</b>  ";
        for (int i = 0; i < lista.size(); i++) {
            if (i < lista.size() - 1) msg += "<span style='color:#BB6600'>" + normalizzaDouble(lista.get(i).toString()) + "</span>  &rarr;  ";
            else msg += "<span style='color:#226622'>" + normalizzaDouble(lista.get(i).toString()) + "</span>";
        }
        timerRicerca.start();
        JEPConsole.setText(msg + "</body></html>");
        JEPConsole.setEnabled(true);
    }
    
    public void attraversamento(String ordine) {
        if (albero.getRadice() != null) {
            poslista = -1;
            JBInorder.setEnabled(false); JBPostorder.setEnabled(false); JBPreorder.setEnabled(false);
            JBAddRandom.setEnabled(false); JTBTab.setEnabled(false); JBBil.setEnabled(false); JBReset.setEnabled(false);
            lista = new ArrayList<>();
            String label = "";
            switch (ordine) {
                case "IN"   -> { label = "INORDER";   lista = albero.attraversamentoSimmetrico(); }
                case "PRE"  -> { label = "PREORDER";  lista = albero.attraversamentoAnticipato(); }
                case "POST" -> { label = "POSTORDER"; lista = albero.attraversamentoPosticipato(); }
            }
            String msg = "<html><body style='font-family:Segoe UI;color:#3355AA;background:#FAFBFF;padding:4px'><b>" + label + ":</b>  ";
            for (int i = 0; i < lista.size(); i++) {
                if (i < lista.size() - 1) msg += "<span style='color:#BB6600'>" + normalizzaDouble(lista.get(i).toString()) + "</span>  ,  ";
                else msg += "<span style='color:#226622'>" + normalizzaDouble(lista.get(i).toString()) + "</span>";
            }
            timerAttraversamento.start();
            JEPConsole.setText(msg + "</body></html>");
            JEPConsole.setEnabled(true);
        }
    }

    private void successore() {
        NodoBT nodoSelezionato = albero.ricercaNodo(listaSelezionati.get(0));
        NodoBT succ = albero.trovaSuccessore(albero.getRadice(), nodoSelezionato);
        segnala(normalizzaDouble(succ.getInfo().toString()), Color.red);
    }

    private void predecessore() {
        NodoBT nodoSelezionato = albero.ricercaNodo(listaSelezionati.get(0));
        NodoBT pred = albero.trovaPredecessore(albero.getRadice(), nodoSelezionato);
        segnala(normalizzaDouble(pred.getInfo().toString()), Color.blue);
    }

    public void segnala(String contNodo, Color c) {
        for (NodoGrafico n : ElencoNodi) if (n.getContenuto().equals(contNodo)) n.setColore(c);
        v.repaint();
    }

    public void svuotaConsole() {
        JEPConsole.setText("<html><body style='font-family:Segoe UI;color:#AABBCC;background:#FAFBFF;padding:5px'><i>Console output will appear here...</i></body></html>");
    }

    public void cambiaListaSelezionati(String tipo, Comparable contenutoNodo) {
        if (tipo.equals("Add")) listaSelezionati.add(contenutoNodo);
        else listaSelezionati.remove(contenutoNodo);
        abilitaDisabilitaPulsanti();
    }

    public void resettaSuccPred() {
        for (NodoGrafico n : ElencoNodi)
            if (n.getColore().equals(Color.blue) | n.getColore().equals(Color.red)) n.setColore(Color.white);
    }

    public String normalizzaDouble(String a) {
        String pulita = a;
        if (a.length() > 1) if (a.startsWith(".0", a.length() - 2)) pulita = a.replace(".0", "");
        return pulita;
    }

    private void eliminaNodoGrafico(String info) {
        for (int i = 0; i < ElencoNodi.size(); i++) {
            if (info.equals(ElencoNodi.get(i).getContenuto())) { ElencoNodi.remove(i); ElencoArchi.clear(); break; }
        }
    }

    private void creaAlberoGrafico(NodoBT node, int x, int y, int size, int dist) {
        if (node != null) {
            NodoGrafico n = cercaNodoGrafico(normalizzaDouble(node.getInfo().toString()));
            if (n == null) creaNodo(x, y, size / 2, normalizzaDouble(node.getInfo().toString()));
            else { n.setX(x); n.setY(y); }
            if (node.getSinistra() != null) { int x1 = x - dist, y1 = y + size * 2; ElencoArchi.add(new Arco(x, x1, y + size / 2, y1 - size / 2, Color.black, "")); creaAlberoGrafico(node.getSinistra(), x1, y1, size, dist / 2); }
            if (node.getDestra() != null)   { int x2 = x + dist, y2 = y + size * 2; ElencoArchi.add(new Arco(x, x2, y + size / 2, y2 - size / 2, Color.black, "")); creaAlberoGrafico(node.getDestra(), x2, y2, size, dist / 2); }
        }
    }

    private NodoGrafico cercaNodoGrafico(String a) {
        for (NodoGrafico n : ElencoNodi) if (n.getContenuto().equals(a)) return n;
        return null;
    }

    private void creaNodo(int x, int y, int r, String contenuto) {
        int lungContenuto = contenuto.length();
        int larghezza = (lungContenuto <= 3) ? r * 2 : lungContenuto * 13;
        ElencoNodi.add(new NodoGrafico(x, y, larghezza, r * 2, Color.white, contenuto));
    }

    @Override
    public void componentResized(ComponentEvent e) {
        ElencoArchi.clear();
        int dimx = this.getWidth() / 2;
        creaAlberoGrafico(albero.getRadice(), dimx, 40, 50, dimx / 2);
        v.ridisegna(false);
    }
    @Override public void componentMoved(ComponentEvent e) {}
    @Override public void componentShown(ComponentEvent e) {}
    @Override public void componentHidden(ComponentEvent e) {}
    @Override public void insertUpdate(DocumentEvent e) { abilitaDisabilitaPulsanti(); }
    @Override public void removeUpdate(DocumentEvent e) { abilitaDisabilitaPulsanti(); }
    @Override public void changedUpdate(DocumentEvent e) { abilitaDisabilitaPulsanti(); }

    private void abilitaDisabilitaPulsanti() {
        boolean hasText = JTFNodiDaElaborare.getText().length() > 0;
        JBAdd.setEnabled(hasText); JBSearch.setEnabled(hasText);
        JBDel.setEnabled(hasText || listaSelezionati.size() > 0);
        boolean one = listaSelezionati.size() == 1;
        JBPredecessore.setEnabled(one); JBSuccessore.setEnabled(one);
    }

    private void eliminaNodi() {
        if (!JTFNodiDaElaborare.getText().trim().equals("")) eliminaNodi(JTFNodiDaElaborare.getText());
        else if (listaSelezionati.size() > 0) for (Comparable cn : listaSelezionati) eliminaNodi(cn.toString());
        int dimx = this.getWidth() / 2;
        creaAlberoGrafico(albero.getRadice(), dimx, 40, 50, dimx / 2);
        listaSelezionati.clear(); abilitaDisabilitaPulsanti();
    }

    String creaNodiCasuali(int numero) {
        String elencoNodi = "";
        Random r = new Random();
        if (JCBNum.isSelected()) {
            for (int i = 1; i <= numero; i++) elencoNodi += (double) r.nextInt(200) + ",";
        } else {
            for (int i = 1; i <= numero; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < 2; j++) sb.append((char)(r.nextInt(26) + 'A'));
                elencoNodi += sb + ",";
            }
        }
        return elencoNodi.substring(0, elencoNodi.length() - 1);
    }
    
    @Override public void keyTyped(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            eliminaNodi(); JTFNodiDaElaborare.setText("");
            JCBNum.setEnabled(albero.getRadice() == null); JTFNodiDaElaborare.requestFocus();
        }
    }
    @Override public void keyReleased(KeyEvent e) {}
}
