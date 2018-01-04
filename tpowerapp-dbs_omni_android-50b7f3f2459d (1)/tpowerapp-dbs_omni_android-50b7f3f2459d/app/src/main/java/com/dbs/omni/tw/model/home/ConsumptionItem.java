package com.dbs.omni.tw.model.home;

import com.dbs.omni.tw.typeMapping.ItemType;

/**
 * Created by siang on 2017/4/26.
 */

public class ConsumptionItem {

    private ItemType itemType;
    private String headerTitle;
    private ConsumptionData consumptionData;

    public ConsumptionItem(ItemType itemType, String headerTitle) {
        this.itemType = itemType;
        this.headerTitle = headerTitle;
    }

    public ConsumptionItem(ItemType itemType, ConsumptionData consumptionData) {
        this.itemType = itemType;
        this.consumptionData = consumptionData;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ConsumptionData getConsumptionData() {
        return consumptionData;
    }

    public void setConsumptionData(ConsumptionData consumptionData) {
        this.consumptionData = consumptionData;
    }
}
