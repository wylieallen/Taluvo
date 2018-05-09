# Taluvo

Taluvo is an adaptation of the board game *Taluva*. It's based on the *TigerIsland* term project from my Intro to Software Engineering at UF in Spring 2017, in which teams of students developed AI players and had them duke it out in a class tournament. (Our team's agent was one of the highest performers!) 

Taluvo takes place on a rapidly expanding, volcanically-active island populated by two, three, or four distinct tribes (each controlled by a player). Every turn, a player places a new tile somewhere on the board (representing new terrain formed by a volcanic eruption) and then places a token on the board (representing a structure inhabited by members of that player's tribe). A contiguous group of tokens belonging to the same player is known as a settlement, which is said to have a size equal to the number of hexes its tokens occupy. The objective of a player is to develop their settlements before their competitor tribes do.

## The Rules

A tile can be placed in an empty space (expanding the island outward) or on top of existing tiles (expanding the island upward and possibly destroying existing structures). A tile placed in an empty space must be adjacent to at least one existing tile (if any exist yet). A tile placed on top of existing tiles must satisfy several conditions: the new tile's volcano hex must overlap an existing tile's volcano hex, the new tile must overlap two or more existing tiles (i.e., it cannot be placed directly on top of another tile with the same orientation) all of which are at the same height, and the placement cannot result in the destruction of a Temple token, a Tower token, or a settlement in its entirety (i.e., if a settlement only has one Villager left, that Villager cannot be destroyed legally).   

The three types of tokens are Villagers, Temples, and Towers. Tokens can be placed on non-volcanic hexes that are not currently occupied by any other tokens. Each player starts with 20 Villagers, 2 Temples, and 3 Towers. A Villager token can be placed on any unoccupied non-volcanic hex of height 1, and it is possible to place Villager tokens on unoccupied non-volcanic hexes of heights greater than 1 using a settlement expansion move (described below). A Temple token can be placed next to an existing settlement with size 3 or greater (as long as that settlement does not already have a Temple) and a Tower token can be placed next to an existing settlement on a hex of height 3 or greater (again, as long as the adjacent settlement does not already have a tower). Finally, as a token placement move, a settlement can be expanded onto all like bordering terrains by placing one Villager per height level on each adjacent hex of the selected terrain. If a player is unable to legally place a token during their turn, that player is eliminated from the game.

The game ends if the deck of tiles is exhausted, if there is only one player remaining, or if any player exhausts two of the three of their token pools. If the deck of tiles is exhausted, the winner is determined by the number of tokens placed: whoever placed the most Temples wins, with ties broken by whoever placed the most Towers, with ties broken again by whoever placed the most Villagers. If a player places all the tokens they have of two of the three types, that player wins instantly.

## The GUI

![Taluvo In-Game Screenshot](https://puu.sh/Aj2AR/12d8bdfdf9.png)

The screenshot above shows the state of a concluded game. Red hexes are volcanos and white hexes are empty space in the playing area. Tile and token placement actions are performed by clicking on the desired hex.

On the left you can see a Settlements widget (which displays every existing settlement and its size) as well as buttons to rematch (restarting the game with the same player configuration) or to return to the main menu.

The widgets in the top-center and bottom-center display the players and their current token pools. Player Two is the winner, so that widget is highlighted in green.

The widget on the top right shows the next tile. The buttons below it can rotate the tile left or right for placement on the board. The widget below that shows details about the hex that is currently being moused over. The widget below that displays information about the state of the game: e.g., whether the game is over and whose turn it is. The buttons in the bottom right allow a player to select a token placement action. 
