package game.ui;

import game.MasterMind;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainPanel extends JPanel implements ActionListener{

    private List<Shape> colorBoard = new ArrayList<>();
    private List<Shape> board = new ArrayList<>();
    private List<Color> userInput = new ArrayList<>();
    private Map<Long, Color> colorMap = new HashMap<>();
    private List<Color> boardColors = new ArrayList<>();
    private List<Shape> responseBoard = new ArrayList<>();
    private List<Color> responseColors = new ArrayList<>();

    private int totalGuesses = 0;
    private int responseTotal = 0;
    private static final int row = 21;
    private static final int col = 6;
    private int currentRow = 0;

    private int colorBoardXPos = 280;
    private int colorBoardYPos = 900;
    private int colorBoardOffset = 40;
    private int userBoardXPos = 270;
    private int userBoardYPos = 60;
    private int responseBoardXPos = 520;
    private int responseBoardYPos = 65;
    private int responseBoardOffset = 30;
    private int width = 30;
    private int height = 30;

    private JButton submit = new JButton("Submit your guess");
    private JButton quit = new JButton("Give up");
    public MouseListener mouseListener;

    MasterMind game = new MasterMind();

    public MainPanel() {
        initializeColorBoard();
        initializeUserBoard();
        initializeResponseBoard();

        Dimension buttonSize = new Dimension(200,40);
        add(submit);
        submit.setPreferredSize(buttonSize);
        submit.setFont(new Font("Arial", Font.PLAIN, 20));

        add(quit);
        quit.setPreferredSize(buttonSize);
        quit.setFont(new Font("Arial", Font.PLAIN, 20));

        submit.addActionListener(this);
        quit.addActionListener(this);

        mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                for (int i = 0; i < colorBoard.size(); i++){
                    if (colorBoard.get(i).contains(e.getPoint()) && userInput.size() < col){
                        userInput.add(colorMap.get((long) i));
                        boardColors.set(totalGuesses, userInput.get(userInput.size() - 1));
                        totalGuesses++;
                        repaint();
                    }

                }
            }
        };
        addMouseListener(mouseListener);
    }

    private void initializeResponseBoard() {
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++) {
                Shape shape = new Rectangle2D.Double(responseBoardXPos + responseBoardOffset * j, responseBoardYPos + colorBoardOffset * i, 20, 20);
                responseBoard.add(shape);
                responseColors.add(Color.WHITE);
            }
        }
    }

    private void initializeUserBoard() {
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++) {
                Shape shape = new Rectangle2D.Double(userBoardXPos + colorBoardOffset * j, userBoardYPos + colorBoardOffset * i, width, height);
                board.add(shape);
                boardColors.add(Color.LIGHT_GRAY);
            }
        }
    }

    public void initializeColorBoard(){
        List<Color> userColors = game.getAvailableColors();

        for(int i = 0; i < userColors.size(); i++) {
            Shape shape = new Ellipse2D.Double(colorBoardXPos + colorBoardOffset *i, colorBoardYPos, width, height);
            colorBoard.add(shape);
            colorMap.put((long) i, userColors.get(i));
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        if(e.getActionCommand().equals("Submit your guess"))
        {
            if(userInput.size() == col) {
                Map<MasterMind.Response, Long> response = game.guess(userInput);

                long match = response.get(MasterMind.Response.MATCH);
                long posMatch = response.get(MasterMind.Response.POSITIONAL_MATCH);

                for (long i = 0; i < posMatch; i++) {
                    responseColors.set(responseTotal, Color.BLACK);
                    responseTotal++;
                }
                for (long i = 0; i < match; i++) {
                    responseColors.set(responseTotal, Color.GRAY);
                    responseTotal++;
                }

                repaint();
                responseTotal = totalGuesses;
                currentRow++;
                userInput.clear();

                if(game.getGameStatus() == MasterMind.Status.WON) {
                    displayCorrectAnswer();
                    JOptionPane.showMessageDialog(null, "Congratulations! You won.");

                }
                if(game.getGameStatus() == MasterMind.Status.LOST)
                {
                    displayCorrectAnswer();
                    JOptionPane.showMessageDialog(null, "You lost the game, the answer is displayed.");
                }
            }
        }
        if(e.getActionCommand().equals("Give up")) {
            displayCorrectAnswer();
            JOptionPane.showMessageDialog(null, "You lost the game, the answer is displayed.");
        }
    }

    public Dimension getPreferredSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Dimension(screenSize.width/2,screenSize.height);
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        int index = 0;
        Graphics2D graphics2D = (Graphics2D) graphics;

        for(Shape shape: colorBoard) {
            graphics2D.setColor(colorMap.get((long)index));
            graphics2D.fill(shape);
            graphics2D.setColor(Color.BLACK);
            graphics2D.draw(shape);
            index++;
        }

        index = 0;
        for(Shape shape: board) {
            graphics2D.setColor(boardColors.get(index));
            graphics2D.fill(shape);
            graphics2D.setColor(Color.BLACK);
            graphics2D.draw(shape);
            graphics2D.drawRect(userBoardXPos - colorBoardOffset/2,  (currentRow)*colorBoardOffset + userBoardYPos + height + (userBoardXPos - colorBoardOffset)/40 , width*15, 1);
            index++;
        }

        index = 0;
        for(Shape shape: responseBoard) {
            graphics2D.setColor(responseColors.get(index));
            graphics2D.fill(shape);
            graphics2D.setColor(Color.BLACK);
            graphics2D.draw(shape);
            index++;
        }
    }

    public void displayCorrectAnswer(){
        List<Color> answer = game.getSelection();

        int bottomUserBoard = (row-1)*col;
        for(int i = 0; i < answer.size(); i++) {
            boardColors.set(bottomUserBoard + i, answer.get(i));
        }
        repaint();
        removeMouseListener(mouseListener);
        remove(submit);
        remove(quit);
    }
}



