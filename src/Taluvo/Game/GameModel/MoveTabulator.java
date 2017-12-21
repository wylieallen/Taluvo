package Taluvo.Game.GameModel;

import java.util.HashSet;
import java.util.Set;

public class MoveTabulator
{
    private Set<Hex> allHexes;

    private Set<Game.TilePlacementAction> legalTilePlacements;
    private Set<Game.BuildingPlacementAction> legalBuildingPlacements;
    private Set<Game.SettlementExpansionAction> legalSettlementExpansions;

    private Set<Game.TilePlacementAction> legalVolcanoPlacements;
    private Set<Game.TilePlacementAction> legalEmptyPlacements;
    private Set<Game.TilePlacementAction> towerEnablers;
    private Set<Game.TilePlacementAction> templeEnablers;
    private Set<Game.TilePlacementAction> singleNukes;
    private Set<Game.TilePlacementAction> doubleNukes;

    private Set<Game.BuildingPlacementAction> legalTowerPlacements;
    private Set<Game.BuildingPlacementAction> legalTemplePlacements;
    private Set<Game.BuildingPlacementAction> legalVillagePlacements;
    private Set<Game.BuildingPlacementAction> villagerPlacementsThatExpand;
    private Set<Game.BuildingPlacementAction> villagerPlacementsThatEnableTowers;

    private Set<Game.SettlementExpansionAction> efficientExpansions;

    public void analyze(Game game)
    {
        updateMoveset(game);
        updateTilePlacements(game);
        updateBuildingPlacements(game);
        //updateSettlementExpansions(game);
    }

    private void updateMoveset(Game game) { allHexes = new HashSet<>(game.getHexes()); }

    private void updateTilePlacements(Game game)
    {
        legalTilePlacements = new HashSet<>();
        legalVolcanoPlacements = new HashSet<>();
        legalEmptyPlacements = new HashSet<>();
        towerEnablers = new HashSet<>();
        templeEnablers = new HashSet<>();
        singleNukes = new HashSet<>();
        doubleNukes = new HashSet<>();

        for(Hex hex : allHexes)
        {
            for(Tile.Orientation orientation : Tile.Orientation.values())
            {
                Tile tile = new Tile(game.getDeck().peek());
                tile.setOrientation(orientation);
                Game.TilePlacementAction action = game.getTilePlacementAction(hex, tile);
                if(game.tilePlacementIsLegal(action))
                {
                    legalTilePlacements.add(action);

                    if(action.getTarget().getTerrain() == Hex.Terrain.VOLCANO)
                    {
                        legalVolcanoPlacements.add(action);

                        Hex hexA = game.getBoard().getNeighbor(action.getTarget(), action.getTile().getOrientation().ordinal());
                        Hex hexB = game.getBoard().getNeighbor(action.getTarget(), action.getTile().getOrientationPlus(1).ordinal());

                        int nukeCount = 0;

                        if(hexA.getOwner() != Player.NONE && hexA.getOwner() != game.getActivePlayer())
                        {
                            nukeCount++;
                        }

                        if(hexB.getOwner() != Player.NONE && hexB.getOwner() != game.getActivePlayer())
                        {
                            nukeCount++;
                        }

                        if(nukeCount == 1)
                        {
                            singleNukes.add(action);
                        }

                        if(nukeCount > 1)
                        {
                            doubleNukes.add(action);
                        }
                    }

                    else if (action.getTarget().getTerrain() == Hex.Terrain.EMPTY)
                    {
                        legalEmptyPlacements.add(action);
                    }
                }
            }
        }
    }

    private void updateBuildingPlacements(Game game)
    {
        legalBuildingPlacements = new HashSet<>();
        legalTowerPlacements = new HashSet<>();
        legalTemplePlacements = new HashSet<>();
        legalVillagePlacements = new HashSet<>();
        villagerPlacementsThatEnableTowers = new HashSet<>();
        villagerPlacementsThatExpand = new HashSet<>();

        for(Hex hex : allHexes)
        {
            for(Hex.Building building : Hex.Building.values())
            {
                Game.BuildingPlacementAction action = game.getBuildingPlacementAction(hex, building);
                if(game.buildingPlacementIsLegal(action.getTarget().getOrigin(), action.getBuilding(), game.getActivePlayer()))
                {
                    legalBuildingPlacements.add(action);

                    switch(action.getBuilding())
                    {
                        case VILLAGE:
                            legalVillagePlacements.add(action);
                            // todo: ... villagerPlacementsThatExpand.add etc
                            break;

                        case TOWER:
                            legalTowerPlacements.add(action);
                            break;

                        case TEMPLE:
                            legalTemplePlacements.add(action);
                            break;
                    }
                }
            }
        }
    }

    public void playNextTurn(Game game)
    {
        analyze(game);
        getBestTilePlacement().execute();
        analyze(game);
        getBestBuildAction(game).execute();
    }

    public Game.Action getBestTilePlacement()
    {
        if(!doubleNukes.isEmpty())
        {
            // Todo: get less goofy way of accessing an element from the sets
            for(Game.Action action : doubleNukes) { return action; }
        }

        if(!singleNukes.isEmpty())
        {
            for(Game.Action action : singleNukes) { return action; }
        }

        if(!legalVolcanoPlacements.isEmpty())
        {
            for(Game.Action action : legalVolcanoPlacements) { return action; }
        }

        else
        {
            for(Game.Action action : legalEmptyPlacements) { return action; }
        }

        // Todo: better exception
        throw new UnsupportedOperationException();
    }

    public Game.Action getBestBuildAction(Game game)
    {
        if(!legalTowerPlacements.isEmpty())
        {
            for(Game.Action action : legalTowerPlacements) { return action; }
        }

        if(!legalTemplePlacements.isEmpty())
        {
            for(Game.Action action : legalTemplePlacements) { return action; }
        }

        if(!legalVillagePlacements.isEmpty())
        {
            for(Game.Action action : legalVillagePlacements) { return action; }
        }

        else if(!legalBuildingPlacements.isEmpty())
        {
            for(Game.Action action : legalBuildingPlacements) { return action; }
        }

        return game.getDeclareForfeitAction();
    }

}
