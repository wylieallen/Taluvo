package Taluvo.Game.PlayerControllers;

import Taluvo.Game.GameModel.Game;
import Taluvo.Game.GameUberstate;

import javax.swing.*;

public class AgentPlayerController implements PlayerController
{

    public void activate(GameUberstate uberstate)
    {
        SwingWorker<Boolean, Boolean> worker = new SwingWorker<Boolean, Boolean>()
        {
          public Boolean doInBackground()
          {
              try
              {
                  Thread.sleep(50);
                  uberstate.playNextTurn();
                  SwingUtilities.invokeLater(new Runnable()
                  {
                      public void run()
                      {
                          uberstate.update();
                          uberstate.repaint();
                      }
                  });
                  return true;
              }
              catch(Exception e)
              {
                  e.printStackTrace();
                  return false;
              }
          }
        };

        worker.execute();
    }

    public void deactivate()
    {

    }
}
