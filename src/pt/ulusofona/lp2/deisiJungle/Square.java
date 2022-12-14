package pt.ulusofona.lp2.deisiJungle;

import com.sun.jdi.request.ClassPrepareRequest;

import java.util.ArrayList;

public class Square {

    private int number;
    private String type;
    private String image;
    private ArrayList<Player> players = new ArrayList<>();
    private Food food = null;


    public Square(){
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
        this.type = food.getTooltip();
        this.image = food.getImage();
    }

    public Square(int number, String type, String image, Food food) {
        this.number = number;
        this.type = type;
        this.image = image;
        this.food = food;
    }


    //already done

    public int getNumber(){
        return number;
    }

    public String getType(int turn){
        if(food!=null){
            if(food.getIdentifier()=='c'){
                Meat meat = (Meat)food;
                meat.updateTurn(turn);
            }
            return food.getTooltip();
        }
        return this.type;
    }

    public String getImage(){
        return image;
    }

    public ArrayList<Player> getPlayers(){
        return players;
    }

    public String getIdPlayersInString(){
        String result = "";

        for(int i=0; i<players.size(); i++){

            if(i== players.size()-1){
                result += ""+players.get(i).getId();
            }else{
                result+= ""+players.get(i).getId()+",";
            }
        }
        return result;
    }

    public boolean addPlayer(Player player){

        for(Player playertest : players){
            if(playertest.getId() == player.getId()){
                return true;
            }
        }
        players.add(player);
        return true;
    }

    public void removePlayer(Player player){
        players.remove(player);
    }


    //only for tests uses

    public void setPlayers(ArrayList<Player> players){
        players = this.players;
    }
}
