package com.codepath.nytimessearch;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mfdavis on 6/23/16.
 */
public class SearchFilters implements Serializable {

    public ArrayList newsDeskItems = new ArrayList<String>();
    public String sort = "";
    public String beginDate = "";

    public ArrayList getNewsDeskItems () {
        return newsDeskItems;
    }

    public void setNewsDeskItems(ArrayList newsDeskItems) {
        this.newsDeskItems = newsDeskItems;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public SearchFilters() {
    }


}
