import { Ingredient } from "./ingredient"

export class Recipe {
    recipeId:string
    recipeTitle:string
    author:string
    readyInMinutes:number
    image:string | undefined
    summary:string
    cuisine:string
    instructions:string
    extendedIngredients:Ingredient[]

    constructor(data: {
        recipeId:string
        recipeTitle: string;
        author:string
        readyInMinutes: number;
        image: string;
        summary: string;
        cuisine: string;
        instructions: string;
        extendedIngredients: any[];
      }) {
        this.recipeId = data.recipeId
        this.recipeTitle = data.recipeTitle;
        this.author = data.author;
        this.readyInMinutes = data.readyInMinutes;
        this.image = data.image;
        this.summary = data.summary;
        this.cuisine = data.cuisine;
        this.instructions = data.instructions;
        this.extendedIngredients = data.extendedIngredients;
      }
}