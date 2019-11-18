package com.androdocs.weatherapp;

import java.util.ArrayList;

/**
 * The main function of the app. Retrieves temperature and additional data
 * to output a clothing suggestion.
 *
 * @author Evan Ritchey
 * @author Lucas Leitz
 * @since 2019-11-08
 * @version 2.0
 *
 * Note:
 * rain_chance and humidity is a %
 * wind_speed is in mph
 * precipitation is windy/snow showers/cloudy/mostly cloudy/sunny etc.
 */
public class SuggestionCalculation {

    //holds the complete bank of clothes
    private ArrayList<String> allClothes = new ArrayList<>();

    public SuggestionCalculation() {
        allClothes.add("T-Shirt"); // > 60              0
        allClothes.add("Long Sleeve Shirt"); // <= 60   1
        allClothes.add("Shorts"); // >60                2
        allClothes.add("Pants"); // <= 60               3
        allClothes.add("Umbrella"); //Raining / Misty   4
        allClothes.add("Sunglasses"); //Sunny           5
        allClothes.add("Jacket"); //Snowing, <= 40      6
        allClothes.add("Sweatshirt"); //<= 50           7
        allClothes.add("Snow Cap"); //Snowing           8
        allClothes.add("Hat"); //Sunny                  9 As in "baseball cap"
        allClothes.add("Gloves"); // Snowing            10
        allClothes.add("Sweater"); //<=40               11
    }

    public String[] suggestion(int temp_int, String windSpeed, String weatherDescription){
                                    //^going to have to convert into int

        //temporarily stores the clothes that will be returned
        ArrayList<String> suggestion_list = new ArrayList<>();
        int suggestionSize = 0;

        //Various decisions based on weather data
        if(weatherDescription.equals("shower rain") || weatherDescription.equals("rain") || weatherDescription.equals("mist")){
            suggestion_list.add(allClothes.get(4)); //Adds Umbrella
            suggestionSize++;
        }

        if(weatherDescription.equals("clear skies")){
            suggestion_list.add(allClothes.get(5)); //Adds Sunglasses
            suggestion_list.add(allClothes.get(9)); //Adds Hat
            suggestionSize+= 2;
        }

        if(weatherDescription.equals("snow")){
            suggestion_list.add(allClothes.get(8)); //Adds Snow Cap
            suggestion_list.add(allClothes.get(10));//Adds Gloves
            suggestion_list.add(allClothes.get(6));//Adds Jacket
            suggestionSize+= 3;
        }

        if( !(weatherDescription.equals("snow")) && temp_int <= 40){
            suggestion_list.add(allClothes.get(5));//Adds Jacket if not snowing but still under 40
            suggestionSize++;
        }

        if(temp_int > 40 && temp_int <= 55){
            suggestion_list.add(allClothes.get(7));//Adds Sweatshirt
            suggestionSize++;
        }

        if(temp_int > 60){
            suggestion_list.add(allClothes.get(0)); //Adds T-Shirt if over 60
            suggestionSize++;
        }
        else if (temp_int > 40 && temp_int <= 60){
            suggestion_list.add(allClothes.get(1)); //Adds Long Sleeve Shirt if under or equal to 60 and greater than 40
            suggestionSize++;
        }
        else if(temp_int <= 40){
            suggestion_list.add(allClothes.get(11)); //adds sweater if greater than 40
            suggestionSize++;
        }

        if(temp_int > 70){
            suggestion_list.add(allClothes.get(2)); //Adds shorts if over 70
            suggestionSize++;
        }
        else{
            suggestion_list.add(allClothes.get(3)); //Adds pants if under or equal to 70
            suggestionSize++;
        }

        String[] finalSuggestionList = new String[suggestionSize];
        for(int i = 0; i < suggestionSize; i++){
            finalSuggestionList[i] = suggestion_list.get(i);
        }
        return finalSuggestionList;//List of clothes to suggest
    }
}