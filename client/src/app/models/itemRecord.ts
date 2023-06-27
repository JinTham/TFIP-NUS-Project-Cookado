import { Item } from "./item"

export interface ItemRecord {
    updateDatetime:string
    action:string
    item:Item
}