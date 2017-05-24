/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.restcrudmanager.workbench;

import info.magnolia.ui.workbench.column.AbstractColumnFormatter;
import info.magnolia.ui.workbench.column.definition.AbstractColumnDefinition;

import java.util.Date;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Table;

/**
 * A {@link info.magnolia.ui.workbench.column.definition.ColumnFormatter} for a
 * rendering a {@link com.tui.countriesanddestinations.item.CountryItem} and subclasses.
 *
 * @author isilanes
 */
public class ItemColumnFormatter extends AbstractColumnFormatter<ItemColumnDefinition> {

    /**
	 * serialization number
	 */
	private static final long serialVersionUID = 1L;

    public ItemColumnFormatter(ItemColumnDefinition definition) {
        super(definition);
    }

    @Override
    public Object generateCell(Table source, Object itemId, Object columnId) {
        Property property = null;
        String display = "";
        Item item = source.getItem(itemId);
        if (
        	(item != null)&&
        	(item.getItemProperty(columnId) != null)&&
        	(item.getItemProperty(columnId).getValue() != null)
        ){
            property = item.getItemProperty(columnId);
            Object propertyValue = property.getValue();
            if (property.getType().equals(Integer.class)) {
                int amount = (Integer) property.getValue();
                if (amount > 0) {
                    display = String.valueOf(propertyValue);
                }
            } else if (property.getType().equals(Date.class)) {
                display = propertyValue.toString();
            } else if (property.getType().equals(String.class)) {
                display = (String) propertyValue;
            } else {
                display = propertyValue.toString();
            }
        }
        boolean isNumber = property != null && property.getType().equals(Integer.class);
        String style = isNumber ? " style=\"text-align: right; display:block; width:100%;\"" : "";
        return "<span" + style + ">" + display + "</span>";
    }
}
