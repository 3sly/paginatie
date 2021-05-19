import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class Userinterface {

    public static void main(List<Main.Instruction> instructions, int[] virtualAddressSpace) {

        JFrame f = new JFrame("A JFrame");

        f.setSize(550, 750);
        f.setLocation(500,100);

        final JTextArea textArea = new JTextArea(10, 40);
        f.getContentPane().add(BorderLayout.CENTER, textArea);

        final JButton button = new JButton("Button 1");
        final JButton button2 = new JButton("Button 2");

        f.getContentPane().add(BorderLayout.WEST, button);

        f.getContentPane().add(BorderLayout.EAST, button2);

        int counter = 0;

        // shows only one
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.append("Button 1 was pressed \n");
                textArea.append("timer:" + "++incrementing number++ \n");
                textArea.append("instructie - virtuele adres huidige instructie - virtuele adres volgende instructie - reële adress [frame, offset ] \n");
                textArea.append("page table: " + "[0, 16] entries -> [frameid, presentbit, modifybit, lastaccestime ] \n");
                textArea.append("RAM" + "[0, 11] blokken/frames -> [pid, pagenumber] \n");
                textArea.append("Reële adres:" + "die aangesproken moet worden in het RAM \n");
                textArea.append(" \n");
            }

        });

        // shows all of them
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.append("Button 2 was pressed \n");
                for(int i = 0; i < instructions.size(); i++) {
                    textArea.append("resultaten tabel \n");
                }
                textArea.append(" \n");
            }
        });

        f.setVisible(true);

    }

}