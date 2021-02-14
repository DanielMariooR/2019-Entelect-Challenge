package za.co.entelect.challenge.entities;

import com.google.gson.annotations.SerializedName;

public class MyWorm extends Worm {

    @SerializedName("bananaBombs")
    public BananaBombs bananaBombs;

    @SerializedName("snowballs")
    public Snowballs snowballs;
}
