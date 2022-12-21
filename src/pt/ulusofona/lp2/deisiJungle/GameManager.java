package pt.ulusofona.lp2.deisiJungle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;


public class GameManager {

    //SPECIES
    Specie elephant = new Elephant();
    Specie lion = new Lion();
    Specie tarzan = new Tarzan();
    Specie turtle = new Turtle();
    Specie bird = new Bird();

    ArrayList<Specie> species = new ArrayList<>()
    {
        {
            add(elephant);
            add(lion);
            add(turtle);
            add(bird);
            add(tarzan);

        }
    };

    //PLAYERS
    ArrayList<Player> players = new ArrayList<>();
    HashMap<Integer, Player> playersById = new HashMap<>();

    //OTHERS
    Map map;
    int turn;




    //MANDITORY FUNCTIONS





    public String[][] stringsgetFoodTypes(){
        return null;
    }



    public String[][] getSpecies(){
        String[][] result = new String[species.size()][7];



        int cnt = 0;
        for (Specie specie : species) {
            result[cnt][0] = specie.getIdentifier()+"";
            result[cnt][1] = specie.getName();
            result[cnt][2] = specie.getImage();
            result[cnt][3] = specie.getInitialEnergy()+"";
            result[cnt][4] = specie.getEnergyConsume()+"";
            result[cnt][5] = specie.getEnrgyEarnedByRest()+"";
            result[cnt][6] = specie.getSpeed()[0] + ".." + specie.getSpeed()[1];
            cnt++;
        }
        return result;
    }
    //DONE


    public boolean createInitialJungle(int jungleSize, int initialEnergy, String[][] playersInfo){

        players = new ArrayList<>();
        playersById = new HashMap<>();

        //O número de jogadores.
        if(playersInfo.length < 2 || playersInfo.length > 4){
            return false;
        }

        //O mapa tem de ter, pelo menos, duas posições por cada jogador que esteja em jogo.
        if (playersInfo.length*2 > jungleSize){
            return false;
        }

        ArrayList<Integer> playerIds = new ArrayList<>();

        for(int i=0; i<playersInfo.length; i++){

            try{
                if(!playerIds.contains(Integer.parseInt(playersInfo[i][0]))){
                    playerIds.add(Integer.parseInt(playersInfo[i][0]));
                }else {
                    return false;
                }
            }catch (NumberFormatException e){
                return false;
            }

            //Verifica se a espécie existe
            boolean validSpecie=false;

            for(Specie specie : species){
                if(specie.getIdentifier() == playersInfo[i][2].charAt(0)){
                    validSpecie=true;
                    break;
                }
            }

            if(!validSpecie){
                return false;
            }

            //Verifica se o nome e valido
            if (playersInfo[i][1] == null || playersInfo[i][1].equals("")) {
                return false;
            }

            //COMO JA PASSOU AS VALIDAÇOES COLOCA AS VARIAVEIS
            int id=Integer.parseInt(playersInfo[i][0]);
            String name=playersInfo[i][1];
            char specieIdentifier=playersInfo[i][2].charAt(0);

            players.add(new Player(id,initialEnergy,getSpicieById(specieIdentifier),name));
            playersById.put(id, new Player(id,initialEnergy,getSpicieById(specieIdentifier),name));

        }
        map = new Map(jungleSize);
        sortPlayersById(players);

        //System.out.println(players);

        //Preenche a 1 casa
        for(Player player : players){
            map.getSquare(1).addPlayer(player);
        }

        return true;
    }
    //ON GOING


    public int[] getPlayerIds(int squareNr){

        if(!map.isSquareValid(squareNr)){
            return new int[]{};
        }

        ArrayList<Player> playersInSquare = map.getSquare(squareNr).getPlayers();

        int[] playerIDs = new int[playersInSquare.size()];

        if(playersInSquare.size()==0){
            return new int[]{};
        }

        for(int i=0; i<playersInSquare.size(); i++){
            playerIDs[i]=playersInSquare.get(i).getId();
        }

        return playerIDs;
    }
    //DONE



    public String[] getSquareInfo(int squareNr){

        if(!map.isSquareValid(squareNr)){
            return null;
        }

        Square square = map.getSquare(squareNr);
        String[] squareInfo = new String[3];

        squareInfo[0] = square.getImage();
        squareInfo[1] = square.getType();
        squareInfo[2] = square.getIdPlayersInString();

        return squareInfo;
    }
    //DONE


    public String[] getPlayerInfo(int playerId){

        String[] playerInfo = new String[4];

        playerInfo[0] = playersById.get(playerId).getId()+"";
        playerInfo[1] = playersById.get(playerId).getName();
        playerInfo[2] = playersById.get(playerId).getSpecie().getIdentifier()+"";
        playerInfo[3] = playersById.get(playerId).getEnergy()+"";

        return playerInfo;
    }
    //DONE


    public String[] getCurrentPlayerInfo(){

        String[] playerInfo = new String[4];

        playerInfo[0] = players.get(turn).getId()+"";
        playerInfo[1] = players.get(turn).getName();
        playerInfo[2] = players.get(turn).getSpecie().getIdentifier()+"";
        playerInfo[3] = players.get(turn).getEnergy()+"";

        return playerInfo;
    }
    //ON GOING


    public String[][] getPlayersInfo(){

        String[][] playersInfo = new String[players.size()][4];

        for(int i=0; i< players.size();i++){

            playersInfo[i][0] = players.get(i).getId()+"";
            playersInfo[i][1] = players.get(i).getName();
            playersInfo[i][2] = players.get(i).getSpecie().getIdentifier()+"";
            playersInfo[i][3] = players.get(i).getEnergy()+"";
        }

        return playersInfo;
    }
    //DONE

    public boolean moveCurrentPlayer(int nrSquares, boolean bypassValidations){

        Player currentPlayer = players.get(turn);
        Square initialSquare = map.getSquare(currentPlayer.getPosition());


        if(!bypassValidations){
            if(nrSquares<1 || nrSquares>6){
                nextTurn();
                return false;
            }
        }

        if(currentPlayer.getEnergy()<2){
            nextTurn();
            return false;
        }

        if(currentPlayer.getPosition()+nrSquares >= map.getSize()){
            Square desiredSquare = map.getSquare(map.getSize());

            initialSquare.removePlayer(currentPlayer);
            desiredSquare.addPlayer(currentPlayer);
            currentPlayer.removeEnergy();
            currentPlayer.updatePosition(nrSquares);

            nextTurn();
            return true;
        }

        /*
        //verifica se passa no square do meio onde é adicionado 6 de energia equivalente a 3 moves
        if(!(currentPlayer.getPosition()>= map.getSize()/2+1)){
            if(passedMiddleSquare(currentPlayer.getPosition()+nrSquares)){
                currentPlayer.addEnergy(6);
            }
        }*/

        Square desiredSquare = map.getSquare(currentPlayer.getPosition()+nrSquares);

        initialSquare.removePlayer(currentPlayer);
        desiredSquare.addPlayer(currentPlayer);
        currentPlayer.removeEnergy();
        currentPlayer.updatePosition(nrSquares);

        nextTurn();
        return true;

    }

    public String[] getWinnerInfo(){

        if(!checkIfGameEnded()){
            return null;
        }

        sortPlayersByPocision();

        int nbrPlayersSamePosition = 0;
        int positionOfFitstPlace = players.get(0).getPosition();

        for(int i = 1; i < players.size(); i++){
            if(players.get(i).getPosition() == positionOfFitstPlace){
                nbrPlayersSamePosition++;
            }
        }

        ArrayList<Player> helper = new ArrayList<>();

        if(nbrPlayersSamePosition >= 1){
            for(int x = 0 ; x <  nbrPlayersSamePosition; x++){
                helper.add(players.get(x));
            }
            sortPlayersById(helper);

            return getPlayerInfo(helper.get(0).getId());
        }
        return getPlayerInfo(players.get(0).getId());
    }

    public ArrayList<String> getGameResults(){

        ArrayList<String> result =  new ArrayList<>();

        int counter = 1;


        for(int i = map.getSize() ; i >= 1; i--){

            if(map.getSquare(i).getPlayers().size() > 1){
                sortPlayersById(map.getSquare(i).getPlayers());

                for(Player player : map.getSquare(i).getPlayers()){
                    result.add("#"+counter + " " + player.getName()+ ", " + player.getSpecie().getName() + ", " + player.getPosition());
                    counter++;
                }
            }
            if(map.getSquare(i).getPlayers().size() == 1){
                Player player = map.getSquare(i).getPlayers().get(0);
                result.add("#"+counter + " " + player.getName()+ ", " + player.getSpecie().getName() + ", " + player.getPosition());
                counter++;
            }
        }

        return result;
    }

    public JPanel getAuthorsPanel(){

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(300,300));

        JTextArea text = new JTextArea("ola o meu nome e pedro");

        panel.setBackground(Color.BLACK);

        JButton buttom = new JButton("Teste");
        //panel.add(buttom);
        panel.add(text);

        return panel;
    }

    public String whoIsTaborda(){
        return "Wrestling";
    }
    //DONE


    //ADDITIONAL FUNCTIONS

    Specie getSpicieById(char id){
        for(Specie specie : species){
            if(specie.getIdentifier() == id){
                return specie;
            }
        }
        return null;
    }

    ArrayList<Player> sortPlayersById(ArrayList<Player> players){
        for (int i = 0; i < players.size(); i++) {

            // Inner nested loop pointing 1 index ahead
            for (int j = i + 1; j < players.size(); j++) {

                // Checking elements
                Player temp;
                if (players.get(j).getId() < players.get(i).getId()) {

                    // Swapping
                    temp = players.get(i);
                    players.set(i, players.get(j));
                    players.set(j, temp);
                }
            }
        }
        return players;
    }


    void nextTurn(){
        if(turn == players.size()-1){
            turn = 0;
            //System.out.println(turn);
        }else {
            turn++;
        }

        //System.out.println(turn);
    }




    boolean checkIfGameEnded(){

        //o jogo acaba se todos estiverem sem energia

        boolean resultA = true;
        boolean resultB = true;

        for(Player player : players){
            if(player.getEnergy() >= 2){
                resultA = false;
            }
        }

        // chek if someone ios on the last
        if(map.getSquare(map.getSize()).getPlayers().size() < 1){
            resultB = false;
        }

        return resultA || resultB;
    }

    void sortPlayersByPocision(){
        //https://www.geeksforgeeks.org/sorting-in-java/--
        for (int i = 0; i < players.size(); i++) {

            // Inner nested loop pointing 1 index ahead
            for (int j = i + 1; j < players.size(); j++) {

                // Checking elements
                Player temp;
                if (players.get(j).getPosition() > players.get(i).getPosition()) {

                    // Swapping
                    temp = players.get(i);
                    players.set(i, players.get(j));
                    players.set(j, temp);
                }
            }
        }
    }

    boolean passedMiddleSquare(int position){
        return position >= map.getSize() / 2 + 1;
    }

}