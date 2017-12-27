package Taluvo.Game;

import Taluvo.GUI.InterfacePanel;
import Taluvo.Game.GameModel.Game;
import Taluvo.Game.GameModel.MoveTabulator;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends InterfacePanel
{
    private GameUberstate gameUberstate;

    public GamePanel()
    {
        super(null);
        super.setActiveUberstate(gameUberstate = new GameUberstate(new Point(0, 0), new Dimension(1280, 720), this.getCamera()));
        centerCamera();
    }

    public void reset()
    {
        this.setActiveUberstate(new GameUberstate(new Point(0, 0), this.getSize(), this.getCamera()));
        centerCamera();
    }

    public void autoplayGames()
    {
        int numberOfGames = Integer.parseInt(JOptionPane.showInputDialog("How many games?"));
        Boolean playerFirst = true;

        int instawins[] = new int[]{0,0};
        int defaults[] = new int[]{0,0};
        int gameCount[] = new int[]{0,0};

        MoveTabulator tabulator = new MoveTabulator();

        for(int i = 0; i < numberOfGames; i++)
        {
            System.out.println("Starting Game #" + i);
            //Game newGame = new Game(new Point(16 * 40 - 20, 9 * 40 - 20));
            Game newGame = new Game();
            // Toggle first player:
            playerFirst = !playerFirst;

            while(newGame.getEndCondition() == Game.EndCondition.ACTIVE)
            {
                tabulator.playNextTurn(newGame);
            }

            Game.EndCondition result = newGame.getEndCondition();

            int playerIndex = playerFirst ? 0 : 1;

            if(result == Game.EndCondition.NO_BUILDINGS)
            {
                instawins[playerIndex]++;
            }
            else if (result == Game.EndCondition.NO_MOVES)
            {
                defaults[playerIndex]++;
            }

            gameCount[playerIndex]++;

            //System.out.println("First move Instawins: " + instawins[0] + ", rate: " + ((float) instawins[0] / (float) gameCount[0]) * 100 + "%");
            //System.out.println("Second move Instawins: " + instawins[1] + ", rate: " + ((float) instawins[1] / (float) gameCount[1]) * 100 + "%");
            //System.out.println("First move Defaults: " + defaults[0] + ", rate: " + ((float) defaults[0] / (float) gameCount[0]) * 100 + "%");
            //System.out.println("Second move Defaults: " + defaults[1] + ", rate: " + ((float) defaults[1] / (float) gameCount[1]) * 100 + "%");

            //panel.paintImmediately(0, 0, WIDTH, HEIGHT);
        }

        int instaTotal = instawins[0] + instawins[1];
        int defaultTotal = defaults[0] + defaults[1];

        int points = ((instaTotal * 20) - (defaultTotal)) * 10;

        System.out.println("Instawins: " + instaTotal + ", rate: " + ((float) instaTotal / (float) numberOfGames) * 100 + "%");
        System.out.println("Defaults: " + defaultTotal + ", rate: " + ((float) defaultTotal / (float) numberOfGames) * 100 + "%");
        System.out.println("Net points: " + points + ", average: " + ((float) points / (float) numberOfGames) + " points per game");
    }


}
