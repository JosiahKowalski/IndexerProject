import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DatabaseSearch extends JFrame implements ActionListener {
    String toggle = null;
    JFrame f;
    JTextField tf, tf1, tf2;
    JLabel label;
    JButton b;

    DatabaseSearch() {
        f = new JFrame("Pokedex DataBase");
        JLabel label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setSize(400,100);

        JButton b = new JButton("Search");
        b.setBounds(200,100,75,20);
        b.addActionListener(this);

        String[] searches = {"Exact Match","Range Query"};
        final JComboBox cb = new JComboBox(searches);
        cb.setBounds(50,100,90,20);

        f.add(cb); f.add(label); f.add(b);
        f.setLayout(null);
        f.setSize(350,350);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

        cb.addActionListener(e -> {
            String data = "" + cb.getItemAt(cb.getSelectedIndex());
            label.setText(data);

            updateSearch(data);
            updateFrame();
        });
        b.addActionListener(this);
    }

    public void updateSearch(String search) {
        if (search.equals("Exact Match")) {
            tf = new JTextField();
            tf.setBounds(85,75,150,20);
            f.add(tf);
            toggle = "exact";
        } else if (search.equals("Range Query")) {
            tf1 = new JTextField(); tf2 = new JTextField();
            tf1.setBounds(50,75,100,20);
            tf2.setBounds(200,75,100,20);
            f.add(tf1); f.add(tf2);
            toggle = "range";
        }
    }

    public void updateFrame() {
        if (toggle.equals("exact")) {
            if (tf1.isVisible() && tf2.isVisible()) {
                f.remove(tf1); f.remove(tf2);
                revalidate();
                repaint();
            }
        } else if (toggle.equals("range")) {
            if (tf.isVisible()) {
                f.remove(tf);
                revalidate();
                repaint();
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
            try {
                String host = tf.getText();
                String ip = java.net.InetAddress.getByName(host).getHostAddress();
                label.setText("IP of "+host+" is: "+ip);
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }

    public static void main(String[] args) {
        DatabaseSearch db = new DatabaseSearch();
    }
}
