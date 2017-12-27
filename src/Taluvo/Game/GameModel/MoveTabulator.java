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
    private Game.SettlementExpansionAction maxExpansion;
    private int maxCost;

    public void analyze(Game game)
    {
        updateMoveset(game);
        updateTilePlacements(game);
        updateBuildingPlacements(game);
        updateSettlementExpansions(game);
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
                    //System.out.println("hexloc " + hex.getOrigin());

                    legalTilePlacements.add(action);

                    if(action.getTarget().getTerrain() == Hex.Terrain.VOLCANO)
                    {
                        legalVolcanoPlacements.add(action);

                        Hex hexA = game.getBoard().getNeighbor(action.getTarget(), action.getTile().getOrientation().ordinal());
                        Hex hexB = game.getBoard().getNeighbor(action.getTarget(), action.getTile().getOrientationPlus(1).ordinal());

                        int nukeCount = 0;

                        if(hexA.getOwner() != Player.getNullPlayer() && hexA.getOwner() != game.getActivePlayer())
                        {
                            nukeCount++;
                        }

                        if(hexB.getOwner() != Player.getNullPlayer() && hexB.getOwner() != game.getActivePlayer())
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
                            if(hexIsTemplelessSettlementAdjacent(action.getTarget(), game) && !hexIsTempledSettlementAdjacent(action.getTarget(), game))
                            {
                                villagerPlacementsThatExpand.add(action);
                            }
                            if(hexEnablesTowerPlacement(action.getTarget(), game))
                            {
                                villagerPlacementsThatEnableTowers.add(action);
                            }
                            break;

                        case TOWER:
                            legalTowerPlacements.add(action);
                            break;

                        case TEMPLE:
                            legalTemplePlacements.add(action);
                            break;

                        default:
                            System.out.println("Undefined building type " + action.getBuilding());
                            break;
                    }
                }
            }
        }
    }

    public void updateSettlementExpansions(Game game)
    {
        legalSettlementExpansions = new HashSet<>();
        efficientExpansions = new HashSet<>();

        maxCost = 0;
        maxExpansion = null;

        for(Settlement settlement : game.getBoard().getSettlements())
        {
            for(Hex.Terrain terrain : Hex.Terrain.values())
            {
                if(terrain.buildable && settlement.getOwner() == game.getActivePlayer())
                {
                    Settlement.Expansion expansion = game.getBoard().getExpansion(settlement, terrain);
                    if(game.settlementExpansionIsLegal(expansion))
                    {
                        Game.SettlementExpansionAction action = game.getSettlementExpansionAction(expansion);
                        legalSettlementExpansions.add(action);

                        int cost = expansion.getCost();

                        if(cost > maxCost)
                        {
                            maxExpansion = action;
                        }

                        if(cost > 1 && ((float) expansion.getHexes().size()) / cost >= 1 && !settlement.hasTemple())
                        {
                            efficientExpansions.add(action);
                        }

                    }
                }
            }
        }
    }

    private boolean hexIsTemplelessSettlementAdjacent(Hex target, Game game)
    {
        for(Hex neighbor : game.getBoard().getNeighbors(target.getOrigin()))
        {
            Settlement settlement = neighbor.getSettlement();
            if(!settlement.hasTemple() && settlement.getOwner() == game.getActivePlayer())
            {
                return true;
            }
        }
        return false;
    }

    private boolean hexIsTempledSettlementAdjacent(Hex target, Game game)
    {
        for(Hex neighbor : game.getBoard().getNeighbors(target.getOrigin()))
        {
            Settlement settlement = neighbor.getSettlement();
            if(settlement.hasTemple() && settlement.getOwner() == game.getActivePlayer())
            {
                return true;
            }
        }
        return false;
    }

    private boolean hexEnablesTowerPlacement(Hex target, Game game)
    {
        for(Hex neighbor : game.getBoard().getNeighbors(target.getOrigin()))
        {
            if(neighbor.buildable() && neighbor.getLevel() >= 3)
            {
                return true;
            }
        }
        return false;
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
            //System.out.println("placing tower");
            for(Game.Action action : legalTowerPlacements) { return action; }
        }

        if(!legalTemplePlacements.isEmpty())
        {
            //System.out.println("placing temple");
            for(Game.Action action : legalTemplePlacements) { return action; }
        }

        if(!villagerPlacementsThatEnableTowers.isEmpty())
        {
            //System.out.println("placing village to enable tower");
            for(Game.Action action : villagerPlacementsThatEnableTowers) { return action; }
        }

        if(maxExpansion != null && (game.getActivePlayer().getTemples() == 0 || game.getActivePlayer().getTowers() == 0))
        {
            //System.out.println("performing max cost expansion to exhaust villagers");
            return maxExpansion;
        }

        if(!efficientExpansions.isEmpty())
        {
            //System.out.println("performing an efficient expansion");
            for(Game.Action action : efficientExpansions) { return action; }
        }

        if(!villagerPlacementsThatExpand.isEmpty())
        {
            //System.out.println("placing tower");
            for(Game.Action action : villagerPlacementsThatExpand) { return action; }
        }

        if(!legalVillagePlacements.isEmpty())
        {
            //System.out.println("placing village");
            for(Game.Action action : legalVillagePlacements) { return action; }
        }

        //if(!legalBuildingPlacements.isEmpty())
        //{
        //    for(Game.Action action : legalBuildingPlacements) { return action; }
        //}

        if(!legalSettlementExpansions.isEmpty())
        {
            //System.out.println("expanding settlement as a last resort");
            for(Game.Action action : legalSettlementExpansions) { return action; }
        }

        //System.out.println("No legal build actions found, " + game.getActivePlayer() + " forfeiting game");

        return game.getDeclareForfeitAction();
    }

}
