package com.example.tictactoe;

public class model_listitem {

    private String image;
    private String name;
    private String email;
    private int single_player_games_played;
    private int single_player_games_draw;
    private int two_player_games_played;
    private int two_player_games_draw;
    private int two_player_games_won;

    public model_listitem() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSingle_player_games_played() {
        return single_player_games_played;
    }

    public void setSingle_player_games_played(int single_player_games_played) {
        this.single_player_games_played = single_player_games_played;
    }

    public int getSingle_player_games_draw() {
        return single_player_games_draw;
    }

    public void setSingle_player_games_draw(int single_player_games_draw) {
        this.single_player_games_draw = single_player_games_draw;
    }

    public int getTwo_player_games_played() {
        return two_player_games_played;
    }

    public void setTwo_player_games_played(int two_player_games_played) {
        this.two_player_games_played = two_player_games_played;
    }

    public int getTwo_player_games_draw() {
        return two_player_games_draw;
    }

    public void setTwo_player_games_draw(int two_player_games_draw) {
        this.two_player_games_draw = two_player_games_draw;
    }

    public int getTwo_player_games_won() {
        return two_player_games_won;
    }

    public void setTwo_player_games_won(int two_player_games_won) {
        this.two_player_games_won = two_player_games_won;
    }
}
