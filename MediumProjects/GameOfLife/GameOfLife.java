package life;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;


public class GameOfLife extends JFrame {

    public JLabel genLabel;

    public JLabel aliveLabel;

    public JToggleButton playToggleButton;

    public JButton resetButton;

    public boolean isStopped;

    public GameOfLife() {
        super();
        isStopped = false;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);
        setLocationRelativeTo(null);

        BoardPanel board = new BoardPanel();

        JPanel countPanel = new JPanel();
        countPanel.setBounds(0,0,300,80);
        countPanel.setLayout(new BoxLayout(countPanel, BoxLayout.Y_AXIS));
        countPanel.setName("count");

        genLabel = new JLabel("Generation #0");
        genLabel.setName("GenerationLabel");
        countPanel.add(genLabel);

        aliveLabel = new JLabel("Alive: 0");
        aliveLabel.setName("AliveLabel");
        countPanel.add(aliveLabel);

        playToggleButton = new JToggleButton("Stop");
        playToggleButton.setName("PlayToggleButton");
        playToggleButton.addActionListener(new PlayToggleActionListener(board));
        countPanel.add(playToggleButton);

        resetButton = new JButton("Reset");
        resetButton.setName("ResetButton");
        resetButton.addActionListener(new ResetActionListener(board));
        countPanel.add(resetButton);

        this.add(countPanel);

        board.setName("board");
        this.add(board);

        setVisible(true);
    }

    private static HashMap<String, Component> createComponentMap(JFrame frame) {
        HashMap<String, Component>  map = new HashMap<>();
        Component[] components = frame.getContentPane().getComponents();
        for (Component component : components) {
            map.put(component.getName(), component);
        }
        return map;
    }

    public static Component getComponentByName(HashMap<String, Component> map, String name) {
        return map.getOrDefault(name, null);
    }


    public static void main(String[] args) {
       GameOfLife game = new GameOfLife();
       BoardPanel board = (BoardPanel) getComponentByName(createComponentMap(game), "board");
       while (true) {
           try {
               Thread.sleep(1000);
               game.repaint();
               if (!board.updateAliveLabel(game.aliveLabel))
                   break;
               board.updateGenerationLabel(game.genLabel);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       }
    }
}

class ResetActionListener implements ActionListener {

    BoardPanel boardPanel;

    public ResetActionListener(BoardPanel panel) {
        this.boardPanel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        System.out.println(e.getActionCommand());
        boardPanel.clear();
    }
}

class PlayToggleActionListener implements ActionListener {

    BoardPanel boardPanel;

    public PlayToggleActionListener(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JToggleButton button = (JToggleButton) e.getSource();
        if (button.getText().equals("Stop")) {
            button.setText("Play");
            boardPanel.stop();
        } else if(button.getText().equals("Play")) {
            button.setText("Stop");
            boardPanel.play();
        }
    }
}
